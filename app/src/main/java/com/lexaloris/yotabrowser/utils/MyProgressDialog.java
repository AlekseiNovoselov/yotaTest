package com.lexaloris.yotabrowser.utils;

import android.app.ProgressDialog;

import com.lexaloris.yotabrowser.MainActivity;

public class MyProgressDialog {

    private MainActivity activity;
    private ProgressDialog mProgressDialog;
    private String dialogText;

    private boolean shouldShowProgressDialog = false;

    public MyProgressDialog(MainActivity activity) {
        mProgressDialog = new ProgressDialog(activity);
        this.activity = activity;
    }

    public void showProgressDialog(int textResourceId) {
        mProgressDialog.setIndeterminate(true);
        dialogText = activity.getString(textResourceId);
        mProgressDialog.setMessage(dialogText);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        shouldShowProgressDialog = true;
    }

    public void dismissProgressDialog() {
        mProgressDialog.dismiss();
        shouldShowProgressDialog = false;
    }

    public void continueShowProgressDialog(boolean flag, String progressDialogText) {
        if (flag) {
            showProgressDialog(progressDialogText);
        }
    }

    private void showProgressDialog(String progressDialogText) {
        dialogText = progressDialogText;
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(progressDialogText);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        shouldShowProgressDialog = true;
    }

    public boolean isShouldShowProgressDialog() {
        return shouldShowProgressDialog;
    }

    public String getDialogText() {
        return dialogText;
    }
}
