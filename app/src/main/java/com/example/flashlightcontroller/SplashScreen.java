package com.example.flashlightcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SplashScreen extends AppCompatActivity {

    ImageView splashScreenImageView;
    ProgressBar progressBar;
    Handler imageViewHandler;
    int cnt = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splashScreenImageView = findViewById(R.id.splash_screen_imageview);
        progressBar = findViewById(R.id.splash_screen_progressbar);

        imageViewHandler = new Handler();
        imageViewHandler.postDelayed(changeImage,500);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0;i<=100;i++){
                    progressBar.setProgress(i);
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t1.start();
    }

    private final Runnable changeImage = new Runnable() {
        @Override
        public void run() {
            if (cnt==0){
                splashScreenImageView.setImageResource(R.drawable.flash_on);
            }else if (cnt==1){
                splashScreenImageView.setImageResource(R.drawable.flash_off);
            }else if (cnt==3){
                Intent i1 = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(i1);
                finish();
            }
            cnt++;
            imageViewHandler.postDelayed(this,500);
        }
    };
}