package com.greenteadev.unive.clair.util;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog;

import it.greenteadev.unive.clair.R;

public final class DialogFactory {

    public static Dialog createSimpleConfirmDialog(Context context, String title, String message, MaterialDialog.SingleButtonCallback positive) {
        MaterialDialog.Builder alertDialog = new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .neutralText(R.string.dialog_action_cancel)
                .positiveText(R.string.dialog_action_confirm)
                .onPositive(positive);
        return alertDialog.build();
    }

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message) {
        MaterialDialog.Builder alertDialog = new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .neutralText(R.string.dialog_action_ok);
        return alertDialog.build();
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource));
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        MaterialDialog.Builder alertDialog = new MaterialDialog.Builder(context)
                .title(context.getString(R.string.dialog_error_title))
                .content(message)
                .neutralText(R.string.dialog_action_ok);
        return alertDialog.build();
    }

    public static Dialog createGenericErrorDialog(Context context, @StringRes int messageResource) {
        return createGenericErrorDialog(context, context.getString(messageResource));
    }

    public static Dialog createProgressDialog(Context context, String message) {
        MaterialDialog.Builder progressDialog = new MaterialDialog.Builder(context);
        progressDialog.content(message);
        return progressDialog.build();
    }

    public static Dialog createProgressDialog(Context context,
                                              @StringRes int messageResource) {
        return createProgressDialog(context, context.getString(messageResource));
    }

}
