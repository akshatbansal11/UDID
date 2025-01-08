package com.udid.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.udid.R;


public class ProcessDialog {
    private static Dialog progressDialog;

    public static void start(Context context) {
        if (!isShowing()) {
            if (!((Activity) context).isFinishing()) {
                progressDialog = new Dialog(context);
                progressDialog.setCancelable(false);
           /*     progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(false);*/
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.setContentView(R.layout.progress_loader);
                progressDialog.show();
            }
        }
    }

    public static void dismiss() {
        try {
            if ((progressDialog != null) && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            progressDialog = null;
        }
    }

    public static boolean isShowing() {
        if (progressDialog != null) {
            return progressDialog.isShowing();
        } else {
            return false;
        }
    }
}
