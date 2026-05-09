package org.telegram.ui;

import static org.telegram.messenger.LocaleController.getString;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FlexFileManager;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;
import java.util.Locale;

public class FlexFileSettingsActivity extends UniversalFragment {

    private static final int ID_EXPORT_SETTINGS = 1;
    private static final int ID_IMPORT_SETTINGS = 2;
    private static final int REQUEST_EXPORT_SETTINGS = 6001;
    private static final int REQUEST_IMPORT_SETTINGS = 6002;

    @Override
    protected CharSequence getTitle() {
        return getString(R.string.FlexFileManagement);
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader(getString(R.string.FlexFileManagement)));
        items.add(UItem.asButton(ID_EXPORT_SETTINGS, R.drawable.msg2_data, getString(R.string.FlexFileExportSettings)));
        items.add(UItem.asButton(ID_IMPORT_SETTINGS, R.drawable.msg2_data, getString(R.string.FlexFileImportSettings)));
        items.add(UItem.asShadow(getString(R.string.FlexFileManagementInfo)));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == ID_EXPORT_SETTINGS) {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            intent.putExtra(Intent.EXTRA_TITLE, String.format(Locale.US, "arkgram-settings-%1$tY%1$tm%1$td-%1$tH%1$tM%1$tS.json", System.currentTimeMillis()));
            startActivityForResult(intent, REQUEST_EXPORT_SETTINGS);
        } else if (item.id == ID_IMPORT_SETTINGS) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            startActivityForResult(intent, REQUEST_IMPORT_SETTINGS);
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }

    @Override
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null) {
            return;
        }
        Uri uri = data.getData();
        if (requestCode == REQUEST_EXPORT_SETTINGS) {
            exportSettings(uri);
        } else if (requestCode == REQUEST_IMPORT_SETTINGS) {
            confirmImport(uri);
        }
    }

    private void confirmImport(Uri uri) {
        if (getContext() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.FlexFileImportConfirmTitle));
        builder.setMessage(getString(R.string.FlexFileImportConfirmText));
        builder.setPositiveButton(getString(R.string.Import), (dialog, which) -> importSettings(uri));
        builder.setNegativeButton(getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    private void exportSettings(Uri uri) {
        Utilities.globalQueue.postRunnable(() -> {
            try {
                FlexFileManager.exportSettings(uri, currentAccount);
                AndroidUtilities.runOnUIThread(() -> {
                    if (getParentActivity() != null) {
                        BulletinFactory.of(this).createSuccessBulletin(getString(R.string.FlexFileExported)).show();
                    }
                });
            } catch (Exception e) {
                FileLog.e(e);
                showFileError(e);
            }
        });
    }

    private void importSettings(Uri uri) {
        Utilities.globalQueue.postRunnable(() -> {
            try {
                FlexFileManager.importSettings(uri, currentAccount);
                AndroidUtilities.runOnUIThread(() -> {
                    if (getParentActivity() != null) {
                        BulletinFactory.of(this).createSuccessBulletin(getString(R.string.FlexFileImported)).show();
                    }
                });
            } catch (Exception e) {
                FileLog.e(e);
                showFileError(e);
            }
        });
    }

    private void showFileError(Exception e) {
        String message = e == null ? null : e.getMessage();
        AndroidUtilities.runOnUIThread(() -> {
            if (getParentActivity() != null) {
                BulletinFactory.of(this).createErrorBulletin(TextUtils.isEmpty(message) ? getString(R.string.UnknownError) : message).show();
            }
        });
    }
}
