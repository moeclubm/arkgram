package org.telegram.ui;

import static org.telegram.messenger.LocaleController.getString;

import android.view.View;

import org.telegram.messenger.FlexConfig;
import org.telegram.messenger.R;
import org.telegram.messenger.TranslateController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.TranslateAlert2;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

public class FlexTranslateSettingsActivity extends UniversalFragment {

    private static final int ID_SHOW_BUTTON = 1;
    private static final int ID_SHOW_CHAT_BUTTON = 2;
    private static final int ID_PROVIDER = 3;
    private static final int ID_TARGET_LANGUAGE = 4;

    @Override
    protected CharSequence getTitle() {
        return getString(R.string.FlexTranslationSettings);
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        TranslateController translateController = getMessagesController().getTranslateController();
        String toLanguage = TranslateAlert2.languageName(TranslateAlert2.getToLanguage());
        if (toLanguage == null) {
            toLanguage = TranslateAlert2.getToLanguage();
        }

        items.add(UItem.asHeader(getString(R.string.TranslateMessages)));
        items.add(UItem.asCheck(ID_SHOW_BUTTON, getString(R.string.ShowTranslateButton)).setChecked(translateController.isContextTranslateEnabled()));
        items.add(UItem.asCheck(ID_SHOW_CHAT_BUTTON, getString(R.string.ShowTranslateChatButton)).setChecked(translateController.isChatTranslateEnabled()));
        items.add(UItem.asButton(ID_PROVIDER, R.drawable.msg_translate, getString(R.string.FlexTranslationProvider), getProviderTitle()));
        items.add(UItem.asButton(ID_TARGET_LANGUAGE, R.drawable.msg2_language, getString(R.string.FlexTranslationTargetLanguage), TranslateAlert2.capitalFirst(toLanguage)));
        items.add(UItem.asShadow(getString(R.string.FlexTranslationInfo)));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        TranslateController translateController = getMessagesController().getTranslateController();
        if (item.id == ID_SHOW_BUTTON) {
            translateController.setContextTranslateEnabled(!translateController.isContextTranslateEnabled());
            listView.adapter.update(true);
        } else if (item.id == ID_SHOW_CHAT_BUTTON) {
            translateController.setChatTranslateEnabled(!translateController.isChatTranslateEnabled());
            listView.adapter.update(true);
        } else if (item.id == ID_PROVIDER) {
            showProviderDialog();
        } else if (item.id == ID_TARGET_LANGUAGE) {
            showLanguageDialog();
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }

    private void showProviderDialog() {
        CharSequence[] items = new CharSequence[] {
            getString(R.string.FlexTranslationProviderTelegram),
            getString(R.string.FlexTranslationProviderGoogle),
            getString(R.string.FlexTranslationProviderGoogleCn)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.FlexTranslationProvider));
        builder.setItems(items, (dialog, which) -> {
            FlexConfig.setTranslationProvider(which);
            listView.adapter.update(true);
        });
        showDialog(builder.create());
    }

    private void showLanguageDialog() {
        ArrayList<TranslateController.Language> languages = TranslateController.getLanguages();
        CharSequence[] items = new CharSequence[languages.size()];
        for (int i = 0; i < languages.size(); i++) {
            items[i] = languages.get(i).displayName;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.FlexTranslationTargetLanguage));
        builder.setItems(items, (dialog, which) -> {
            TranslateAlert2.setToLanguage(languages.get(which).code);
            listView.adapter.update(true);
        });
        showDialog(builder.create());
    }

    private CharSequence getProviderTitle() {
        int provider = FlexConfig.getTranslationProvider();
        if (provider == 2) {
            return getString(R.string.FlexTranslationProviderGoogleCn);
        }
        if (provider == 1) {
            return getString(R.string.FlexTranslationProviderGoogle);
        }
        return getString(R.string.FlexTranslationProviderTelegram);
    }
}
