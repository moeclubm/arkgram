package org.telegram.messenger;

import android.graphics.Color;
import android.content.SharedPreferences;

import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class FlexConfig {

    public static final int BOOST_NONE = 0;
    public static final int BOOST_AVERAGE = 1;
    public static final int BOOST_EXTREME = 2;
    public static final int VIDEO_QUALITY_DEFAULT_HIGHEST = -2;
    public static final int VIDEO_QUALITY_DEFAULT_AUTO = -1;
    public static final int VIDEO_QUALITY_DEFAULT_ORIGINAL = 0;
    public static final int TRANSLATION_PROVIDER_TELEGRAM = 0;
    public static final int TRANSLATION_PROVIDER_GOOGLE = 1;
    public static final int TRANSLATION_PROVIDER_GOOGLE_CN = 2;
    public static final int TRANSLATION_PROVIDER_DEEPL = 3;
    public static final int TRANSLATION_PROVIDER_LLM = 4;
    public static final int LLM_PROVIDER_CUSTOM = 0;
    public static final int LLM_PROVIDER_OPENAI = 1;
    public static final int LLM_PROVIDER_OPENROUTER = 2;
    public static final int LLM_PROVIDER_DEEPSEEK = 3;
    public static final int LLM_PROVIDER_GROQ = 4;
    public static final int LLM_PROVIDER_SILICONFLOW = 5;
    public static final String DEFAULT_DEEPL_API_URL = "https://api-free.deepl.com/v2/translate";
    public static final String DEFAULT_TRANSLATION_LLM_PROMPT = "You are a professional translation engine. Translate the user's text into the requested target language. Return only the translated text. Preserve line breaks, markdown, URLs, mentions, hashtags, emoji, punctuation, and list structure. Do not add explanations.";
    public static final String DEFAULT_AI_SUMMARY_PROMPT = "You analyze group and channel discussions. Write a structured report in the requested output language based only on the provided messages and local statistics. Cover overview, main topics, hotspots, participant activity, timeline changes, decisions or action items, and unresolved questions. If the messages do not support a conclusion, state that explicitly.";
    private static SharedPreferences prefs() {
        return MessagesController.getGlobalMainSettings();
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    public static String getLlmProviderDefaultApiUrl(int provider) {
        if (provider == LLM_PROVIDER_OPENAI) {
            return "https://api.openai.com/v1/chat/completions";
        }
        if (provider == LLM_PROVIDER_OPENROUTER) {
            return "https://openrouter.ai/api/v1/chat/completions";
        }
        if (provider == LLM_PROVIDER_DEEPSEEK) {
            return "https://api.deepseek.com/chat/completions";
        }
        if (provider == LLM_PROVIDER_GROQ) {
            return "https://api.groq.com/openai/v1/chat/completions";
        }
        if (provider == LLM_PROVIDER_SILICONFLOW) {
            return "https://api.siliconflow.cn/v1/chat/completions";
        }
        return "";
    }

    private static String resolveLlmApiUrl(int provider, String customValue) {
        String value = clean(customValue);
        return value.isEmpty() ? getLlmProviderDefaultApiUrl(provider) : value;
    }

    public static int getDownloadSpeedBoost() {
        return prefs().getInt("flex_download_speed_boost", BOOST_NONE);
    }

    public static void setDownloadSpeedBoost(int value) {
        prefs().edit().putInt("flex_download_speed_boost", value).remove("flex_enhanced_file_loader").apply();
    }

    public static int getDefaultVideoQuality() {
        return prefs().getInt("flex_default_video_quality", VIDEO_QUALITY_DEFAULT_AUTO);
    }

    public static void setDefaultVideoQuality(int value) {
        prefs().edit().putInt("flex_default_video_quality", value).apply();
    }

    public static boolean isEnhancedFileLoaderEnabled() {
        return getDownloadSpeedBoost() != BOOST_NONE;
    }

    public static void setEnhancedFileLoaderEnabled(boolean value) {
        setDownloadSpeedBoost(value ? BOOST_AVERAGE : BOOST_NONE);
    }

    public static boolean isWebRtcDisabled() {
        return prefs().getBoolean("flex_disable_webrtc", false);
    }

    public static void setWebRtcDisabled(boolean value) {
        prefs().edit().putBoolean("flex_disable_webrtc", value).apply();
    }

    public static boolean isNoForwardsRestrictionsDisabled() {
        return prefs().getBoolean("flex_disable_no_forwards_restrictions", false);
    }

    public static void setNoForwardsRestrictionsDisabled(boolean value) {
        prefs().edit().putBoolean("flex_disable_no_forwards_restrictions", value).apply();
    }

    public static boolean isNoForwardsBlocked(boolean peerRestricted, boolean messageRestricted) {
        return !isNoForwardsRestrictionsDisabled() && (peerRestricted || messageRestricted);
    }

    public static boolean isForwardingSourceHiddenByDefault() {
        return prefs().getBoolean("flex_forwarding_hide_source_default", false);
    }

    public static void setForwardingSourceHiddenByDefault(boolean value) {
        SharedPreferences.Editor editor = prefs().edit().putBoolean("flex_forwarding_hide_source_default", value);
        if (!value) {
            editor.putBoolean("flex_forwarding_hide_caption_default", false);
        }
        editor.apply();
    }

    public static boolean isForwardingCaptionHiddenByDefault() {
        return prefs().getBoolean("flex_forwarding_hide_caption_default", false);
    }

    public static void setForwardingCaptionHiddenByDefault(boolean value) {
        SharedPreferences.Editor editor = prefs().edit().putBoolean("flex_forwarding_hide_caption_default", value);
        if (value) {
            editor.putBoolean("flex_forwarding_hide_source_default", true);
        }
        editor.apply();
    }

    public static boolean isDcInfoEnabled() {
        return prefs().getBoolean("flex_show_dc_info", true);
    }

    public static void setDcInfoEnabled(boolean value) {
        prefs().edit().putBoolean("flex_show_dc_info", value).apply();
    }

    public static boolean isMainTabsHidden() {
        return prefs().getBoolean("flex_hide_main_tabs", false);
    }

    public static void setMainTabsHidden(boolean value) {
        prefs().edit().putBoolean("flex_hide_main_tabs", value).apply();
    }

    public static boolean isUiTransparencyDisabled() {
        return prefs().getBoolean("flex_disable_ui_transparency", false);
    }

    public static void setUiTransparencyDisabled(boolean value) {
        prefs().edit().putBoolean("flex_disable_ui_transparency", value).apply();
    }

    public static boolean isUiBlurDisabled() {
        return prefs().getBoolean("flex_disable_ui_blur", false);
    }

    public static void setUiBlurDisabled(boolean value) {
        prefs().edit().putBoolean("flex_disable_ui_blur", value).apply();
    }

    public static int resolveUiTransparencyColor(int color) {
        if (!isUiTransparencyDisabled()) {
            return color;
        }
        final int alpha = Color.alpha(color);
        return alpha == 0 || alpha == 255 ? color : ColorUtils.setAlphaComponent(color, 255);
    }

    public static boolean isMarkdownDisabled() {
        return prefs().getBoolean("flex_disable_markdown", false);
    }

    public static void setMarkdownDisabled(boolean value) {
        prefs().edit().putBoolean("flex_disable_markdown", value).apply();
    }

    public static boolean isNewMarkdownParserEnabled() {
        return prefs().getBoolean("flex_new_markdown_parser", true);
    }

    public static void setNewMarkdownParserEnabled(boolean value) {
        prefs().edit().putBoolean("flex_new_markdown_parser", value).apply();
    }

    public static boolean isMarkdownParseLinksEnabled() {
        return prefs().getBoolean("flex_markdown_parse_links", true);
    }

    public static void setMarkdownParseLinksEnabled(boolean value) {
        prefs().edit().putBoolean("flex_markdown_parse_links", value).apply();
    }

    public static int getTranslationProvider() {
        if (prefs().contains("flex_translation_provider")) {
            return prefs().getInt("flex_translation_provider", TRANSLATION_PROVIDER_TELEGRAM);
        }
        if (prefs().contains("flex_prefer_telegram_translate")) {
            return prefs().getBoolean("flex_prefer_telegram_translate", true) ? TRANSLATION_PROVIDER_TELEGRAM : TRANSLATION_PROVIDER_GOOGLE;
        }
        return TRANSLATION_PROVIDER_TELEGRAM;
    }

    public static void setTranslationProvider(int value) {
        prefs().edit().putInt("flex_translation_provider", value).remove("flex_prefer_telegram_translate").apply();
    }

    public static boolean isTelegramTranslatePreferred() {
        return getTranslationProvider() == TRANSLATION_PROVIDER_TELEGRAM;
    }

    public static boolean usesExternalTranslationProvider() {
        return getTranslationProvider() != TRANSLATION_PROVIDER_TELEGRAM;
    }

    public static String getDeepLApiUrl() {
        String value = prefs().getString("flex_translation_deepl_api_url", DEFAULT_DEEPL_API_URL);
        return value == null || value.trim().isEmpty() ? DEFAULT_DEEPL_API_URL : value.trim();
    }

    public static void setDeepLApiUrl(String value) {
        prefs().edit().putString("flex_translation_deepl_api_url", value == null || value.trim().isEmpty() ? DEFAULT_DEEPL_API_URL : value.trim()).apply();
    }

    public static String getDeepLApiKey() {
        String value = prefs().getString("flex_translation_deepl_api_key", "");
        return value == null ? "" : value.trim();
    }

    public static void setDeepLApiKey(String value) {
        prefs().edit().putString("flex_translation_deepl_api_key", value == null ? "" : value.trim()).apply();
    }

    private static String llmProviderKey(int provider, String suffix) {
        return "flex_llm_provider_" + provider + "_" + suffix;
    }

    private static int getLegacyTranslationLlmProvider() {
        return prefs().getInt("flex_translation_llm_provider", LLM_PROVIDER_OPENAI);
    }

    private static int getLegacyAiSummaryLlmProvider() {
        return prefs().getInt("flex_ai_summary_llm_provider", LLM_PROVIDER_OPENAI);
    }

    private static String getLegacyTranslationLlmApiUrl() {
        return clean(prefs().getString("flex_translation_llm_api_url", ""));
    }

    private static String getLegacyAiSummaryLlmApiUrl() {
        return clean(prefs().getString("flex_ai_summary_llm_api_url", ""));
    }

    private static String getLegacyTranslationLlmApiKey() {
        return clean(prefs().getString("flex_translation_llm_api_key", ""));
    }

    private static String getLegacyAiSummaryLlmApiKey() {
        return clean(prefs().getString("flex_ai_summary_llm_api_key", ""));
    }

    private static String getLegacyTranslationLlmModel() {
        return clean(prefs().getString("flex_translation_llm_model", ""));
    }

    private static String getLegacyAiSummaryLlmModel() {
        return clean(prefs().getString("flex_ai_summary_llm_model", ""));
    }

    private static ArrayList<String> splitModelList(String value) {
        LinkedHashSet<String> models = new LinkedHashSet<>();
        String[] parts = clean(value).replace('\r', '\n').split("[,\n]");
        for (int i = 0; i < parts.length; ++i) {
            String model = clean(parts[i]);
            if (!model.isEmpty()) {
                models.add(model);
            }
        }
        return new ArrayList<>(models);
    }

    private static String joinModelList(Iterable<String> values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            String model = clean(value);
            if (model.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(model);
        }
        return builder.toString();
    }

    private static String mergeModelLists(String first, String second) {
        LinkedHashSet<String> models = new LinkedHashSet<>();
        models.addAll(splitModelList(first));
        models.addAll(splitModelList(second));
        return joinModelList(models);
    }

    public static String makeLlmModelRef(int provider, String model) {
        String value = clean(model);
        return value.isEmpty() ? "" : provider + ":" + value;
    }

    public static int getLlmProviderFromRef(String ref) {
        String value = clean(ref);
        int index = value.indexOf(':');
        if (index <= 0) {
            return -1;
        }
        try {
            return Integer.parseInt(value.substring(0, index));
        } catch (Exception ignore) {
            return -1;
        }
    }

    public static String getLlmModelFromRef(String ref) {
        String value = clean(ref);
        int index = value.indexOf(':');
        if (index < 0 || index >= value.length() - 1) {
            return "";
        }
        return clean(value.substring(index + 1));
    }

    private static String getLegacyTranslationLlmModelRef() {
        String model = getLegacyTranslationLlmModel();
        return model.isEmpty() ? "" : makeLlmModelRef(getLegacyTranslationLlmProvider(), model);
    }

    private static String getLegacyAiSummaryLlmModelRef() {
        String model = getLegacyAiSummaryLlmModel();
        return model.isEmpty() ? "" : makeLlmModelRef(getLegacyAiSummaryLlmProvider(), model);
    }

    public static String getTranslationLlmModelRef() {
        String value = clean(prefs().getString("flex_translation_llm_model_ref", ""));
        return value.isEmpty() ? getLegacyTranslationLlmModelRef() : value;
    }

    public static void setTranslationLlmModelRef(String value) {
        prefs().edit().putString("flex_translation_llm_model_ref", clean(value)).apply();
    }

    public static String getAiSummaryLlmModelRef() {
        String value = clean(prefs().getString("flex_ai_summary_llm_model_ref", ""));
        return value.isEmpty() ? getLegacyAiSummaryLlmModelRef() : value;
    }

    public static void setAiSummaryLlmModelRef(String value) {
        prefs().edit().putString("flex_ai_summary_llm_model_ref", clean(value)).apply();
    }

    public static String getStoredProviderApiUrl(int provider) {
        return clean(prefs().getString(llmProviderKey(provider, "api_url"), ""));
    }

    public static String getProviderApiUrl(int provider) {
        String value = getStoredProviderApiUrl(provider);
        if (!value.isEmpty()) {
            return resolveLlmApiUrl(provider, value);
        }
        if (provider == getLegacyTranslationLlmProvider()) {
            value = getLegacyTranslationLlmApiUrl();
        }
        if (value.isEmpty() && provider == getLegacyAiSummaryLlmProvider()) {
            value = getLegacyAiSummaryLlmApiUrl();
        }
        return resolveLlmApiUrl(provider, value);
    }

    public static void setProviderApiUrl(int provider, String value) {
        prefs().edit().putString(llmProviderKey(provider, "api_url"), clean(value)).apply();
    }

    public static String getStoredProviderApiKey(int provider) {
        return clean(prefs().getString(llmProviderKey(provider, "api_key"), ""));
    }

    public static String getProviderApiKey(int provider) {
        String value = getStoredProviderApiKey(provider);
        if (!value.isEmpty()) {
            return value;
        }
        if (provider == getLegacyTranslationLlmProvider()) {
            value = getLegacyTranslationLlmApiKey();
        }
        if (value.isEmpty() && provider == getLegacyAiSummaryLlmProvider()) {
            value = getLegacyAiSummaryLlmApiKey();
        }
        return value;
    }

    public static void setProviderApiKey(int provider, String value) {
        prefs().edit().putString(llmProviderKey(provider, "api_key"), clean(value)).apply();
    }

    public static String getStoredProviderModelsText(int provider) {
        return clean(prefs().getString(llmProviderKey(provider, "models"), ""));
    }

    public static String getProviderModelsText(int provider) {
        String value = getStoredProviderModelsText(provider);
        if (!value.isEmpty()) {
            return joinModelList(splitModelList(value));
        }
        String translationModels = provider == getLegacyTranslationLlmProvider() ? getLegacyTranslationLlmModel() : "";
        String summaryModels = provider == getLegacyAiSummaryLlmProvider() ? getLegacyAiSummaryLlmModel() : "";
        return mergeModelLists(translationModels, summaryModels);
    }

    public static ArrayList<String> getProviderModelList(int provider) {
        return splitModelList(getProviderModelsText(provider));
    }

    public static void setProviderModelsText(int provider, String value) {
        prefs().edit().putString(llmProviderKey(provider, "models"), joinModelList(splitModelList(value))).apply();
    }

    public static int getTranslationLlmProvider() {
        int provider = getLlmProviderFromRef(getTranslationLlmModelRef());
        return provider >= 0 ? provider : getLegacyTranslationLlmProvider();
    }

    public static void setTranslationLlmProvider(int value) {
        prefs().edit().putInt("flex_translation_llm_provider", value).apply();
    }

    public static String getLlmApiUrl() {
        return getProviderApiUrl(getTranslationLlmProvider());
    }

    public static String getStoredLlmApiUrl() {
        String value = getStoredProviderApiUrl(getTranslationLlmProvider());
        return value.isEmpty() ? getLegacyTranslationLlmApiUrl() : value;
    }

    public static void setLlmApiUrl(String value) {
        setProviderApiUrl(getTranslationLlmProvider(), value);
    }

    public static String getLlmApiKey() {
        return getProviderApiKey(getTranslationLlmProvider());
    }

    public static void setLlmApiKey(String value) {
        setProviderApiKey(getTranslationLlmProvider(), value);
    }

    public static String getLlmModel() {
        String value = getLlmModelFromRef(getTranslationLlmModelRef());
        return value.isEmpty() ? getLegacyTranslationLlmModel() : value;
    }

    public static void setLlmModel(String value) {
        String model = clean(value);
        int provider = getTranslationLlmProvider();
        setTranslationLlmModelRef(makeLlmModelRef(provider, model));
        if (!model.isEmpty()) {
            ArrayList<String> models = getProviderModelList(provider);
            if (!models.contains(model)) {
                models.add(model);
                setProviderModelsText(provider, joinModelList(models));
            }
        }
    }

    public static String getLlmPrompt() {
        String value = clean(prefs().getString("flex_translation_llm_prompt", ""));
        return value.isEmpty() ? DEFAULT_TRANSLATION_LLM_PROMPT : value;
    }

    public static void setLlmPrompt(String value) {
        prefs().edit().putString("flex_translation_llm_prompt", clean(value)).apply();
    }

    public static int getAiSummaryLlmProvider() {
        int provider = getLlmProviderFromRef(getAiSummaryLlmModelRef());
        return provider >= 0 ? provider : getLegacyAiSummaryLlmProvider();
    }

    public static void setAiSummaryLlmProvider(int value) {
        prefs().edit().putInt("flex_ai_summary_llm_provider", value).apply();
    }

    public static String getAiSummaryLlmApiUrl() {
        return getProviderApiUrl(getAiSummaryLlmProvider());
    }

    public static String getStoredAiSummaryLlmApiUrl() {
        String value = getStoredProviderApiUrl(getAiSummaryLlmProvider());
        return value.isEmpty() ? getLegacyAiSummaryLlmApiUrl() : value;
    }

    public static void setAiSummaryLlmApiUrl(String value) {
        setProviderApiUrl(getAiSummaryLlmProvider(), value);
    }

    public static String getAiSummaryLlmApiKey() {
        return getProviderApiKey(getAiSummaryLlmProvider());
    }

    public static void setAiSummaryLlmApiKey(String value) {
        setProviderApiKey(getAiSummaryLlmProvider(), value);
    }

    public static String getAiSummaryLlmModel() {
        String value = getLlmModelFromRef(getAiSummaryLlmModelRef());
        return value.isEmpty() ? getLegacyAiSummaryLlmModel() : value;
    }

    public static void setAiSummaryLlmModel(String value) {
        String model = clean(value);
        int provider = getAiSummaryLlmProvider();
        setAiSummaryLlmModelRef(makeLlmModelRef(provider, model));
        if (!model.isEmpty()) {
            ArrayList<String> models = getProviderModelList(provider);
            if (!models.contains(model)) {
                models.add(model);
                setProviderModelsText(provider, joinModelList(models));
            }
        }
    }

    public static String getAiSummaryLlmPrompt() {
        String value = clean(prefs().getString("flex_ai_summary_llm_prompt", ""));
        return value.isEmpty() ? DEFAULT_AI_SUMMARY_PROMPT : value;
    }

    public static void setAiSummaryLlmPrompt(String value) {
        prefs().edit().putString("flex_ai_summary_llm_prompt", clean(value)).apply();
    }
}
