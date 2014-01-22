package com.sjl.download;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ExampleDownloadActivity extends Activity {

    private static final String URL = "http://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss";
    private static final int RSS_DOWNLOAD_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // when the start button is tapped, download and parse using the intentservice
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PendingIntent pendingResult = createPendingResult(
                        RSS_DOWNLOAD_REQUEST_CODE, new Intent(), 0);
                Intent intent = new Intent(getApplicationContext(), DownloadIntentService.class);
                intent.putExtra(DownloadIntentService.URL_EXTRA, URL);
                intent.putExtra(DownloadIntentService.PENDING_RESULT_EXTRA, pendingResult);
                startService(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RSS_DOWNLOAD_REQUEST_CODE) {
            switch (resultCode) {
                case DownloadIntentService.INVALID_URL_CODE:
                    handleInvalidURL();
                    break;
                case DownloadIntentService.ERROR_CODE:
                    handleError(data);
                    break;
                case DownloadIntentService.RESULT_CODE:
                    handleRSS(data);
                    break;
            }
            handleRSS(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleRSS(Intent data) {
        IllustrativeRSS rss = data.getParcelableExtra(DownloadIntentService.RSS_RESULT_EXTRA);
        ViewGroup result = (ViewGroup)findViewById(R.id.results);
        result.removeAllViews();
        for (int i=0; i<rss.size(); i++) {
            IllustrativeRSS.Item item = rss.get(i);
            TextView v = new TextView(this);
            v.setText(item.title);
            result.addView(v);
        }
    }

    private void handleError(Intent data) {
        // whatever you want
    }

    private void handleInvalidURL() {
        // whatever you want
    }
}
