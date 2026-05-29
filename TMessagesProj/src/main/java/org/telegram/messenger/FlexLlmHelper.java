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

    private static final String ANTHROPIC_VERSION = "2023-06-01";
    private static final int ANTHROPIC_MAX_TOKENS = 8192;

    public static void requestText(String apiUrl, int endpointType, String apiKey, String model, String systemPrompt, String userPrompt, double temperature, boolean stream, Utilities.Callback2<String, String> done) {
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
                connection = (HttpURLConnection) new URI(buildEndpointUrl(apiUrl, endpointType)).toURL().openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(stream ? 120000 : 60000);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                applyAuthHeaders(connection, endpointType, apiKey);
                if (stream) {
                    connection.setRequestProperty("Accept", "text/event-stream");
                }

                String body = buildRequestBody(endpointType, model, systemPrompt, userPrompt, temperature, stream).toString();
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(body.getBytes(Charsets.UTF_8));
                }

                String result;
                if (stream) {
                    result = readStreamText(connection, endpointType);
                } else {
                    responseText = readConnectionText(connection, false);
                    result = extractResponseText(endpointType, new JSONObject(responseText));
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

    public static void requestModels(String apiUrl, int endpointType, String apiKey, Utilities.Callback2<ArrayList<String>, String> done) {
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
                applyAuthHeaders(connection, endpointType, apiKey);

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

    private static void applyAuthHeaders(HttpURLConnection connection, int endpointType, String apiKey) {
        if (TextUtils.isEmpty(apiKey)) {
            return;
        }
        if (endpointType == FlexConfig.LLM_ENDPOINT_ANTHROPIC) {
            connection.setRequestProperty("x-api-key", apiKey);
            connection.setRequestProperty("anthropic-version", ANTHROPIC_VERSION);
        } else {
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        }
    }

    private static JSONObject buildRequestBody(int endpointType, String model, String systemPrompt, String userPrompt, double temperature, boolean stream) throws Exception {
        JSONObject request = new JSONObject();
        request.put("model", model);
        if (endpointType == FlexConfig.LLM_ENDPOINT_ANTHROPIC) {
            request.put("max_tokens", ANTHROPIC_MAX_TOKENS);
            request.put("temperature", temperature);
            if (!TextUtils.isEmpty(systemPrompt)) {
                request.put("system", systemPrompt);
            }
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "user").put("content", userPrompt));
            request.put("messages", messages);
        } else if (endpointType == FlexConfig.LLM_ENDPOINT_RESPONSES) {
            request.put("temperature", temperature);
            if (!TextUtils.isEmpty(systemPrompt)) {
                request.put("instructions", systemPrompt);
            }
            request.put("input", userPrompt);
        } else {
            request.put("temperature", temperature);
            JSONArray messages = new JSONArray();
            if (!TextUtils.isEmpty(systemPrompt)) {
                messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));
            }
            messages.put(new JSONObject().put("role", "user").put("content", userPrompt));
            request.put("messages", messages);
        }
        if (stream) {
            request.put("stream", true);
        }
        return request;
    }

    private static String extractResponseText(int endpointType, JSONObject root) {
        if (endpointType == FlexConfig.LLM_ENDPOINT_ANTHROPIC) {
            return extractContentBlocks(root.optJSONArray("content"));
        }
        if (endpointType == FlexConfig.LLM_ENDPOINT_RESPONSES) {
            String direct = root.optString("output_text", null);
            if (!TextUtils.isEmpty(direct)) {
                return direct.trim();
            }
            JSONArray output = root.optJSONArray("output");
            StringBuilder builder = new StringBuilder();
            if (output != null) {
                for (int i = 0; i < output.length(); ++i) {
                    JSONObject item = output.optJSONObject(i);
                    if (item != null) {
                        builder.append(extractContentBlocks(item.optJSONArray("content")));
                    }
                }
            }
            return builder.toString().trim();
        }
        JSONArray choices = root.optJSONArray("choices");
        if (choices == null || choices.length() <= 0) {
            return null;
        }
        JSONObject choice = choices.optJSONObject(0);
        if (choice == null) {
            return null;
        }
        String result = extractMessageContent(choice.optJSONObject("message"));
        if (TextUtils.isEmpty(result)) {
            result = choice.optString("text", null);
        }
        return result;
    }

    private static String extractContentBlocks(JSONArray content) {
        if (content == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < content.length(); ++i) {
            JSONObject block = content.optJSONObject(i);
            if (block != null) {
                builder.append(block.optString("text", ""));
            }
        }
        return builder.toString();
    }

    private static String readStreamText(HttpURLConnection connection, int endpointType) throws IOException {
        InputStream stream = connection.getInputStream();
        if (stream == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("data:")) {
                    continue;
                }
                String payload = line.substring(5).trim();
                if (payload.isEmpty() || "[DONE]".equals(payload)) {
                    continue;
                }
                result.append(extractStreamDelta(endpointType, new JSONObject(payload)));
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return result.toString();
    }

    private static String extractStreamDelta(int endpointType, JSONObject event) {
        if (endpointType == FlexConfig.LLM_ENDPOINT_ANTHROPIC) {
            JSONObject delta = event.optJSONObject("delta");
            return delta != null ? delta.optString("text", "") : "";
        }
        if (endpointType == FlexConfig.LLM_ENDPOINT_RESPONSES) {
            return event.optString("type", "").endsWith("output_text.delta") ? event.optString("delta", "") : "";
        }
        JSONArray choices = event.optJSONArray("choices");
        if (choices == null || choices.length() <= 0) {
            return "";
        }
        JSONObject delta = choices.optJSONObject(0) != null ? choices.optJSONObject(0).optJSONObject("delta") : null;
        return delta != null ? delta.optString("content", "") : "";
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

    private static String normalizeBaseUrl(String apiUrl) {
        String value = apiUrl.trim();
        int queryIndex = value.indexOf('?');
        if (queryIndex >= 0) {
            value = value.substring(0, queryIndex);
        }
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        if (!value.contains("://")) {
            value = "https://" + value;
        }
        String[] suffixes = {"/chat/completions", "/completions", "/responses", "/messages", "/models"};
        for (int i = 0; i < suffixes.length; ++i) {
            if (value.endsWith(suffixes[i])) {
                value = value.substring(0, value.length() - suffixes[i].length());
                break;
            }
        }
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        int slash = value.lastIndexOf('/');
        String lastSegment = slash >= 0 ? value.substring(slash + 1) : value;
        if (!lastSegment.matches("v\\d+")) {
            value = value + "/v1";
        }
        return value;
    }

    private static String buildEndpointUrl(String apiUrl, int endpointType) {
        String base = normalizeBaseUrl(apiUrl);
        if (endpointType == FlexConfig.LLM_ENDPOINT_ANTHROPIC) {
            return base + "/messages";
        }
        if (endpointType == FlexConfig.LLM_ENDPOINT_RESPONSES) {
            return base + "/responses";
        }
        return base + "/chat/completions";
    }

    private static String getModelsApiUrl(String apiUrl) {
        return normalizeBaseUrl(apiUrl) + "/models";
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
