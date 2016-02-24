package com.lexaloris.yotabrowser.network.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import com.lexaloris.yotabrowser.network.processors.HtmlSourceProcessor;
import com.lexaloris.yotabrowser.network.processors.Processor;
import com.lexaloris.yotabrowser.network.result_receivers.HtmlSourceProcessorResultListener;

public class NetworkService extends IntentService {

    public static final String EXTRA_ORIGINAL_INTENT = "com.lexaloris.yotabrowser.network.service.extra.EXTRA_ORIGINAL_INTENT";
    public static final String EXTRA_RESULT_CODE = "com.lexaloris.yotabrowser.network.service.extra.EXTRA_RESULT_CODE";
    public static final String ACTION_HTML_SOURCE = "com.lexaloris.yotabrowser.network.service.action.HTML_Source";
    public static final String EXTRA_REQUEST_ID = "com.lexaloris.yotabrowser.network.service.extra.EXTRA_REQUEST_ID";
    public static final String EXTRA_URL = "com.lexaloris.yotabrowser.network.service.extra.EXTRA_URL";
    public static final String ERROR_MESSAGE = "ru.mail.park.bughouse.network.service.extra.ERROR_MESSAGE";

    public NetworkService() {
        super("YotaBrowserNetworkService");
    }

    private static final String EXTRA_SERVICE_CALLBACK = "EXTRA_SERVICE_CALLBACK";
    public static void startHtmlSourceService(Context context, ResultReceiver serviceCallback, long requestId, String urlText) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_HTML_SOURCE);
        intent.putExtra(EXTRA_SERVICE_CALLBACK, serviceCallback);
        intent.putExtra(EXTRA_REQUEST_ID, requestId);
        intent.putExtra(EXTRA_URL, urlText);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver resultReceiver = intent.getParcelableExtra(EXTRA_SERVICE_CALLBACK);
        final String action = intent.getAction();
        switch (action) {
            case ACTION_HTML_SOURCE:
                final String extraUrl = intent.getStringExtra(EXTRA_URL);
                Processor signOutProcessor = new HtmlSourceProcessor(this, new HtmlSourceProcessorResultListener(intent, resultReceiver), extraUrl);
                signOutProcessor.process();
                break;
        }
    }
}
