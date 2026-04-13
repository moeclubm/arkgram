package org.telegram.ui;

import static org.telegram.messenger.LocaleController.getString;

import android.view.View;

import org.telegram.messenger.R;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

public class FlexLlmSettingsActivity extends UniversalFragment {

    private static final int ID_TRANSLATION = 1;
    private static final int ID_AI_SUMMARY = 2;

    @Override
    protected CharSequence getTitle() {
        return getString(R.string.FlexLlmSettings);
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader(getString(R.string.FlexLlmSettings)));
        items.add(UItem.asButton(ID_TRANSLATION, R.drawable.msg_translate, getString(R.string.FlexTranslationLlmSettings)));
        items.add(UItem.asButton(ID_AI_SUMMARY, R.drawable.outline_ai_translate2, getString(R.string.FlexAiSummarySettings)));
        items.add(UItem.asShadow(getString(R.string.FlexLlmSettingsInfo)));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == ID_TRANSLATION) {
            presentFragment(new FlexLlmFeatureSettingsActivity(false));
        } else if (item.id == ID_AI_SUMMARY) {
            presentFragment(new FlexLlmFeatureSettingsActivity(true));
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
