package org.telegram.ui;

import static org.telegram.messenger.LocaleController.getString;

import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FlexConfig;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

public class FlexLlmFeatureSettingsActivity extends UniversalFragment {

    private static final int ID_PROVIDER = 1;
    private static final int ID_API_URL = 2;
    private static final int ID_API_KEY = 3;
    private static final int ID_MODEL = 4;
    private static final int ID_PROMPT = 5;

    private final boolean summary;

    public FlexLlmFeatureSettingsActivity(boolean summary) {
        this.summary = summary;
    }

    @Override
    protected CharSequence getTitle() {
        return summary ? getString(R.string.FlexAiSummarySettings) : getString(R.string.FlexTranslationLlmSettings);
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader(getString(R.string.FlexLlmProviderConfig)));
        items.add(UItem.asButton(ID_PROVIDER, R.drawable.msg_translate, getString(R.string.FlexLlmProvider), getProviderTitle(getProvider())));
        items.add(UItem.asButton(ID_API_URL, R.drawable.msg2_data, getString(R.string.FlexLlmApiUrl), formatPlainValue(getApiUrl())));
        items.add(UItem.asButton(ID_API_KEY, R.drawable.msg_translate, getString(R.string.FlexLlmApiKey), formatSecretValue(getApiKey())));
        items.add(UItem.asButton(ID_MODEL, R.drawable.msg2_data, getString(R.string.FlexLlmModel), formatPlainValue(getModel())));
        items.add(UItem.asButton(ID_PROMPT, R.drawable.menu_feature_code, getString(R.string.FlexLlmPrompt), formatPromptValue(getPrompt())));
        items.add(UItem.asShadow(getInfoText()));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == ID_PROVIDER) {
            showProviderDialog();
        } else if (item.id == ID_API_URL) {
            showTextValueDialog(getString(R.string.FlexLlmApiUrl), getString(R.string.FlexLlmApiUrlHint), getApiUrl(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, false, this::setApiUrl);
        } else if (item.id == ID_API_KEY) {
            showTextValueDialog(getString(R.string.FlexLlmApiKey), null, getApiKey(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, false, this::setApiKey);
        } else if (item.id == ID_MODEL) {
            showTextValueDialog(getString(R.string.FlexLlmModel), getString(R.string.FlexLlmModelHint), getModel(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, false, this::setModel);
        } else if (item.id == ID_PROMPT) {
            showTextValueDialog(getString(R.string.FlexLlmPrompt), getString(R.string.FlexLlmPromptHint), getPrompt(), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES, true, this::setPrompt);
        }
        listView.adapter.update(true);
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }

    public static CharSequence getProviderTitle(int provider) {
        if (provider == FlexConfig.LLM_PROVIDER_OPENAI) {
            return getString(R.string.FlexLlmProviderOpenAi);
        }
        if (provider == FlexConfig.LLM_PROVIDER_OPENROUTER) {
            return getString(R.string.FlexLlmProviderOpenRouter);
        }
        if (provider == FlexConfig.LLM_PROVIDER_DEEPSEEK) {
            return getString(R.string.FlexLlmProviderDeepSeek);
        }
        if (provider == FlexConfig.LLM_PROVIDER_GROQ) {
            return getString(R.string.FlexLlmProviderGroq);
        }
        if (provider == FlexConfig.LLM_PROVIDER_SILICONFLOW) {
            return getString(R.string.FlexLlmProviderSiliconFlow);
        }
        return getString(R.string.FlexLlmProviderCustom);
    }

    private int getProvider() {
        return summary ? FlexConfig.getAiSummaryLlmProvider() : FlexConfig.getTranslationLlmProvider();
    }

    private void setProvider(int provider) {
        if (summary) {
            FlexConfig.setAiSummaryLlmProvider(provider);
        } else {
            FlexConfig.setTranslationLlmProvider(provider);
        }
    }

    private String getApiUrl() {
        return summary ? FlexConfig.getAiSummaryLlmApiUrl() : FlexConfig.getLlmApiUrl();
    }

    private void setApiUrl(String value) {
        if (summary) {
            FlexConfig.setAiSummaryLlmApiUrl(value);
        } else {
            FlexConfig.setLlmApiUrl(value);
        }
    }

    private String getApiKey() {
        return summary ? FlexConfig.getAiSummaryLlmApiKey() : FlexConfig.getLlmApiKey();
    }

    private void setApiKey(String value) {
        if (summary) {
            FlexConfig.setAiSummaryLlmApiKey(value);
        } else {
            FlexConfig.setLlmApiKey(value);
        }
    }

    private String getModel() {
        return summary ? FlexConfig.getAiSummaryLlmModel() : FlexConfig.getLlmModel();
    }

    private void setModel(String value) {
        if (summary) {
            FlexConfig.setAiSummaryLlmModel(value);
        } else {
            FlexConfig.setLlmModel(value);
        }
    }

    private String getPrompt() {
        return summary ? FlexConfig.getAiSummaryLlmPrompt() : FlexConfig.getLlmPrompt();
    }

    private void setPrompt(String value) {
        if (summary) {
            FlexConfig.setAiSummaryLlmPrompt(value);
        } else {
            FlexConfig.setLlmPrompt(value);
        }
    }

    private String getInfoText() {
        StringBuilder builder = new StringBuilder();
        builder.append(getString(R.string.FlexLlmApiUrlHint)).append('\n');
        builder.append(getString(R.string.FlexLlmPromptHint)).append('\n').append('\n');
        builder.append(summary ? getString(R.string.FlexAiSummarySettingsInfo) : getString(R.string.FlexTranslationLlmSettingsInfo));
        return builder.toString();
    }

    private CharSequence formatPlainValue(String value) {
        return TextUtils.isEmpty(value) ? getString(R.string.FlexLlmNotSet) : value;
    }

    private CharSequence formatSecretValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return getString(R.string.FlexLlmNotSet);
        }
        if (value.length() <= 4) {
            return "****";
        }
        return "****" + value.substring(value.length() - 4);
    }

    private CharSequence formatPromptValue(String value) {
        if (TextUtils.isEmpty(value)) {
            return getString(R.string.FlexLlmNotSet);
        }
        String singleLine = value.replace('\n', ' ').trim();
        if (singleLine.length() <= 36) {
            return singleLine;
        }
        return singleLine.substring(0, 36) + "...";
    }

    private void showProviderDialog() {
        CharSequence[] items = new CharSequence[] {
            getString(R.string.FlexLlmProviderCustom),
            getString(R.string.FlexLlmProviderOpenAi),
            getString(R.string.FlexLlmProviderOpenRouter),
            getString(R.string.FlexLlmProviderDeepSeek),
            getString(R.string.FlexLlmProviderGroq),
            getString(R.string.FlexLlmProviderSiliconFlow)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.FlexLlmProvider));
        builder.setItems(items, (dialog, which) -> {
            setProvider(which);
            listView.adapter.update(true);
        });
        showDialog(builder.create());
    }

    private void showTextValueDialog(String title, String hint, String value, int inputType, boolean multiline, Utilities.Callback<String> onSave) {
        if (getContext() == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);

        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);

        if (!TextUtils.isEmpty(hint)) {
            TextView hintView = new TextView(getContext());
            hintView.setText(hint);
            hintView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            hintView.setTextColor(Theme.getColor(Theme.key_dialogTextGray3, resourceProvider));
            container.addView(hintView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 4, 24, 12));
        }

        EditTextBoldCursor editText = new EditTextBoldCursor(getContext());
        editText.setInputType(inputType);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        editText.setText(value);
        editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourceProvider));
        editText.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText, resourceProvider));
        editText.setCursorColor(Theme.getColor(Theme.key_chat_messagePanelCursor, resourceProvider));
        editText.setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField, resourceProvider), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated, resourceProvider), Theme.getColor(Theme.key_text_RedRegular, resourceProvider));
        editText.setSingleLine(!multiline);
        editText.setMaxLines(multiline ? 12 : 1);
        if (multiline) {
            editText.setMinLines(6);
            editText.setGravity(Gravity.TOP | (org.telegram.messenger.LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT));
        }
        editText.setFocusable(true);
        editText.setImeOptions(multiline ? EditorInfo.IME_FLAG_NO_ENTER_ACTION : EditorInfo.IME_ACTION_DONE);
        editText.setBackgroundDrawable(null);
        container.addView(editText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 0, 24, 12));

        builder.setView(container);
        builder.setWidth(AndroidUtilities.dp(multiline ? 320 : 292));
        builder.setPositiveButton(getString(R.string.Done), (dialog, which) -> onSave.run(editText.getText().toString().trim()));
        builder.setNegativeButton(getString(R.string.Cancel), null);
        if (!TextUtils.isEmpty(value)) {
            builder.setNeutralButton(getString(R.string.FlexLlmClearValue), (dialog, which) -> onSave.run(""));
        }

        AlertDialog dialog = builder.create();
        showDialog(dialog);
        editText.setSelection(editText.getText().length());
    }
}
