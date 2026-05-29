package org.telegram.ui;

import static org.telegram.messenger.LocaleController.getString;

import android.text.TextUtils;
import android.view.View;

import org.telegram.messenger.FlexConfig;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

public class FlexLlmSettingsActivity extends UniversalFragment {

    private static final int ID_TRANSLATION = 1;
    private static final int ID_AI_SUMMARY = 2;
    private static final int ID_ADD_PROVIDER = 3;
    private static final int ID_STREAM = 4;
    private static final int ID_PROVIDER_BASE = 1000;

    @Override
    protected CharSequence getTitle() {
        return getString(R.string.FlexLlmSettings);
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader(getString(R.string.FlexLlmProviders)));
        ArrayList<Integer> providers = FlexConfig.getProviderIds();
        for (int i = 0; i < providers.size(); ++i) {
            int provider = providers.get(i);
            items.add(UItem.asButton(ID_PROVIDER_BASE + provider, R.drawable.msg2_data, FlexLlmFeatureSettingsActivity.getProviderTitle(provider), getProviderValue(provider)));
        }
        items.add(UItem.asButton(ID_ADD_PROVIDER, R.drawable.msg_filled_plus, getString(R.string.FlexLlmAddProvider)));
        items.add(UItem.asShadow(getString(R.string.FlexLlmSettingsInfo)));
        items.add(UItem.asHeader(getString(R.string.FlexLlmFeatures)));
        items.add(UItem.asButton(ID_TRANSLATION, R.drawable.msg_translate, getString(R.string.FlexTranslationLlmSettings), getFeatureValue(false)));
        items.add(UItem.asButton(ID_AI_SUMMARY, R.drawable.outline_ai_translate2, getString(R.string.FlexAiSummarySettings), getFeatureValue(true)));
        items.add(UItem.asCheck(ID_STREAM, getString(R.string.FlexLlmStream)).setChecked(FlexConfig.isLlmStreamEnabled()));
        items.add(UItem.asShadow(getString(R.string.FlexLlmStreamInfo)));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == ID_TRANSLATION) {
            presentFragment(new FlexLlmFeatureSettingsActivity(false));
        } else if (item.id == ID_AI_SUMMARY) {
            presentFragment(new FlexLlmFeatureSettingsActivity(true));
        } else if (item.id == ID_STREAM) {
            FlexConfig.setLlmStreamEnabled(!FlexConfig.isLlmStreamEnabled());
            listView.adapter.update(true);
        } else if (item.id == ID_ADD_PROVIDER) {
            showAddProviderDialog();
        } else if (item.id >= ID_PROVIDER_BASE) {
            presentFragment(new FlexLlmProviderSettingsActivity(item.id - ID_PROVIDER_BASE));
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }

    private void showAddProviderDialog() {
        int[] types = new int[] {FlexConfig.LLM_ENDPOINT_OPENAI, FlexConfig.LLM_ENDPOINT_RESPONSES, FlexConfig.LLM_ENDPOINT_ANTHROPIC};
        CharSequence[] items = new CharSequence[types.length];
        for (int i = 0; i < types.length; ++i) {
            items[i] = FlexLlmFeatureSettingsActivity.getEndpointTypeTitle(types[i]);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.FlexLlmAddProvider));
        builder.setItems(items, (dialog, which) -> {
            int provider = FlexConfig.addProvider(getString(R.string.FlexLlmProviderCustom).toString(), types[which]);
            presentFragment(new FlexLlmProviderSettingsActivity(provider));
        });
        showDialog(builder.create());
    }

    private CharSequence getProviderValue(int provider) {
        ArrayList<String> models = FlexConfig.getProviderModelList(provider);
        if (models.isEmpty()) {
            return getString(R.string.FlexLlmNotSet);
        }
        if (models.size() == 1) {
            return models.get(0);
        }
        return LocaleController.formatString(R.string.FlexLlmModelsCount, models.size());
    }

    private CharSequence getFeatureValue(boolean summary) {
        int provider = summary ? FlexConfig.getAiSummaryLlmProvider() : FlexConfig.getTranslationLlmProvider();
        String model = summary ? FlexConfig.getAiSummaryLlmModel() : FlexConfig.getLlmModel();
        if (TextUtils.isEmpty(model)) {
            return getString(R.string.FlexLlmNotSet);
        }
        return FlexLlmFeatureSettingsActivity.getProviderTitle(provider) + " / " + model;
    }
}
