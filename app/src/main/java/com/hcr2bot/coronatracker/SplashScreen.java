package com.hcr2bot.coronatracker;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        ImageView imageView = findViewById(R.id.covidSplash);
        Glide.with(this).load(R.drawable.splash).into(imageView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent Home = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(Home);
                finish();
            }
        }, 5000);

    }

}