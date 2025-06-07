package com.janhvi.qrshare.utility;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

public class DialogUtils {

    private static final String TAG = DialogUtils.class.getSimpleName();

    // Show loading dialog
//    public static Dialog showLoadingDialog(Context context, String message, Dialog dialog) {
//        try {
//            DialogUtils.dismissDialog(dialog);
//            dialog = new Dialog(context, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
//            dialog.setContentView(R.layout.loading);
//            TextView tvLoadingText = dialog.findViewById(R.id.tvLoadingText);
//            if (message.trim().isEmpty()) {
//                tvLoadingText.setVisibility(View.GONE);
//            } else {
//                tvLoadingText.setText(message);
//                tvLoadingText.setVisibility(View.VISIBLE);
//            }
//            dialog.setCancelable(false);
//            dialog.show();
//            return dialog;
//        } catch (Exception e) {
//            Log.e(TAG, "Error in DialogUtils: ", e);
//        }
//        return null;
//    }

    // Dismiss a dialog
    public static void dismissDialog(Dialog dialog) {
        try {
            if (dialog != null) {
                try {
                    dialog.cancel();
                } catch (Exception e) {
                    Log.e(TAG, "Error in DialogUtils: ", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in DialogUtils: ", e);
        }
    }

    // Show simple AlertDialog with options list
    public static AlertDialog showOptionAlertDialog(Context context, String title, String[] optionsList, DialogInterface.OnClickListener onCLickListItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setItems(optionsList, onCLickListItem);
        return builder.create();
    }

    // Open a simple AlertDialog with message and OK button
    public static AlertDialog openAlertDialog(final Context context, String title, String message, DialogInterface.OnClickListener onClickPositiveButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", onClickPositiveButton);
        return builder.create();
    }

    // Logout dialog with option to finish activity
    public static AlertDialog logoutDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("Yes", (dialog, id) -> {
            dialog.cancel();
            int flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK;
            SharedPref.setIsLoggedIn(context, false);
//            Helper.goToWithFlags(context, MainActivity.class, flags);
        });

        builder.setNegativeButton("No", (dialog, id) -> dialog.cancel());
        return builder.create();
    }

    // Show confirmation dialog before performing an operation (e.g., delete)
    public static AlertDialog confirmationDialog(final Context context, String confirmationText, DialogInterface.OnClickListener onDeleteClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (confirmationText == null) confirmationText = "perform this operation";
        builder.setMessage("Are you sure you want to " + confirmationText + "?" +
                "\nWARNING: This action cannot be undone");
        builder.setPositiveButton("Yes", onDeleteClickListener);
        builder.setNegativeButton("No", (dialog, id) -> dialog.cancel());
        return builder.create();
    }


}