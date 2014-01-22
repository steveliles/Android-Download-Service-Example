package com.sjl.download;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadIntentService extends IntentService {

    private static final String TAG = DownloadIntentService.class.getSimpleName();

    public static final String PENDING_RESULT_EXTRA = "pending_result";
    public static final String URL_EXTRA = "url";
    public static final String RSS_RESULT_EXTRA = "url";

    public static final int RESULT_CODE = 0;
    public static final int INVALID_URL_CODE = 1;
    public static final int ERROR_CODE = 2;

    private IllustrativeRSSParser parser;

    public DownloadIntentService() {
        super(TAG);

        // make one and re-use, in the case where more than one intent is queued
        parser = new IllustrativeRSSParser();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PendingIntent reply = intent.getParcelableExtra(PENDING_RESULT_EXTRA);
        InputStream in = null;
        try {
            try {
                URL url = new URL(intent.getStringExtra(URL_EXTRA));
                IllustrativeRSS rss = parser.parse(in = url.openStream());

                Intent result = new Intent();
                result.putExtra(RSS_RESULT_EXTRA, rss);

                reply.send(this, RESULT_CODE, result);
            } catch (MalformedURLException exc) {
                reply.send(INVALID_URL_CODE);
            } catch (Exception exc) {
                // could do better by treating the different sax/xml exceptions individually
                reply.send(ERROR_CODE);
            }
        } catch (PendingIntent.CanceledException exc) {
            Log.i(TAG, "reply cancelled", exc);
        }
    }
}
