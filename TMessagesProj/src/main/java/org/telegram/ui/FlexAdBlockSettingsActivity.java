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
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

public class FlexAdBlockSettingsActivity extends UniversalFragment {

    private static final int ID_ENABLE = 1;
    private static final int ID_KEYWORDS = 2;
    private static final int ID_CLEAR_MESSAGES = 3;

    @Override
    protected CharSequence getTitle() {
        return getString(R.string.FlexAdBlockSettings);
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader(getString(R.string.FlexAdBlockSettings)));
        items.add(UItem.asCheck(ID_ENABLE, getString(R.string.FlexAdBlockEnabled)).setChecked(FlexConfig.isAdBlockEnabled()));
        items.add(UItem.asButton(ID_KEYWORDS, R.drawable.menu_tag_filter, getString(R.string.FlexAdBlockKeywords), getKeywordsSummary()));
        items.add(UItem.asButton(ID_CLEAR_MESSAGES, R.drawable.msg_delete, getString(R.string.FlexAdBlockClearMessages), String.valueOf(FlexConfig.getAdBlockedMessageCount())));
        items.add(UItem.asShadow(getString(R.string.FlexAdBlockInfo)));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == ID_ENABLE) {
            FlexConfig.setAdBlockEnabled(!FlexConfig.isAdBlockEnabled());
            notifyChanged();
            listView.adapter.update(true);
        } else if (item.id == ID_KEYWORDS) {
            showKeywordsDialog();
        } else if (item.id == ID_CLEAR_MESSAGES) {
            FlexConfig.clearAdBlockedMessages();
            notifyChanged();
            listView.adapter.update(true);
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }

    private CharSequence getKeywordsSummary() {
        String value = FlexConfig.getAdBlockKeywordsText().replace('\n', ' ').trim();
        if (TextUtils.isEmpty(value)) {
            return getString(R.string.FlexLlmNotSet);
        }
        return value.length() <= 36 ? value : value.substring(0, 36) + "...";
    }

    private void showKeywordsDialog() {
        if (getContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.FlexAdBlockKeywords));

        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);

        TextView hintView = new TextView(getContext());
        hintView.setText(getString(R.string.FlexAdBlockKeywordsHint));
        hintView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        hintView.setTextColor(Theme.getColor(Theme.key_dialogTextGray3, resourceProvider));
        container.addView(hintView, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 4, 24, 12));

        EditTextBoldCursor editText = new EditTextBoldCursor(getContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        editText.setText(FlexConfig.getAdBlockKeywordsText());
        editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourceProvider));
        editText.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText, resourceProvider));
        editText.setCursorColor(Theme.getColor(Theme.key_chat_messagePanelCursor, resourceProvider));
        editText.setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField, resourceProvider), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated, resourceProvider), Theme.getColor(Theme.key_text_RedRegular, resourceProvider));
        editText.setSingleLine(false);
        editText.setMinLines(6);
        editText.setMaxLines(12);
        editText.setGravity(Gravity.TOP | (org.telegram.messenger.LocaleController.isRTL ? Gravity.RIGHT : Gravity.LEFT));
        editText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        editText.setBackgroundDrawable(null);
        container.addView(editText, LayoutHelper.createLinear(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, 24, 0, 24, 12));

        builder.setView(container);
        builder.setWidth(AndroidUtilities.dp(320));
        builder.setPositiveButton(getString(R.string.Done), (dialog, which) -> {
            FlexConfig.setAdBlockKeywordsText(editText.getText().toString());
            notifyChanged();
            listView.adapter.update(true);
        });
        builder.setNegativeButton(getString(R.string.Cancel), null);
        if (!TextUtils.isEmpty(FlexConfig.getAdBlockKeywordsText())) {
            builder.setNeutralButton(getString(R.string.FlexLlmClearValue), (dialog, which) -> {
                FlexConfig.setAdBlockKeywordsText("");
                notifyChanged();
                listView.adapter.update(true);
            });
        }

        AlertDialog dialog = builder.create();
        showDialog(dialog);
        editText.setSelection(editText.getText().length());
    }

    private void notifyChanged() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.flexAdBlockSettingsChanged);
    }
}
