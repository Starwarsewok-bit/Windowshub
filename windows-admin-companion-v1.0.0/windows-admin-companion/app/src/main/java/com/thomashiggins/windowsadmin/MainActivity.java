package com.thomashiggins.windowsadmin;

import android.app.AlertDialog;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String START_URL = "https://192.168.0.105:6600";
    private static final String ALLOWED_HOST = "192.168.0.105";
    private WebView web;
    private ProgressBar progress;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(100);
        root.addView(progress, new LinearLayout.LayoutParams(-1, dp(3)));
        web = new WebView(this);
        root.addView(web, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);

        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setDatabaseEnabled(true);
        web.getSettings().setMediaPlaybackRequiresUserGesture(false);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        android.webkit.CookieManager.getInstance().setAcceptCookie(true);
        android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(web, true);

        web.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int value) {
                progress.setProgress(value);
                progress.setVisibility(value == 100 ? View.GONE : View.VISIBLE);
            }
        });
        web.setWebViewClient(new WebViewClient() {
            @Override public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Uri uri = Uri.parse(error.getUrl());
                if (ALLOWED_HOST.equals(uri.getHost()) && uri.getPort() == 6600) handler.proceed();
                else handler.cancel();
            }

            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (ALLOWED_HOST.equals(uri.getHost()) && uri.getPort() == 6600) return false;
                Toast.makeText(MainActivity.this, "Blocked link outside HOMESERVER", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                showLogin(handler);
            }
        });

        if (state == null) web.loadUrl(START_URL); else web.restoreState(state);
    }

    @Override public void onBackPressed() {
        if (web.canGoBack()) web.goBack(); else super.onBackPressed();
    }

    private void showLogin(HttpAuthHandler handler) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = dp(20); box.setPadding(pad, pad / 2, pad, 0);
        EditText user = new EditText(this); user.setHint("Windows username");
        EditText pass = new EditText(this); pass.setHint("Password"); pass.setInputType(0x81);
        box.addView(user); box.addView(pass);
        new AlertDialog.Builder(this).setTitle("Sign in to HOMESERVER").setView(box)
            .setPositiveButton("Sign in", (d, w) -> handler.proceed(user.getText().toString(), pass.getText().toString()))
            .setNegativeButton("Cancel", (d, w) -> handler.cancel()).setOnCancelListener(d -> handler.cancel()).show();
    }

    @Override protected void onSaveInstanceState(Bundle out) { web.saveState(out); super.onSaveInstanceState(out); }
    @Override protected void onDestroy() { web.destroy(); super.onDestroy(); }
    private int dp(int n) { return Math.round(n * getResources().getDisplayMetrics().density); }
}
