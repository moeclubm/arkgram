package org.telegram.ui;

import static org.telegram.messenger.LocaleController.getString;

import android.view.View;

import org.telegram.messenger.FlexConfig;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

public class FlexSettingsActivity extends UniversalFragment {

    private static final int ID_ENHANCED_DOWNLOAD = 2;
    private static final int ID_DISABLE_WEBRTC = 3;
    private static final int ID_SHOW_DC_INFO = 4;
    private static final int ID_MARKDOWN = 5;
    private static final int ID_TRANSLATE = 6;
    private static final int ID_HIDE_MAIN_TABS = 7;
    private static final int ID_DISABLE_UI_TRANSPARENCY = 8;
    private static final int ID_DISABLE_UI_BLUR = 9;
    private static final int ID_LLM_SETTINGS = 10;
    private static final int ID_FILE_MANAGEMENT = 11;
    private static final int ID_DISABLE_NO_FORWARDS_RESTRICTIONS = 12;
    private static final int ID_DEFAULT_VIDEO_QUALITY = 13;
    private static final int ID_DEFAULT_PHOTO_QUALITY = 14;
    private static final int ID_AUTO_RETRY_FAILED_MEDIA = 15;
    private static final int ID_DISABLE_CHANNEL_SWIPE_NEXT = 16;
    private static final int ID_LAZY_ATTACH_CAMERA = 17;
    private static final int ID_FORWARD_HIDE_SOURCE = 18;
    private static final int ID_FORWARD_HIDE_CAPTION = 19;
    private static final int ID_AD_BLOCK = 20;
    private static final int ID_PLUS_ONE_MODE = 21;
    private static final int ID_CHAT = 100;
    private static final int ID_DATA = 101;
    private static final int ID_LANGUAGE = 102;
    private static final int ID_NOTIFICATIONS = 103;

    @Override
    protected CharSequence getTitle() {
        return getString(R.string.FlexSettings);
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader(getString(R.string.DataSettings)));
        addDataItems(items);
        items.add(UItem.asShadow(getString(R.string.FlexDefaultVideoQualityInfo)));

        items.add(UItem.asHeader(getString(R.string.ChatSettings)));
        addChatItems(items);
        items.add(UItem.asShadow(getString(R.string.FlexChatSettingsInfo)));

        items.add(UItem.asHeader(getString(R.string.FlexForwardingSettings)));
        addForwardingItems(items);
        items.add(UItem.asShadow(getString(R.string.FlexForwardingSettingsInfo) + "\n\n" + getString(R.string.FlexDisableNoForwardsRestrictionsInfo)));

        items.add(UItem.asHeader(getString(R.string.Calls)));
        items.add(UItem.asCheck(ID_DISABLE_WEBRTC, getString(R.string.FlexDisableWebrtc)).setChecked(FlexConfig.isWebRtcDisabled()));
        items.add(UItem.asShadow(getString(R.string.FlexFeaturesInfo)));

        items.add(UItem.asHeader(getString(R.string.ChangeChannelNameColor2)));
        addAppearanceItems(items);
        items.add(UItem.asShadow(getString(R.string.FlexHideMainTabsInfo)));

        items.add(UItem.asHeader(getString(R.string.General)));
        addGeneralItems(items);
        items.add(UItem.asShadow(getString(R.string.FlexSettingsInfo)));

        items.add(UItem.asHeader(getString(R.string.FlexQuickAccess)));
        addQuickAccessItems(items);
        items.add(UItem.asShadow(null));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        switch (item.id) {
            case ID_ENHANCED_DOWNLOAD:
                showDownloadSpeedBoostDialog();
                break;
            case ID_DEFAULT_VIDEO_QUALITY:
                showDefaultVideoQualityDialog();
                break;
            case ID_DEFAULT_PHOTO_QUALITY:
                SharedConfig.photoHighQualityDefault = !SharedConfig.photoHighQualityDefault;
                SharedConfig.saveConfig();
                refreshItems();
                break;
            case ID_AUTO_RETRY_FAILED_MEDIA:
                SharedConfig.toggleAutoRetryFailedMediaDownloads();
                refreshItems();
                break;
            case ID_DISABLE_CHANNEL_SWIPE_NEXT:
                SharedConfig.toggleDisableChannelSwipeNext();
                refreshItems();
                break;
            case ID_LAZY_ATTACH_CAMERA:
                SharedConfig.toggleLazyAttachCamera();
                refreshItems();
                break;
            case ID_DISABLE_WEBRTC:
                FlexConfig.setWebRtcDisabled(!FlexConfig.isWebRtcDisabled());
                refreshItems();
                break;
            case ID_DISABLE_NO_FORWARDS_RESTRICTIONS:
                FlexConfig.setNoForwardsRestrictionsDisabled(!FlexConfig.isNoForwardsRestrictionsDisabled());
                refreshItems();
                break;
            case ID_FORWARD_HIDE_SOURCE:
                FlexConfig.setForwardingSourceHiddenByDefault(!FlexConfig.isForwardingSourceHiddenByDefault());
                refreshItems();
                break;
            case ID_FORWARD_HIDE_CAPTION:
                FlexConfig.setForwardingCaptionHiddenByDefault(!FlexConfig.isForwardingCaptionHiddenByDefault());
                refreshItems();
                break;
            case ID_PLUS_ONE_MODE:
                showPlusOneModeDialog();
                break;
            case ID_SHOW_DC_INFO:
                FlexConfig.setDcInfoEnabled(!FlexConfig.isDcInfoEnabled());
                refreshItems();
                break;
            case ID_HIDE_MAIN_TABS:
                FlexConfig.setMainTabsHidden(!FlexConfig.isMainTabsHidden());
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.mainTabsVisibilityToggled);
                refreshItems();
                break;
            case ID_DISABLE_UI_TRANSPARENCY:
                FlexConfig.setUiTransparencyDisabled(!FlexConfig.isUiTransparencyDisabled());
                Theme.refreshThemeColors();
                refreshItems();
                break;
            case ID_DISABLE_UI_BLUR:
                FlexConfig.setUiBlurDisabled(!FlexConfig.isUiBlurDisabled());
                Theme.refreshThemeColors();
                refreshItems();
                break;
            case ID_MARKDOWN:
                presentFragment(new FlexMarkdownSettingsActivity());
                break;
            case ID_TRANSLATE:
                presentFragment(new FlexTranslateSettingsActivity());
                break;
            case ID_FILE_MANAGEMENT:
                presentFragment(new FlexFileSettingsActivity());
                break;
            case ID_LLM_SETTINGS:
                presentFragment(new FlexLlmSettingsActivity());
                break;
            case ID_AD_BLOCK:
                presentFragment(new FlexAdBlockSettingsActivity());
                break;
            case ID_CHAT:
                presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC));
                break;
            case ID_DATA:
                presentFragment(new DataSettingsActivity());
                break;
            case ID_LANGUAGE:
                presentFragment(new LanguageSelectActivity());
                break;
            case ID_NOTIFICATIONS:
                presentFragment(new NotificationsSettingsActivity());
                break;
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }

    private void addDataItems(ArrayList<UItem> items) {
        items.add(UItem.asButton(ID_ENHANCED_DOWNLOAD, R.drawable.msg_speed, getString(R.string.FlexEnhancedDownload), getDownloadSpeedBoostTitle()));
        items.add(UItem.asButton(ID_DEFAULT_VIDEO_QUALITY, R.drawable.video_settings, getString(R.string.FlexDefaultVideoQuality), getDefaultVideoQualityTitle()));
        items.add(UItem.asCheck(ID_DEFAULT_PHOTO_QUALITY, getString(R.string.FlexDefaultPhotoQuality)).setChecked(SharedConfig.photoHighQualityDefault));
        items.add(UItem.asCheck(ID_AUTO_RETRY_FAILED_MEDIA, getString(R.string.FlexAutoRetryFailedMedia)).setChecked(SharedConfig.autoRetryFailedMediaDownloads));
        items.add(UItem.asButton(ID_FILE_MANAGEMENT, R.drawable.msg2_data, getString(R.string.FlexFileManagement)));
    }

    private void addChatItems(ArrayList<UItem> items) {
        items.add(UItem.asButton(ID_TRANSLATE, R.drawable.msg_translate, getString(R.string.FlexTranslationSettings)));
        items.add(UItem.asButton(ID_MARKDOWN, R.drawable.menu_feature_code, getString(R.string.FlexMarkdownSettings)));
        items.add(UItem.asButton(ID_AD_BLOCK, R.drawable.msg_block, getString(R.string.FlexAdBlockSettings)));
        items.add(UItem.asCheck(ID_LAZY_ATTACH_CAMERA, getString(R.string.FlexLazyAttachCamera)).setChecked(SharedConfig.lazyAttachCamera));
        items.add(UItem.asCheck(ID_DISABLE_CHANNEL_SWIPE_NEXT, getString(R.string.FlexDisableChannelSwipeNext)).setChecked(SharedConfig.disableChannelSwipeNext));
    }

    private void addForwardingItems(ArrayList<UItem> items) {
        items.add(UItem.asButton(ID_PLUS_ONE_MODE, R.drawable.msg_filled_plus, getString(R.string.FlexPlusOneMode), getPlusOneModeTitle()));
        items.add(UItem.asCheck(ID_FORWARD_HIDE_SOURCE, getString(R.string.FlexForwardHideSourceDefault)).setChecked(FlexConfig.isForwardingSourceHiddenByDefault()));
        items.add(UItem.asCheck(ID_FORWARD_HIDE_CAPTION, getString(R.string.FlexForwardHideCaptionDefault)).setChecked(FlexConfig.isForwardingCaptionHiddenByDefault()));
        items.add(UItem.asCheck(ID_DISABLE_NO_FORWARDS_RESTRICTIONS, getString(R.string.FlexDisableNoForwardsRestrictions)).setChecked(FlexConfig.isNoForwardsRestrictionsDisabled()));
    }

    private void addAppearanceItems(ArrayList<UItem> items) {
        items.add(UItem.asCheck(ID_HIDE_MAIN_TABS, getString(R.string.FlexHideMainTabs)).setChecked(FlexConfig.isMainTabsHidden()));
        items.add(UItem.asCheck(ID_SHOW_DC_INFO, getString(R.string.FlexShowDcInfo)).setChecked(FlexConfig.isDcInfoEnabled()));
        items.add(UItem.asCheck(ID_DISABLE_UI_TRANSPARENCY, getString(R.string.FlexDisableUiTransparency)).setChecked(FlexConfig.isUiTransparencyDisabled()));
        items.add(UItem.asCheck(ID_DISABLE_UI_BLUR, getString(R.string.FlexDisableUiBlur)).setChecked(FlexConfig.isUiBlurDisabled()));
    }

    private void addGeneralItems(ArrayList<UItem> items) {
        items.add(UItem.asButton(ID_LLM_SETTINGS, R.drawable.outline_ai_translate2, getString(R.string.FlexLlmSettings)));
    }

    private void addQuickAccessItems(ArrayList<UItem> items) {
        items.add(UItem.asButton(ID_CHAT, R.drawable.msg2_discussion, getString(R.string.ChatSettings)));
        items.add(UItem.asButton(ID_DATA, R.drawable.msg2_data, getString(R.string.DataSettings)));
        items.add(UItem.asButton(ID_LANGUAGE, R.drawable.msg2_language, getString(R.string.Language)));
        items.add(UItem.asButton(ID_NOTIFICATIONS, R.drawable.msg_notifications, getString(R.string.NotificationsAndSounds)));
    }

    private void refreshItems() {
        listView.adapter.update(true);
    }

    private void showDownloadSpeedBoostDialog() {
        CharSequence[] items = new CharSequence[] {
            getString(R.string.FlexDownloadSpeedBoostOff),
            getString(R.string.FlexDownloadSpeedBoostAverage),
            getString(R.string.FlexDownloadSpeedBoostExtreme)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.FlexEnhancedDownload));
        builder.setItems(items, (dialog, which) -> {
            FlexConfig.setDownloadSpeedBoost(which);
            listView.adapter.update(true);
        });
        showDialog(builder.create());
    }

    private CharSequence getDownloadSpeedBoostTitle() {
        int boost = FlexConfig.getDownloadSpeedBoost();
        if (boost == FlexConfig.BOOST_EXTREME) {
            return getString(R.string.FlexDownloadSpeedBoostExtreme);
        }
        if (boost == FlexConfig.BOOST_AVERAGE) {
            return getString(R.string.FlexDownloadSpeedBoostAverage);
        }
        return getString(R.string.FlexDownloadSpeedBoostOff);
    }

    private void showDefaultVideoQualityDialog() {
        int[] values = new int[] {
            FlexConfig.VIDEO_QUALITY_DEFAULT_HIGHEST,
            FlexConfig.VIDEO_QUALITY_DEFAULT_AUTO,
            FlexConfig.VIDEO_QUALITY_DEFAULT_ORIGINAL,
            2160,
            1440,
            1080,
            720,
            480,
            360,
            240,
            144
        };
        CharSequence[] items = new CharSequence[values.length];
        for (int i = 0; i < values.length; ++i) {
            items[i] = getDefaultVideoQualityTitle(values[i]);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.FlexDefaultVideoQuality));
        builder.setItems(items, (dialog, which) -> {
            FlexConfig.setDefaultVideoQuality(values[which]);
            listView.adapter.update(true);
        });
        showDialog(builder.create());
    }

    private CharSequence getDefaultVideoQualityTitle() {
        return getDefaultVideoQualityTitle(FlexConfig.getDefaultVideoQuality());
    }

    private CharSequence getDefaultVideoQualityTitle(int value) {
        if (value == FlexConfig.VIDEO_QUALITY_DEFAULT_HIGHEST) {
            return getString(R.string.FlexDefaultVideoQualityHighest);
        }
        if (value == FlexConfig.VIDEO_QUALITY_DEFAULT_AUTO) {
            return getString(R.string.QualityAuto);
        }
        if (value == FlexConfig.VIDEO_QUALITY_DEFAULT_ORIGINAL) {
            return getString(R.string.QualityOriginal);
        }
        return value + "p";
    }

    private void showPlusOneModeDialog() {
        CharSequence[] items = new CharSequence[] {
            getString(R.string.FlexPlusOneModeForward),
            getString(R.string.FlexPlusOneModeNoSource)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.FlexPlusOneMode));
        builder.setItems(items, (dialog, which) -> {
            FlexConfig.setPlusOneMode(which);
            refreshItems();
        });
        showDialog(builder.create());
    }

    private CharSequence getPlusOneModeTitle() {
        return getString(FlexConfig.isPlusOneNoSource()
            ? R.string.FlexPlusOneModeNoSource
            : R.string.FlexPlusOneModeForward);
    }
}
