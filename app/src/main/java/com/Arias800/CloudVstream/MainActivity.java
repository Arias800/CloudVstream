package com.Arias800.CloudVstream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    private boolean debugMode = false;
    private WebView mWebView;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.evaluateJavascript(
                        "document.documentElement.innerHTML",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String html) {
                                if (html.contains("Checking your browser before accessing") == false) {
                                    createFile(html);
                                }
                            }
                        });
            }
        });

        String yourFilePath = "/storage/emulated/0/Android/data/org.xbmc.kodi/files/.kodi/userdata/addon_data/plugin.video.vstream/url.txt";
        File yourFile = new File( yourFilePath );

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(yourFile));

            String line = br.readLine();
            br.close();

            // REMOTE RESOURCE
            mWebView.loadUrl(line);

        } catch (Exception e) {
            System.out.println(e.getClass());
        }
    }


    public static StringBuffer removeUTFCharacters(String data){
        Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
        Matcher m = p.matcher(data);
        StringBuffer buf = new StringBuffer(data.length());
        while (m.find()) {
            String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
            m.appendReplacement(buf, Matcher.quoteReplacement(ch));
        }
        m.appendTail(buf);
        return buf;
    }

    public void createFile(String data){
        try {
            File gpxfile = new File("/storage/emulated/0/Android/data/org.xbmc.kodi/files/.kodi/userdata/addon_data/plugin.video.vstream/", "content.html");
            FileWriter writer = new FileWriter(gpxfile);

            //Traitement du resultat
            data = removeUTFCharacters(data).toString();
            data = data.substring( 1, data.length() - 1 );
            data = data.replaceAll("\\\\n", " ");
            data = data.replaceAll("\\\\","");
            if (debugMode) {
                Log.i("Content", data);
            }
            writer.append(data);

            writer.flush();
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
