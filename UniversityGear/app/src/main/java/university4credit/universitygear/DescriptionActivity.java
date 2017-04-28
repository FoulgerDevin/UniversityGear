package university4credit.universitygear;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

/**
 * Created by Devin on 4/24/2017.
 */

public class DescriptionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        Intent webIntent = getIntent();
        final String webData = webIntent.getStringExtra("itemURL");

        WebView descriptionWebview = (WebView)findViewById(R.id.descriptionWebView);

        descriptionWebview.loadData(webData, "text/html; charset=utf-8", "UTF-8");
    }
}
