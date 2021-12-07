package com.tecstudyap.sinkuquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tecstudyap.sinkuquiz.databinding.ActivityResultBinding;

public class ResultActivity extends AppCompatActivity {
    ActivityResultBinding binding;
    InterstitialAd mInterstitialAd;
    int POINTS = 5;
   // int POINTSMinus = -5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                interstitialAdLoad();

            }
        });

        int correctAnswers = getIntent().getIntExtra("correct", 0);
        int totalQuestions = getIntent().getIntExtra("total", 0);
       // int wrongAnswers = getIntent().getIntExtra("wrong",0);

        long points = correctAnswers * POINTS;
   //  long pointscut = wrongAnswers*POINTSMinus;

        binding.score.setText(String.format("%d/%d", correctAnswers, totalQuestions));
        binding.earnedCoins.setText(String.valueOf(points));
    //   binding.earnedCoins.setText(String.valueOf(pointscut));



        FirebaseFirestore database = FirebaseFirestore.getInstance();


           database.collection("users")
                   .document(FirebaseAuth.getInstance().getUid())
                   .update("coins", FieldValue.increment(points));


//           database.collection("users")
//                   .document(FirebaseAuth.getInstance().getUid())
//                   .update("coins", FieldValue.increment(pointscut));



        binding.restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInterstitialAd();
               // startActivity(new Intent(ResultActivity.this, MainActivity.class));
               finishAffinity();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ResultActivity.this, MainActivity.class));
        finishAffinity();
    }


    public void interstitialAdLoad() {

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, getString(R.string.interstitialAd_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        //  Log.d("Admob interstitial", "onAdLoaded");

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                //interstitialAdLoad();
                                startActivity(new Intent(ResultActivity.this, MainActivity.class));
                                finishAffinity();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                // Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                // Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        //Log.d("Admob interstitial", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });


    }


    private void showInterstitialAd(){

        if (mInterstitialAd != null) {
            mInterstitialAd.show(ResultActivity.this);
        } else {

            //Toast.makeText(MainActivity.this, "yourmessage", Toast.LENGTH_SHORT).show();
        }
    }

}
