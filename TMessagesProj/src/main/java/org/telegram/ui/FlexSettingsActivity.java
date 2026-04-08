package org.telegram.ui;

import static org.telegram.messenger.LocaleController.getString;

import android.view.View;

import org.telegram.messenger.FlexConfig;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
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
        items.add(UItem.asHeader(getString(R.string.FlexFeatures)));
        items.add(UItem.asButton(ID_ENHANCED_DOWNLOAD, R.drawable.msg_speed, getString(R.string.FlexEnhancedDownload), getDownloadSpeedBoostTitle()));
        items.add(UItem.asCheck(ID_DISABLE_WEBRTC, getString(R.string.FlexDisableWebrtc)).setChecked(FlexConfig.isWebRtcDisabled()));
        items.add(UItem.asCheck(ID_SHOW_DC_INFO, getString(R.string.FlexShowDcInfo)).setChecked(FlexConfig.isDcInfoEnabled()));
        items.add(UItem.asButton(ID_MARKDOWN, R.drawable.menu_feature_code, getString(R.string.FlexMarkdownSettings)));
        items.add(UItem.asButton(ID_TRANSLATE, R.drawable.msg_translate, getString(R.string.FlexTranslationSettings)));
        items.add(UItem.asShadow(getString(R.string.FlexFeaturesInfo)));

        items.add(UItem.asHeader(getString(R.string.FlexQuickAccess)));
        items.add(UItem.asButton(ID_CHAT, R.drawable.msg2_discussion, getString(R.string.ChatSettings)));
        items.add(UItem.asButton(ID_DATA, R.drawable.msg2_data, getString(R.string.DataSettings)));
        items.add(UItem.asButton(ID_LANGUAGE, R.drawable.msg2_language, getString(R.string.Language)));
        items.add(UItem.asButton(ID_NOTIFICATIONS, R.drawable.msg_notifications, getString(R.string.NotificationsAndSounds)));
        items.add(UItem.asShadow(null));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == ID_ENHANCED_DOWNLOAD) {
            showDownloadSpeedBoostDialog();
        } else if (item.id == ID_DISABLE_WEBRTC) {
            FlexConfig.setWebRtcDisabled(!FlexConfig.isWebRtcDisabled());
            listView.adapter.update(true);
        } else if (item.id == ID_SHOW_DC_INFO) {
            FlexConfig.setDcInfoEnabled(!FlexConfig.isDcInfoEnabled());
            listView.adapter.update(true);
        } else if (item.id == ID_MARKDOWN) {
            presentFragment(new FlexMarkdownSettingsActivity());
        } else if (item.id == ID_TRANSLATE) {
            presentFragment(new FlexTranslateSettingsActivity());
        } else if (item.id == ID_CHAT) {
            presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC));
        } else if (item.id == ID_DATA) {
            presentFragment(new DataSettingsActivity());
        } else if (item.id == ID_LANGUAGE) {
            presentFragment(new LanguageSelectActivity());
        } else if (item.id == ID_NOTIFICATIONS) {
            presentFragment(new NotificationsSettingsActivity());
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
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
}
