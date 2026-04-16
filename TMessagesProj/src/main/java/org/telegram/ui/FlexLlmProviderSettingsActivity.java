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
import org.telegram.messenger.LocaleController;
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

public class FlexLlmProviderSettingsActivity extends UniversalFragment {

    private static final int ID_API_URL = 1;
    private static final int ID_API_KEY = 2;
    private static final int ID_MODELS = 3;

    private final int provider;

    public FlexLlmProviderSettingsActivity(int provider) {
        this.provider = provider;
    }

    @Override
    protected CharSequence getTitle() {
        return FlexLlmFeatureSettingsActivity.getProviderTitle(provider);
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader(getString(R.string.FlexLlmProviderConfig)));
        items.add(UItem.asButton(ID_API_URL, R.drawable.msg2_data, getString(R.string.FlexLlmApiUrl), formatPlainValue(FlexConfig.getProviderApiUrl(provider))));
        items.add(UItem.asButton(ID_API_KEY, R.drawable.msg_translate, getString(R.string.FlexLlmApiKey), formatSecretValue(FlexConfig.getProviderApiKey(provider))));
        items.add(UItem.asButton(ID_MODELS, R.drawable.menu_feature_code, getString(R.string.FlexLlmModels), formatModelsValue()));
        items.add(UItem.asShadow(getInfoText()));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == ID_API_URL) {
            showTextValueDialog(getString(R.string.FlexLlmApiUrl), getString(R.string.FlexLlmApiUrlHint), FlexConfig.getProviderApiUrl(provider), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, false, value -> {
                FlexConfig.setProviderApiUrl(provider, value);
                listView.adapter.update(true);
            });
        } else if (item.id == ID_API_KEY) {
            showTextValueDialog(getString(R.string.FlexLlmApiKey), null, FlexConfig.getProviderApiKey(provider), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, false, value -> {
                FlexConfig.setProviderApiKey(provider, value);
                listView.adapter.update(true);
            });
        } else if (item.id == ID_MODELS) {
            showTextValueDialog(getString(R.string.FlexLlmModels), getString(R.string.FlexLlmModelsHint), FlexConfig.getProviderModelsText(provider), InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, true, value -> {
                FlexConfig.setProviderModelsText(provider, value);
                listView.adapter.update(true);
            });
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }

    private String getInfoText() {
        return getString(R.string.FlexLlmApiUrlHint) + '\n' + getString(R.string.FlexLlmModelsHint);
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

    private CharSequence formatModelsValue() {
        ArrayList<String> models = FlexConfig.getProviderModelList(provider);
        if (models.isEmpty()) {
            return getString(R.string.FlexLlmNotSet);
        }
        if (models.size() == 1) {
            return models.get(0);
        }
        return LocaleController.formatString(R.string.FlexLlmModelsCount, models.size());
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
