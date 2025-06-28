package com.janhvi.qrshare.utility;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.janhvi.qrshare.MainActivity;
import com.janhvi.qrshare.R;

public class DialogUtils {

    private static final String TAG = DialogUtils.class.getSimpleName();

    private static Dialog dialog;

    public static void showLoadingDialog(Context context, String message) {
        if (dialog != null && dialog.isShowing()) return;

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        TextView tvLoadingText = dialog.findViewById(R.id.tvLoadingText);
        tvLoadingText.setText(message);

        dialog.show();
    }

    public static void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
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
            int flags = Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK;
            SharedPref.setIsLoggedIn(context, false);
            SharedPref.deleteAll(context);
            Helper.goToWithFlags(context, MainActivity.class, flags);
        });

        builder.setNegativeButton("No", (dialog, id) -> dialog.cancel());
        return builder.create();
    }

    // Show confirmation dialog before performing an operation (e.g., delete)
    public static AlertDialog confirmationDialog(final Context context, String confirmationText, DialogInterface.OnClickListener onDeleteClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (confirmationText == null) confirmationText = "perform this operation";
        builder.setMessage("Are you sure you want to " + confirmationText + "?" + "\nWARNING: This action cannot be undone");
        builder.setPositiveButton("Yes", onDeleteClickListener);
        builder.setNegativeButton("No", (dialog, id) -> dialog.cancel());
        return builder.create();
    }
}