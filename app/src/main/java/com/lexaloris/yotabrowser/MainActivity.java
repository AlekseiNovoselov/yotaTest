package com.lexaloris.yotabrowser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.lexaloris.yotabrowser.db.DBHelper;
import com.lexaloris.yotabrowser.db.MyContentProvider;
import com.lexaloris.yotabrowser.fragments.RequestFragment;
import com.lexaloris.yotabrowser.network.service.NetworkService;
import com.lexaloris.yotabrowser.network.service.Response;
import com.lexaloris.yotabrowser.network.service.ServiceHelper;
import com.lexaloris.yotabrowser.utils.MyFragmentManager;
import com.lexaloris.yotabrowser.utils.MyProgressDialog;

public class MainActivity extends AppCompatActivity implements RequestFragment.OnRequestListener {

    private MyFragmentManager myFragmentManager;
    private long mUrlRequestId;
    private MyProgressDialog myProgressDialog;
    private boolean shouldShowProgressDialog = false;
    private String progressDialogText;

    private BroadcastReceiver mQueryCompletedReceiver = new QueryCompletedReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myProgressDialog = new MyProgressDialog(MainActivity.this);
        myFragmentManager = new MyFragmentManager(MainActivity.this);

    }

    @Override
    public void sendRequest(String urlText) {
        mUrlRequestId = ServiceHelper.get(this).getSourceText(urlText);
        myProgressDialog.showProgressDialog(R.string.request_progress_dialog_text);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            myFragmentManager.addRequestFragment();
            DBHelper dbHelper = new DBHelper();
            dbHelper.saveState(this, DBHelper.NOT_RECEIVED);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public int getIsQueryReceived() {
        Cursor cursor = getContentResolver().query(
                MyContentProvider.STATE_CONTENT_URL, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor != null) {
            if (cursor.getCount() == 0 ) {
                return 0;
            }
        }
        return cursor != null ?
                cursor.getInt(cursor.getColumnIndex(MyContentProvider.COLUMN_IS_RECEIVED)) : 0;
    }

    public class QueryCompletedReceiver extends BroadcastReceiver {
        private final String LOG_TAG = QueryCompletedReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("QueryCompletedReceiver", "onReceive");
            final long requestId = intent.getLongExtra(NetworkService.EXTRA_REQUEST_ID, 0);
            final int status = intent.getIntExtra(NetworkService.EXTRA_RESULT_CODE, 0);
            myProgressDialog.dismissProgressDialog();
            if (mUrlRequestId == requestId) {
                switch (status) {
                    case Response.RESULT_OK:
                        handleResultOk();
                        break;
                    case Response.RESULT_CANNOT_SEND_MESSAGE:
                        handleCanNotSendMessage();
                        break;
                    case Response.RESULT_UNEXPECTED_ERROR:
                        Toast.makeText(MainActivity.this, R.string.internal_auth_client_error_text,
                                Toast.LENGTH_SHORT).show();
                    default:
                        handleErrorMessage(intent);
                }
            }
        }

        private void handleResultOk() {
            myFragmentManager.addResponseFragment();
        }

        private void handleCanNotSendMessage() {
            Toast.makeText(MainActivity.this, R.string.unreachechanable_url, Toast.LENGTH_SHORT).show();
        }

        private void handleErrorMessage(@NonNull Intent intent) {
            String errorMessage = intent.getStringExtra(NetworkService.ERROR_MESSAGE);
            Log.w(LOG_TAG, "method handleErrorMessage: " + errorMessage);
        }
    }

    @Override
    public void onBackPressed() {
        if (myFragmentManager.getLastFragment() == myFragmentManager.responseFragmentPosition) {
            myFragmentManager.addRequestFragment();
            DBHelper dbHelper = new DBHelper();
            dbHelper.saveState(this, DBHelper.NOT_RECEIVED);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onCreate ", String.valueOf(shouldShowProgressDialog));
        int isReceived = getIsQueryReceived();
        if (isReceived == 0) {
            myProgressDialog.continueShowProgressDialog(shouldShowProgressDialog, progressDialogText);
        }
        myFragmentManager.addFragmentReceived(isReceived);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("onStart", "registerReceiver");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mQueryCompletedReceiver, new IntentFilter(ServiceHelper.ACTION_REQUEST_RESULT));
    }

    @Override
    protected void onStop() {
        Log.d("onStart", "unregisterReceiver");
        super.onStop();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mQueryCompletedReceiver);
        } catch (Exception ignore) {}
        myProgressDialog.dismissProgressDialog();
    }

    @Override

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt("lastFragment", myFragmentManager.getLastFragment());
        state.putLong("mUrlRequestId", mUrlRequestId);
        state.putBoolean("shouldShowProgressDialog", myProgressDialog.isShouldShowProgressDialog());
        state.putString("progressDialogText", myProgressDialog.getDialogText());
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        myFragmentManager.addFragment(savedInstanceState.getInt("lastFragment"));
        mUrlRequestId = savedInstanceState.getLong("mUrlRequestId");
        shouldShowProgressDialog = savedInstanceState.getBoolean("shouldShowProgressDialog");
        progressDialogText = savedInstanceState.getString("progressDialogText");
    }
}
