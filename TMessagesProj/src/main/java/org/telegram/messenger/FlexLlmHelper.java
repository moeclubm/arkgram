package org.telegram.messenger;

import android.text.TextUtils;

import com.google.common.base.Charsets;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;

public class FlexLlmHelper {

    public static void requestText(String apiUrl, String apiKey, String model, String systemPrompt, String userPrompt, double temperature, Utilities.Callback2<String, String> done) {
        if (done == null) {
            return;
        }
        if (TextUtils.isEmpty(apiUrl)) {
            AndroidUtilities.runOnUIThread(() -> done.run(null, LocaleController.getString(R.string.FlexLlmApiUrlMissing)));
            return;
        }
        if (TextUtils.isEmpty(model)) {
            AndroidUtilities.runOnUIThread(() -> done.run(null, LocaleController.getString(R.string.FlexLlmModelMissing)));
            return;
        }
        new Thread(() -> {
            HttpURLConnection connection = null;
            String responseText = null;
            try {
                connection = (HttpURLConnection) new URI(apiUrl).toURL().openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                if (!TextUtils.isEmpty(apiKey)) {
                    connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                }

                JSONObject request = new JSONObject();
                request.put("model", model);
                request.put("temperature", temperature);
                JSONArray messages = new JSONArray();
                if (!TextUtils.isEmpty(systemPrompt)) {
                    messages.put(new JSONObject()
                        .put("role", "system")
                        .put("content", systemPrompt));
                }
                messages.put(new JSONObject()
                    .put("role", "user")
                    .put("content", userPrompt));
                request.put("messages", messages);

                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(request.toString().getBytes(Charsets.UTF_8));
                }

                responseText = readConnectionText(connection, false);
                JSONArray choices = new JSONObject(responseText).optJSONArray("choices");
                if (choices == null || choices.length() <= 0) {
                    throw new IllegalStateException(LocaleController.getString(R.string.FlexLlmInvalidResponse));
                }
                JSONObject choice = choices.optJSONObject(0);
                String result = null;
                if (choice != null) {
                    result = extractMessageContent(choice.optJSONObject("message"));
                    if (TextUtils.isEmpty(result)) {
                        result = choice.optString("text", null);
                    }
                }
                if (TextUtils.isEmpty(result)) {
                    throw new IllegalStateException(LocaleController.getString(R.string.FlexLlmInvalidResponse));
                }
                String finalResult = result.trim();
                AndroidUtilities.runOnUIThread(() -> done.run(finalResult, null));
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                if (TextUtils.isEmpty(errorMessage) || errorMessage.startsWith("org.json.")) {
                    try {
                        responseText = readConnectionText(connection, true);
                    } catch (Exception ignore) {
                    }
                    errorMessage = getErrorMessage(connection, responseText);
                }
                FileLog.e(e);
                String finalErrorMessage = errorMessage;
                AndroidUtilities.runOnUIThread(() -> done.run(null, finalErrorMessage));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }, "FlexLlmRequest").start();
    }

    public static void requestModels(String apiUrl, String apiKey, Utilities.Callback2<ArrayList<String>, String> done) {
        if (done == null) {
            return;
        }
        if (TextUtils.isEmpty(apiUrl)) {
            AndroidUtilities.runOnUIThread(() -> done.run(null, LocaleController.getString(R.string.FlexLlmApiUrlMissing)));
            return;
        }
        new Thread(() -> {
            HttpURLConnection connection = null;
            String responseText = null;
            try {
                connection = (HttpURLConnection) new URI(getModelsApiUrl(apiUrl)).toURL().openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(30000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                if (!TextUtils.isEmpty(apiKey)) {
                    connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                }

                responseText = readConnectionText(connection, false);
                JSONArray data = new JSONObject(responseText).optJSONArray("data");
                if (data == null) {
                    throw new IllegalStateException(LocaleController.getString(R.string.FlexLlmInvalidResponse));
                }
                ArrayList<String> models = new ArrayList<>();
                for (int i = 0; i < data.length(); ++i) {
                    JSONObject model = data.optJSONObject(i);
                    String id = model != null ? model.optString("id", "").trim() : "";
                    if (!TextUtils.isEmpty(id) && !models.contains(id)) {
                        models.add(id);
                    }
                }
                if (models.isEmpty()) {
                    throw new IllegalStateException(LocaleController.getString(R.string.FlexLlmInvalidResponse));
                }
                AndroidUtilities.runOnUIThread(() -> done.run(models, null));
            } catch (Exception e) {
                String errorMessage = e.getMessage();
                if (TextUtils.isEmpty(errorMessage) || errorMessage.startsWith("org.json.")) {
                    try {
                        responseText = readConnectionText(connection, true);
                    } catch (Exception ignore) {
                    }
                    errorMessage = getErrorMessage(connection, responseText);
                }
                FileLog.e(e);
                String finalErrorMessage = errorMessage;
                AndroidUtilities.runOnUIThread(() -> done.run(null, finalErrorMessage));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }, "FlexLlmModelsRequest").start();
    }

    private static String readConnectionText(HttpURLConnection connection, boolean errorStream) throws IOException {
        if (connection == null) {
            return null;
        }
        InputStream stream = errorStream ? connection.getErrorStream() : connection.getInputStream();
        if (stream == null) {
            return null;
        }
        Reader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
            return builder.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static String getErrorMessage(HttpURLConnection connection, String responseText) {
        try {
            if (!TextUtils.isEmpty(responseText)) {
                JSONObject object = new JSONObject(responseText);
                JSONObject error = object.optJSONObject("error");
                if (error != null) {
                    String message = error.optString("message", null);
                    if (!TextUtils.isEmpty(message)) {
                        return message;
                    }
                }
                String message = object.optString("message", null);
                if (!TextUtils.isEmpty(message)) {
                    return message;
                }
            }
        } catch (Exception ignore) {
        }
        try {
            int responseCode = connection != null ? connection.getResponseCode() : 0;
            if (responseCode > 0) {
                return LocaleController.getString(R.string.FlexLlmRequestFailed) + " (" + responseCode + ")";
            }
        } catch (Exception ignore) {
        }
        return LocaleController.getString(R.string.FlexLlmRequestFailed);
    }

    private static String getModelsApiUrl(String apiUrl) {
        String value = apiUrl.trim();
        int queryIndex = value.indexOf('?');
        if (queryIndex >= 0) {
            value = value.substring(0, queryIndex);
        }
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        if (value.endsWith("/chat/completions")) {
            return value.substring(0, value.length() - "/chat/completions".length()) + "/models";
        }
        if (value.endsWith("/completions")) {
            return value.substring(0, value.length() - "/completions".length()) + "/models";
        }
        return value + "/models";
    }

    private static String extractMessageContent(JSONObject message) {
        if (message == null) {
            return null;
        }
        Object content = message.opt("content");
        if (content instanceof String) {
            return ((String) content).trim();
        }
        if (!(content instanceof JSONArray)) {
            return null;
        }
        JSONArray contentArray = (JSONArray) content;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < contentArray.length(); ++i) {
            Object block = contentArray.opt(i);
            if (block instanceof String) {
                builder.append((String) block);
                continue;
            }
            if (!(block instanceof JSONObject)) {
                continue;
            }
            JSONObject object = (JSONObject) block;
            Object text = object.opt("text");
            if (text instanceof String) {
                builder.append((String) text);
            } else if (text instanceof JSONObject) {
                builder.append(((JSONObject) text).optString("value"));
            }
        }
        String result = builder.toString().trim();
        return result.isEmpty() ? null : result;
    }
}
