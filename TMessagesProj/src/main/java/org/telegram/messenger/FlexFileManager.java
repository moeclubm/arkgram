package org.telegram.messenger;

import android.content.ContentResolver;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TranslateAlert2;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;

public class FlexFileManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void exportSettings(Uri uri, int currentAccount) throws Exception {
        ContentResolver resolver = ApplicationLoader.applicationContext.getContentResolver();
        OutputStream outputStream = resolver.openOutputStream(uri, "wt");
        if (outputStream == null) {
            throw new IllegalStateException(LocaleController.getString(R.string.FlexFileUnavailable));
        }

        TranslateController translateController = MessagesController.getInstance(currentAccount).getTranslateController();
        LinkedHashMap<String, Object> root = new LinkedHashMap<>();
        LinkedHashMap<String, Object> global = new LinkedHashMap<>();
        LinkedHashMap<String, Object> account = new LinkedHashMap<>();

        root.put("version", 2);
        root.put("exported_at", System.currentTimeMillis());
        root.put("current_account", currentAccount);
        root.put("global", global);
        root.put("account", account);

        global.put("download_speed_boost", FlexConfig.getDownloadSpeedBoost());
        global.put("disable_webrtc", FlexConfig.isWebRtcDisabled());
        global.put("show_dc_info", FlexConfig.isDcInfoEnabled());
        global.put("hide_main_tabs", FlexConfig.isMainTabsHidden());
        global.put("disable_ui_transparency", FlexConfig.isUiTransparencyDisabled());
        global.put("disable_ui_blur", FlexConfig.isUiBlurDisabled());
        global.put("disable_markdown", FlexConfig.isMarkdownDisabled());
        global.put("new_markdown_parser", FlexConfig.isNewMarkdownParserEnabled());
        global.put("markdown_parse_links", FlexConfig.isMarkdownParseLinksEnabled());
        global.put("translation_provider", FlexConfig.getTranslationProvider());
        global.put("translation_target_language", TranslateAlert2.getToLanguage());
        global.put("translation_deepl_api_url", FlexConfig.getDeepLApiUrl());
        global.put("translation_deepl_api_key", FlexConfig.getDeepLApiKey());
        global.put("translation_llm_provider", FlexConfig.getTranslationLlmProvider());
        global.put("translation_llm_api_url", FlexConfig.getStoredLlmApiUrl());
        global.put("translation_llm_api_key", FlexConfig.getLlmApiKey());
        global.put("translation_llm_model", FlexConfig.getLlmModel());
        global.put("translation_llm_prompt", FlexConfig.getLlmPrompt());
        global.put("opencc_auto_conversion", FlexConfig.isOpenCCAutoConversionEnabled());
        global.put("opencc_conversion", FlexConfig.getOpenCCConversion());
        global.put("ai_summary_llm_provider", FlexConfig.getAiSummaryLlmProvider());
        global.put("ai_summary_llm_api_url", FlexConfig.getStoredAiSummaryLlmApiUrl());
        global.put("ai_summary_llm_api_key", FlexConfig.getAiSummaryLlmApiKey());
        global.put("ai_summary_llm_model", FlexConfig.getAiSummaryLlmModel());
        global.put("ai_summary_llm_prompt", FlexConfig.getAiSummaryLlmPrompt());

        account.put("translate_button", translateController.isContextTranslateEnabled());
        account.put("translate_chat_button", translateController.isChatTranslateEnabled());

        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            GSON.toJson(root, writer);
        }
    }

    public static void importSettings(Uri uri, int currentAccount) throws Exception {
        ContentResolver resolver = ApplicationLoader.applicationContext.getContentResolver();
        InputStream inputStream = resolver.openInputStream(uri);
        if (inputStream == null) {
            throw new IllegalStateException(LocaleController.getString(R.string.FlexFileUnavailable));
        }

        JsonObject root;
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            root = JsonParser.parseReader(reader).getAsJsonObject();
        }

        JsonObject global = root.has("global") && root.get("global").isJsonObject() ? root.getAsJsonObject("global") : root;
        if (global.has("download_speed_boost")) {
            FlexConfig.setDownloadSpeedBoost(global.get("download_speed_boost").getAsInt());
        }
        if (global.has("disable_webrtc")) {
            FlexConfig.setWebRtcDisabled(global.get("disable_webrtc").getAsBoolean());
        }
        if (global.has("show_dc_info")) {
            FlexConfig.setDcInfoEnabled(global.get("show_dc_info").getAsBoolean());
        }
        if (global.has("hide_main_tabs")) {
            FlexConfig.setMainTabsHidden(global.get("hide_main_tabs").getAsBoolean());
        }
        if (global.has("disable_ui_transparency")) {
            FlexConfig.setUiTransparencyDisabled(global.get("disable_ui_transparency").getAsBoolean());
        }
        if (global.has("disable_ui_blur")) {
            FlexConfig.setUiBlurDisabled(global.get("disable_ui_blur").getAsBoolean());
        }
        if (global.has("disable_markdown")) {
            FlexConfig.setMarkdownDisabled(global.get("disable_markdown").getAsBoolean());
        }
        if (global.has("new_markdown_parser")) {
            FlexConfig.setNewMarkdownParserEnabled(global.get("new_markdown_parser").getAsBoolean());
        }
        if (global.has("markdown_parse_links")) {
            FlexConfig.setMarkdownParseLinksEnabled(global.get("markdown_parse_links").getAsBoolean());
        }
        if (global.has("translation_provider")) {
            FlexConfig.setTranslationProvider(global.get("translation_provider").getAsInt());
        }
        if (global.has("translation_target_language")) {
            TranslateAlert2.setToLanguage(global.get("translation_target_language").getAsString());
        }
        if (global.has("translation_deepl_api_url")) {
            FlexConfig.setDeepLApiUrl(global.get("translation_deepl_api_url").getAsString());
        }
        if (global.has("translation_deepl_api_key")) {
            FlexConfig.setDeepLApiKey(global.get("translation_deepl_api_key").getAsString());
        }
        if (global.has("translation_llm_provider")) {
            FlexConfig.setTranslationLlmProvider(global.get("translation_llm_provider").getAsInt());
        }
        if (global.has("translation_llm_api_url")) {
            FlexConfig.setLlmApiUrl(global.get("translation_llm_api_url").getAsString());
        }
        if (global.has("translation_llm_api_key")) {
            FlexConfig.setLlmApiKey(global.get("translation_llm_api_key").getAsString());
        }
        if (global.has("translation_llm_model")) {
            FlexConfig.setLlmModel(global.get("translation_llm_model").getAsString());
        }
        if (global.has("translation_llm_prompt")) {
            FlexConfig.setLlmPrompt(global.get("translation_llm_prompt").getAsString());
        }
        if (global.has("opencc_auto_conversion")) {
            FlexConfig.setOpenCCAutoConversionEnabled(global.get("opencc_auto_conversion").getAsBoolean());
        }
        if (global.has("opencc_conversion")) {
            FlexConfig.setOpenCCConversion(global.get("opencc_conversion").getAsString());
        }
        if (global.has("ai_summary_llm_provider")) {
            FlexConfig.setAiSummaryLlmProvider(global.get("ai_summary_llm_provider").getAsInt());
        }
        if (global.has("ai_summary_llm_api_url")) {
            FlexConfig.setAiSummaryLlmApiUrl(global.get("ai_summary_llm_api_url").getAsString());
        }
        if (global.has("ai_summary_llm_api_key")) {
            FlexConfig.setAiSummaryLlmApiKey(global.get("ai_summary_llm_api_key").getAsString());
        }
        if (global.has("ai_summary_llm_model")) {
            FlexConfig.setAiSummaryLlmModel(global.get("ai_summary_llm_model").getAsString());
        }
        if (global.has("ai_summary_llm_prompt")) {
            FlexConfig.setAiSummaryLlmPrompt(global.get("ai_summary_llm_prompt").getAsString());
        }

        JsonObject account = root.has("account") && root.get("account").isJsonObject() ? root.getAsJsonObject("account") : root;
        TranslateController translateController = MessagesController.getInstance(currentAccount).getTranslateController();
        if (account.has("translate_button")) {
            translateController.setContextTranslateEnabled(account.get("translate_button").getAsBoolean());
        }
        if (account.has("translate_chat_button")) {
            translateController.setChatTranslateEnabled(account.get("translate_chat_button").getAsBoolean());
        }

        Theme.refreshThemeColors();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.mainTabsVisibilityToggled);
    }
}
