package com.lexaloris.yotabrowser.network.result_receivers;

import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import com.lexaloris.yotabrowser.network.processors.Processor;
import com.lexaloris.yotabrowser.network.service.NetworkService;
import com.lexaloris.yotabrowser.network.service.Response;

public class HtmlSourceProcessorResultListener implements Processor.OnProcessorResultListener {

    private final Intent mOriginalRequestIntent;
    private final ResultReceiver mResultReceiver;

    public HtmlSourceProcessorResultListener(Intent originalRequestIntent, ResultReceiver resultReceiver) {
        mOriginalRequestIntent = originalRequestIntent;
        mResultReceiver = resultReceiver;
    }

    @Override
    public void send(@Nullable Response response) {
        if (response == null)
            return;

        if (mResultReceiver != null) {
            Bundle result;
            result = handleResultOk();

            mResultReceiver.send(response.getStatus(), result);
        }
    }

    @Nullable
    private Bundle handleResultOk() {
        Bundle result = new Bundle();
        result.putParcelable(NetworkService.EXTRA_ORIGINAL_INTENT, mOriginalRequestIntent);
        return result;
    }

}
