package tr.com.akifsafayildiz.kampanyakala;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class CampaignIntent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_intent);

        WebView webView;
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true); // enable javascript

        webView.loadUrl("http://akifsafayildiz.com.tr/projeler/kioskuygulama/urun_migros2/firstPage.html");


    }

}
