package com.example.budgetkitaapp.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.budgetkitaapp.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();  //HIDE TOOLBAR

        new Handler().postDelayed(new Runnable(){
            //show the app logo and go to next activity using intent
            public void run() {
                startActivity(new Intent( SplashActivity.this, MainActivity.class));
                finish();
            }
        },5000);
    }
}