package org.telegram.ui;

import static org.telegram.messenger.LocaleController.getString;

import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FlexConfig;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

public class FlexAdBlockSettingsActivity extends UniversalFragment implements NotificationCenter.NotificationCenterDelegate {

    private static final int ID_ENABLE = 1;
    private static final int ID_ADD_KEYWORD = 2;
    private static final int ID_KEYWORD = 3;
    private static final int ID_CLEAR_RULES = 4;
    private static final int ID_CLEAR_TELEGRAM_BLOCKLIST = 5;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getMessagesController().getBlockedPeers(true);
        getNotificationCenter().addObserver(this, NotificationCenter.blockedUsersDidLoad);
        return true;
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.blockedUsersDidLoad);
    }

    @Override
    protected CharSequence getTitle() {
        return getString(R.string.FlexAdBlockSettings);
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        ArrayList<String> keywords = FlexConfig.getAdBlockKeywords();

        items.add(UItem.asHeader(getString(R.string.FlexAdBlockSettings)));
        items.add(UItem.asCheck(ID_ENABLE, getString(R.string.FlexAdBlockEnabled)).setChecked(FlexConfig.isAdBlockEnabled()));

        items.add(UItem.asHeader(getString(R.string.FlexAdBlockKeywords)));
        items.add(UItem.asButton(ID_ADD_KEYWORD, R.drawable.menu_tag_filter, getString(R.string.FlexAdBlockAddKeyword)));
        if (keywords.isEmpty()) {
            items.add(UItem.asShadow(getString(R.string.FlexLlmNotSet)));
        } else {
            for (int i = 0; i < keywords.size(); ++i) {
                UItem keywordItem = UItem.asButton(ID_KEYWORD, R.drawable.msg_delete, keywords.get(i), getString(R.string.Delete));
                keywordItem.object = keywords.get(i);
                items.add(keywordItem);
            }
            items.add(UItem.asShadow(getString(R.string.FlexAdBlockKeywordsHint)));
        }

        items.add(UItem.asHeader(getString(R.string.FlexAdBlockRules)));
        items.add(UItem.asButton(0, R.drawable.msg_block2, getString(R.string.FlexAdBlockBlockedMessages), String.valueOf(FlexConfig.getAdBlockedMessageCount() + FlexConfig.getAdBlockedMessageRuleCount())).setEnabled(false));
        items.add(UItem.asButton(0, R.drawable.msg_block2, getString(R.string.FlexAdBlockBlockedUsers), String.valueOf(FlexConfig.getAdBlockedUserCount())).setEnabled(false));
        items.add(UItem.asButton(ID_CLEAR_RULES, R.drawable.msg_delete, getString(R.string.FlexAdBlockClearRules), String.valueOf(FlexConfig.getAdBlockRuleCount())).red());

        items.add(UItem.asHeader(getString(R.string.FlexTelegramBlocklist)));
        items.add(UItem.asButton(ID_CLEAR_TELEGRAM_BLOCKLIST, R.drawable.msg_delete, getString(R.string.FlexClearTelegramBlocklist), getTelegramBlocklistSummary()).red());
        items.add(UItem.asShadow(getString(R.string.FlexAdBlockInfo)));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == ID_ENABLE) {
            FlexConfig.setAdBlockEnabled(!FlexConfig.isAdBlockEnabled());
            notifyChanged();
            listView.adapter.update(true);
        } else if (item.id == ID_ADD_KEYWORD) {
            showAddKeywordDialog();
        } else if (item.id == ID_KEYWORD) {
            showRemoveKeywordDialog((String) item.object);
        } else if (item.id == ID_CLEAR_RULES) {
            showClearRulesDialog();
        } else if (item.id == ID_CLEAR_TELEGRAM_BLOCKLIST) {
            showClearTelegramBlocklistDialog();
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        if (item.id == ID_KEYWORD) {
            FlexConfig.removeAdBlockKeyword((String) item.object);
            notifyChanged();
            listView.adapter.update(true);
            return true;
        }
        return false;
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.blockedUsersDidLoad && listView != null) {
            listView.adapter.update(true);
        }
    }

    private CharSequence getTelegramBlocklistSummary() {
        MessagesController messagesController = getMessagesController();
        int count = messagesController.totalBlockedCount >= 0 ? messagesController.totalBlockedCount : messagesController.blockePeers.size();
        return String.valueOf(Math.max(count, messagesController.blockePeers.size()));
    }

    private void showAddKeywordDialog() {
        if (getContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.FlexAdBlockAddKeyword));

        EditTextBoldCursor editText = new EditTextBoldCursor(getContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourceProvider));
        editText.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText, resourceProvider));
        editText.setCursorColor(Theme.getColor(Theme.key_chat_messagePanelCursor, resourceProvider));
        editText.setLineColors(Theme.getColor(Theme.key_windowBackgroundWhiteInputField, resourceProvider), Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated, resourceProvider), Theme.getColor(Theme.key_text_RedRegular, resourceProvider));
        editText.setSingleLine(true);
        editText.setHint(getString(R.string.FlexAdBlockKeywordHint));
        editText.setBackgroundDrawable(null);

        builder.setView(editText);
        builder.setWidth(AndroidUtilities.dp(320));
        builder.setPositiveButton(getString(R.string.Add), (dialog, which) -> {
            String keyword = editText.getText().toString().trim();
            if (!TextUtils.isEmpty(keyword)) {
                FlexConfig.addAdBlockKeyword(keyword);
                notifyChanged();
                listView.adapter.update(true);
            }
        });
        builder.setNegativeButton(getString(R.string.Cancel), null);

        AlertDialog dialog = builder.create();
        showDialog(dialog);
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    private void showRemoveKeywordDialog(String keyword) {
        if (getContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.FlexAdBlockRemoveKeyword));
        builder.setMessage(keyword);
        builder.setPositiveButton(getString(R.string.Delete), (dialog, which) -> {
            FlexConfig.removeAdBlockKeyword(keyword);
            notifyChanged();
            listView.adapter.update(true);
        });
        builder.setNegativeButton(getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void showClearRulesDialog() {
        if (getContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.FlexAdBlockClearRules));
        builder.setMessage(getString(R.string.FlexAdBlockClearRulesConfirm));
        builder.setPositiveButton(getString(R.string.Clear), (dialog, which) -> {
            FlexConfig.clearAdBlockRules();
            notifyChanged();
            listView.adapter.update(true);
            AlertsCreator.showSimpleToast(this, getString(R.string.FlexAdBlockRulesCleared));
        });
        builder.setNegativeButton(getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void showClearTelegramBlocklistDialog() {
        if (getContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.FlexClearTelegramBlocklist));
        builder.setMessage(getString(R.string.FlexClearTelegramBlocklistConfirm));
        builder.setPositiveButton(getString(R.string.Clear), (dialog, which) -> {
            AlertDialog progressDialog = new AlertDialog(getContext(), AlertDialog.ALERT_TYPE_SPINNER);
            progressDialog.setCanCancel(false);
            progressDialog.showDelayed(200);
            getMessagesController().clearBlockedPeers((count, error) -> {
                progressDialog.dismiss();
                if (listView != null) {
                    listView.adapter.update(true);
                }
                if (error != null) {
                    AlertsCreator.showSimpleToast(this, LocaleController.formatString(R.string.UnknownErrorCode, error.text));
                } else {
                    AlertsCreator.showSimpleToast(this, LocaleController.formatString(R.string.FlexTelegramBlocklistCleared, count));
                }
            });
        });
        builder.setNegativeButton(getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void notifyChanged() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.flexAdBlockSettingsChanged);
    }
}
