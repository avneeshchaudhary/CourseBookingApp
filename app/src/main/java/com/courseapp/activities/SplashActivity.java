package com.courseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.courseapp.R;
import com.courseapp.activities.authentication.LoginActivity;

import static com.courseapp.AppCredentials.APP_DB;
import static com.courseapp.AppCredentials.AUTHENTICATED;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences(APP_DB, MODE_PRIVATE);
                if (preferences.getBoolean(AUTHENTICATED, false)) {
                    startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                }
                else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, 2000);
    }
}