package com.tecstudyap.sinkuquiz;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicyForQuizinku extends AppCompatActivity {
    WebView webView;
    ImageView closeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy_for_quizinku);


        closeBtn = findViewById(R.id.closeBtn);

        webView = findViewById(R.id.webViewterms);
        webView.loadUrl("file:///android_asset/privacypolicy.html");

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



}