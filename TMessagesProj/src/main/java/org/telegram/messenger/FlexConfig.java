package org.telegram.messenger;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Base64;

import androidx.core.graphics.ColorUtils;

import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

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
    public static final int LLM_PROVIDER_OPENAI = 1;
    public static final int LLM_PROVIDER_DEEPSEEK = 3;
    public static final int LLM_PROVIDER_ANTHROPIC = 6;
    public static final int LLM_ENDPOINT_OPENAI = 0;
    public static final int LLM_ENDPOINT_RESPONSES = 1;
    public static final int LLM_ENDPOINT_ANTHROPIC = 2;
    public static final int AD_BLOCK_COLLAPSED_LOCAL_ID = -21041001;
    public static final String DEFAULT_DEEPL_API_URL = "https://api-free.deepl.com/v2/translate";
    public static final String DEFAULT_TRANSLATION_LLM_PROMPT = "You are a professional translation engine. Translate the user's text into the requested target language. Return only the translated text. Preserve line breaks, markdown, URLs, mentions, hashtags, emoji, punctuation, and list structure. Do not add explanations.";
    public static final String DEFAULT_AI_SUMMARY_PROMPT = "You analyze group and channel discussions. Write a structured report in the requested output language based only on the provided messages and local statistics. Cover overview, main topics, hotspots, participant activity, timeline changes, decisions or action items, and unresolved questions. If the messages do not support a conclusion, state that explicitly.";
    private static SharedPreferences prefs() {
        return MessagesController.getGlobalMainSettings();
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private static ArrayList<String> splitLineList(String value) {
        LinkedHashSet<String> lines = new LinkedHashSet<>();
        String[] parts = clean(value).replace('\r', '\n').split("\n");
        for (int i = 0; i < parts.length; ++i) {
            String line = clean(parts[i]);
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        return new ArrayList<>(lines);
    }

    private static String joinLineList(Iterable<String> values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            String line = clean(value);
            if (line.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(line);
        }
        return builder.toString();
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

    public static boolean isPlusOneMessageOnly() {
        return prefs().getBoolean("flex_plus_one_message_only", false);
    }

    public static void setPlusOneMessageOnly(boolean value) {
        prefs().edit().putBoolean("flex_plus_one_message_only", value).apply();
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

    public static boolean isAdBlockEnabled() {
        return prefs().getBoolean("flex_ad_block_enabled", false);
    }

    public static void setAdBlockEnabled(boolean value) {
        prefs().edit().putBoolean("flex_ad_block_enabled", value).apply();
    }

    public static String getAdBlockKeywordsText() {
        return joinLineList(splitLineList(prefs().getString("flex_ad_block_keywords", "")));
    }

    public static ArrayList<String> getAdBlockKeywords() {
        return splitLineList(getAdBlockKeywordsText());
    }

    public static void setAdBlockKeywordsText(String value) {
        prefs().edit().putString("flex_ad_block_keywords", joinLineList(splitLineList(value))).apply();
    }

    public static void addAdBlockKeyword(String value) {
        LinkedHashSet<String> keywords = new LinkedHashSet<>(getAdBlockKeywords());
        String keyword = clean(value);
        if (!keyword.isEmpty()) {
            keywords.add(keyword);
            prefs().edit().putString("flex_ad_block_keywords", joinLineList(keywords)).apply();
        }
    }

    public static void removeAdBlockKeyword(String value) {
        LinkedHashSet<String> keywords = new LinkedHashSet<>(getAdBlockKeywords());
        if (keywords.remove(clean(value))) {
            prefs().edit().putString("flex_ad_block_keywords", joinLineList(keywords)).apply();
        }
    }

    public static String getAdBlockedMessageIdsText() {
        Set<String> values = prefs().getStringSet("flex_ad_blocked_message_ids", new LinkedHashSet<>());
        return joinLineList(values);
    }

    public static void setAdBlockedMessageIdsText(String value) {
        prefs().edit().putStringSet("flex_ad_blocked_message_ids", new LinkedHashSet<>(splitLineList(value))).apply();
    }

    public static int getAdBlockedMessageCount() {
        return prefs().getStringSet("flex_ad_blocked_message_ids", new LinkedHashSet<>()).size();
    }

    public static void clearAdBlockedMessages() {
        prefs().edit().remove("flex_ad_blocked_message_ids").apply();
    }

    public static int getAdBlockedMessageRuleCount() {
        return prefs().getStringSet("flex_ad_blocked_message_keys", new LinkedHashSet<>()).size();
    }

    public static String getAdBlockedMessageRulesText() {
        Set<String> values = prefs().getStringSet("flex_ad_blocked_message_keys", new LinkedHashSet<>());
        return joinLineList(values);
    }

    public static void setAdBlockedMessageRulesText(String value) {
        prefs().edit().putStringSet("flex_ad_blocked_message_keys", new LinkedHashSet<>(splitLineList(value))).apply();
    }

    public static int getAdBlockedUserCount() {
        return prefs().getStringSet("flex_ad_blocked_user_ids", new LinkedHashSet<>()).size();
    }

    public static String getAdBlockedUsersText() {
        Set<String> values = prefs().getStringSet("flex_ad_blocked_user_ids", new LinkedHashSet<>());
        return joinLineList(values);
    }

    public static void setAdBlockedUsersText(String value) {
        prefs().edit().putStringSet("flex_ad_blocked_user_ids", new LinkedHashSet<>(splitLineList(value))).apply();
    }

    public static int getAdBlockRuleCount() {
        return getAdBlockKeywords().size() + getAdBlockedMessageCount() + getAdBlockedMessageRuleCount() + getAdBlockedUserCount();
    }

    public static void clearAdBlockRules() {
        prefs().edit()
                .remove("flex_ad_block_keywords")
                .remove("flex_ad_blocked_message_ids")
                .remove("flex_ad_blocked_message_keys")
                .remove("flex_ad_blocked_user_ids")
                .apply();
    }

    public static void addAdBlockedMessage(MessageObject messageObject) {
        LinkedHashSet<String> ids = new LinkedHashSet<>(prefs().getStringSet("flex_ad_blocked_message_ids", new LinkedHashSet<>()));
        ids.add(Base64.encodeToString(messageObject.sponsoredId, Base64.NO_WRAP));
        prefs().edit().putStringSet("flex_ad_blocked_message_ids", ids).apply();
    }

    public static void addAdBlockedMessageRule(MessageObject messageObject) {
        LinkedHashSet<String> keys = new LinkedHashSet<>(prefs().getStringSet("flex_ad_blocked_message_keys", new LinkedHashSet<>()));
        keys.addAll(getMessageRuleKeys(messageObject));
        prefs().edit().putStringSet("flex_ad_blocked_message_keys", keys).apply();
    }

    public static void addAdBlockedUserRule(MessageObject messageObject) {
        long senderId = messageObject.getSenderId();
        if (senderId != 0) {
            LinkedHashSet<String> ids = new LinkedHashSet<>(prefs().getStringSet("flex_ad_blocked_user_ids", new LinkedHashSet<>()));
            ids.add(Long.toString(senderId));
            prefs().edit().putStringSet("flex_ad_blocked_user_ids", ids).apply();
        }
    }

    public static boolean isMessageBlocked(MessageObject messageObject) {
        if (!isAdBlockEnabled()) {
            return false;
        }
        if (messageObject.messageOwner != null && messageObject.messageOwner.local_id == AD_BLOCK_COLLAPSED_LOCAL_ID) {
            return false;
        }
        if (messageObject.isSponsored()) {
            return isSponsoredMessageBlocked(messageObject);
        }
        if (prefs().getStringSet("flex_ad_blocked_user_ids", new LinkedHashSet<>()).contains(Long.toString(messageObject.getSenderId()))) {
            return true;
        }
        Set<String> blockedMessageKeys = prefs().getStringSet("flex_ad_blocked_message_keys", new LinkedHashSet<>());
        ArrayList<String> messageKeys = getMessageRuleKeys(messageObject);
        for (int i = 0; i < messageKeys.size(); ++i) {
            if (blockedMessageKeys.contains(messageKeys.get(i))) {
                return true;
            }
        }
        ArrayList<String> keywords = getAdBlockKeywords();
        return !keywords.isEmpty() && containsAdBlockKeyword(getMessageSearchText(messageObject), keywords);
    }

    public static boolean isSponsoredMessageBlocked(MessageObject messageObject) {
        if (!isAdBlockEnabled() || !messageObject.isSponsored()) {
            return false;
        }
        String sponsoredId = Base64.encodeToString(messageObject.sponsoredId, Base64.NO_WRAP);
        if (prefs().getStringSet("flex_ad_blocked_message_ids", new LinkedHashSet<>()).contains(sponsoredId)) {
            return true;
        }
        ArrayList<String> keywords = getAdBlockKeywords();
        return !keywords.isEmpty() && containsAdBlockKeyword(getMessageSearchText(messageObject), keywords);
    }

    private static ArrayList<String> getMessageRuleKeys(MessageObject messageObject) {
        LinkedHashSet<String> keys = new LinkedHashSet<>();
        int messageId = messageObject.getId();
        long dialogId = messageObject.getDialogId();
        if (dialogId != 0 && messageId > 0) {
            keys.add(dialogId + ":" + messageId);
        }
        TLRPC.MessageFwdHeader fwdFrom = messageObject.messageOwner.fwd_from;
        if (fwdFrom != null) {
            long fromId = MessageObject.getPeerId(fwdFrom.from_id);
            if (fromId != 0 && fwdFrom.channel_post > 0) {
                keys.add(fromId + ":" + fwdFrom.channel_post);
            }
            long savedFromPeerId = MessageObject.getPeerId(fwdFrom.saved_from_peer);
            if (savedFromPeerId != 0 && fwdFrom.saved_from_msg_id > 0) {
                keys.add(savedFromPeerId + ":" + fwdFrom.saved_from_msg_id);
            }
            if (savedFromPeerId != 0 && fwdFrom.channel_post > 0) {
                keys.add(savedFromPeerId + ":" + fwdFrom.channel_post);
            }
        }
        return new ArrayList<>(keys);
    }

    private static boolean containsAdBlockKeyword(String value, ArrayList<String> keywords) {
        String text = value.toLowerCase(Locale.US);
        for (int i = 0; i < keywords.size(); ++i) {
            if (text.contains(keywords.get(i).toLowerCase(Locale.US))) {
                return true;
            }
        }
        return false;
    }

    private static String getMessageSearchText(MessageObject messageObject) {
        StringBuilder builder = new StringBuilder();
        appendSearchText(builder, messageObject.messageText);
        appendSearchText(builder, messageObject.caption);
        appendSearchText(builder, messageObject.messageOwner.message);
        appendSearchText(builder, messageObject.sponsoredTitle);
        appendSearchText(builder, messageObject.sponsoredUrl);
        appendSearchText(builder, messageObject.sponsoredInfo);
        appendSearchText(builder, messageObject.sponsoredAdditionalInfo);
        appendSearchText(builder, messageObject.sponsoredButtonText);
        return builder.toString();
    }

    private static void appendSearchText(StringBuilder builder, CharSequence value) {
        if (TextUtils.isEmpty(value)) {
            return;
        }
        if (builder.length() > 0) {
            builder.append('\n');
        }
        builder.append(value);
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

    public static final String DEFAULT_OPENAI_API_URL = "https://api.openai.com/v1";
    public static final String DEFAULT_ANTHROPIC_API_URL = "https://api.anthropic.com";
    public static final String DEFAULT_DEEPSEEK_API_URL = "https://api.deepseek.com";

    private static String llmProviderKey(int provider, String suffix) {
        return "flex_llm_provider_" + provider + "_" + suffix;
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

    // ----- provider list -----

    public static ArrayList<Integer> getProviderIds() {
        String stored = clean(prefs().getString("flex_llm_provider_ids", ""));
        if (stored.isEmpty()) {
            seedDefaultProviders();
            stored = clean(prefs().getString("flex_llm_provider_ids", ""));
        }
        ArrayList<Integer> ids = new ArrayList<>();
        String[] parts = stored.split(",");
        for (int i = 0; i < parts.length; ++i) {
            String part = clean(parts[i]);
            if (!part.isEmpty()) {
                ids.add(Integer.parseInt(part));
            }
        }
        return ids;
    }

    public static void setProviderIds(ArrayList<Integer> ids) {
        saveProviderIds(ids);
    }

    private static void saveProviderIds(ArrayList<Integer> ids) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ids.size(); ++i) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(ids.get(i));
        }
        prefs().edit().putString("flex_llm_provider_ids", builder.toString()).apply();
    }

    private static void seedDefaultProviders() {
        SharedPreferences.Editor editor = prefs().edit();
        editor.putString("flex_llm_provider_ids", LLM_PROVIDER_OPENAI + "," + LLM_PROVIDER_ANTHROPIC + "," + LLM_PROVIDER_DEEPSEEK);
        seedProvider(editor, LLM_PROVIDER_OPENAI, "OpenAI", LLM_ENDPOINT_OPENAI, DEFAULT_OPENAI_API_URL);
        seedProvider(editor, LLM_PROVIDER_ANTHROPIC, "Anthropic", LLM_ENDPOINT_ANTHROPIC, DEFAULT_ANTHROPIC_API_URL);
        seedProvider(editor, LLM_PROVIDER_DEEPSEEK, "DeepSeek", LLM_ENDPOINT_OPENAI, DEFAULT_DEEPSEEK_API_URL);
        editor.apply();
    }

    private static void seedProvider(SharedPreferences.Editor editor, int provider, String name, int type, String apiUrl) {
        if (!prefs().contains(llmProviderKey(provider, "name"))) {
            editor.putString(llmProviderKey(provider, "name"), name);
        }
        if (!prefs().contains(llmProviderKey(provider, "type"))) {
            editor.putInt(llmProviderKey(provider, "type"), type);
        }
        if (!prefs().contains(llmProviderKey(provider, "api_url"))) {
            editor.putString(llmProviderKey(provider, "api_url"), apiUrl);
        }
    }

    public static int addProvider(String name, int type) {
        ArrayList<Integer> ids = getProviderIds();
        int newId = LLM_PROVIDER_ANTHROPIC + 1;
        for (int i = 0; i < ids.size(); ++i) {
            if (ids.get(i) >= newId) {
                newId = ids.get(i) + 1;
            }
        }
        prefs().edit()
            .putString(llmProviderKey(newId, "name"), clean(name))
            .putInt(llmProviderKey(newId, "type"), type)
            .apply();
        ids.add(newId);
        saveProviderIds(ids);
        return newId;
    }

    public static void removeProvider(int provider) {
        ArrayList<Integer> ids = getProviderIds();
        if (ids.remove((Integer) provider)) {
            saveProviderIds(ids);
        }
        prefs().edit()
            .remove(llmProviderKey(provider, "name"))
            .remove(llmProviderKey(provider, "type"))
            .remove(llmProviderKey(provider, "api_url"))
            .remove(llmProviderKey(provider, "api_key"))
            .remove(llmProviderKey(provider, "models"))
            .apply();
    }

    public static String getProviderName(int provider) {
        getProviderIds();
        return clean(prefs().getString(llmProviderKey(provider, "name"), ""));
    }

    public static void setProviderName(int provider, String value) {
        prefs().edit().putString(llmProviderKey(provider, "name"), clean(value)).apply();
    }

    public static int getProviderType(int provider) {
        getProviderIds();
        return prefs().getInt(llmProviderKey(provider, "type"), LLM_ENDPOINT_OPENAI);
    }

    public static void setProviderType(int provider, int value) {
        prefs().edit().putInt(llmProviderKey(provider, "type"), value).apply();
    }

    public static String getStoredProviderApiUrl(int provider) {
        return clean(prefs().getString(llmProviderKey(provider, "api_url"), ""));
    }

    public static String getProviderApiUrl(int provider) {
        return getStoredProviderApiUrl(provider);
    }

    public static void setProviderApiUrl(int provider, String value) {
        prefs().edit().putString(llmProviderKey(provider, "api_url"), clean(value)).apply();
    }

    public static String getProviderApiKey(int provider) {
        return clean(prefs().getString(llmProviderKey(provider, "api_key"), ""));
    }

    public static void setProviderApiKey(int provider, String value) {
        prefs().edit().putString(llmProviderKey(provider, "api_key"), clean(value)).apply();
    }

    public static String getStoredProviderModelsText(int provider) {
        return clean(prefs().getString(llmProviderKey(provider, "models"), ""));
    }

    public static String getProviderModelsText(int provider) {
        return joinModelList(splitModelList(getStoredProviderModelsText(provider)));
    }

    public static ArrayList<String> getProviderModelList(int provider) {
        return splitModelList(getProviderModelsText(provider));
    }

    public static void setProviderModelsText(int provider, String value) {
        prefs().edit().putString(llmProviderKey(provider, "models"), joinModelList(splitModelList(value))).apply();
    }

    // ----- streaming -----

    public static boolean isLlmStreamEnabled() {
        return prefs().getBoolean("flex_llm_stream", true);
    }

    public static void setLlmStreamEnabled(boolean value) {
        prefs().edit().putBoolean("flex_llm_stream", value).apply();
    }

    // ----- model refs -----

    public static String getTranslationLlmModelRef() {
        return clean(prefs().getString("flex_translation_llm_model_ref", ""));
    }

    public static void setTranslationLlmModelRef(String value) {
        prefs().edit().putString("flex_translation_llm_model_ref", clean(value)).apply();
    }

    public static String getAiSummaryLlmModelRef() {
        return clean(prefs().getString("flex_ai_summary_llm_model_ref", ""));
    }

    public static void setAiSummaryLlmModelRef(String value) {
        prefs().edit().putString("flex_ai_summary_llm_model_ref", clean(value)).apply();
    }

    private static int resolveFeatureProvider(String ref) {
        int provider = getLlmProviderFromRef(ref);
        if (provider >= 0) {
            return provider;
        }
        ArrayList<Integer> ids = getProviderIds();
        return ids.isEmpty() ? LLM_PROVIDER_OPENAI : ids.get(0);
    }

    // ----- translation feature -----

    public static int getTranslationLlmProvider() {
        return resolveFeatureProvider(getTranslationLlmModelRef());
    }

    public static void setTranslationLlmProvider(int value) {
        String model = getLlmModelFromRef(getTranslationLlmModelRef());
        if (!model.isEmpty()) {
            setTranslationLlmModelRef(makeLlmModelRef(value, model));
        }
    }

    public static int getTranslationLlmEndpointType() {
        return getProviderType(getTranslationLlmProvider());
    }

    public static String getLlmApiUrl() {
        return getProviderApiUrl(getTranslationLlmProvider());
    }

    public static String getStoredLlmApiUrl() {
        return getStoredProviderApiUrl(getTranslationLlmProvider());
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
        return getLlmModelFromRef(getTranslationLlmModelRef());
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

    // ----- AI summary feature -----

    public static int getAiSummaryLlmProvider() {
        return resolveFeatureProvider(getAiSummaryLlmModelRef());
    }

    public static void setAiSummaryLlmProvider(int value) {
        String model = getLlmModelFromRef(getAiSummaryLlmModelRef());
        if (!model.isEmpty()) {
            setAiSummaryLlmModelRef(makeLlmModelRef(value, model));
        }
    }

    public static int getAiSummaryLlmEndpointType() {
        return getProviderType(getAiSummaryLlmProvider());
    }

    public static String getAiSummaryLlmApiUrl() {
        return getProviderApiUrl(getAiSummaryLlmProvider());
    }

    public static String getStoredAiSummaryLlmApiUrl() {
        return getStoredProviderApiUrl(getAiSummaryLlmProvider());
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
        return getLlmModelFromRef(getAiSummaryLlmModelRef());
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
