package esaph.filing.rechtliches;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import esaph.filing.R;
import esaph.filing.Utils.EsaphActivity;

public class Haftungsausschluss extends EsaphActivity
{
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haftungsausschluss);

        button = findViewById(R.id.activityHaftungsausschlussTextView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent data = new Intent();
                setResult(420, data);
                finish();
            }
        });

        WebView webView = findViewById(R.id.activityHaftungsauschlussWebView);
        webView.loadUrl("file:///android_asset/Haftungsausschluss.html");
    }
}
