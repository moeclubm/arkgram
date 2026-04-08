package org.telegram.messenger;

import android.content.SharedPreferences;
import android.os.Bundle;

import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.MainTabsActivity;

public class FlexConfig {

    private static SharedPreferences prefs() {
        return MessagesController.getGlobalMainSettings();
    }

    public static boolean isMainTabsEnabled() {
        return prefs().getBoolean("flex_main_tabs_enabled", false);
    }

    public static void setMainTabsEnabled(boolean value) {
        prefs().edit().putBoolean("flex_main_tabs_enabled", value).apply();
    }

    public static boolean isEnhancedFileLoaderEnabled() {
        return prefs().getBoolean("flex_enhanced_file_loader", false);
    }

    public static void setEnhancedFileLoaderEnabled(boolean value) {
        prefs().edit().putBoolean("flex_enhanced_file_loader", value).apply();
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
            return prefs().getInt("flex_translation_provider", 0);
        }
        if (prefs().contains("flex_prefer_telegram_translate")) {
            return prefs().getBoolean("flex_prefer_telegram_translate", true) ? 0 : 1;
        }
        return 0;
    }

    public static void setTranslationProvider(int value) {
        prefs().edit().putInt("flex_translation_provider", value).remove("flex_prefer_telegram_translate").apply();
    }

    public static boolean isTelegramTranslatePreferred() {
        return getTranslationProvider() == 0;
    }

    public static BaseFragment createMainFragment() {
        return createMainFragment(null);
    }

    public static BaseFragment createMainFragment(Bundle args) {
        if (isMainTabsEnabled()) {
            try {
                MainTabsActivity mainTabsActivity = new MainTabsActivity();
                if (args != null) {
                    mainTabsActivity.prepareDialogsActivity(new Bundle(args));
                }
                return mainTabsActivity;
            } catch (Throwable e) {
                FileLog.e(e);
                setMainTabsEnabled(false);
            }
        }
        return args == null ? new DialogsActivity(null) : new DialogsActivity(new Bundle(args));
    }
}
