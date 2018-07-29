package com.app.loyalme;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.app.loyalme.helper.SessionManager;
import com.app.loyalme.model.UserInfo;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private Runnable runnable;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int SPLASH_TIME_OUT = 1500;
        sessionManager = new SessionManager(SplashActivity.this);


        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {

                if(sessionManager.isLoggedIn()) {

                    UserInfo info = sessionManager.getUserInfo();
                    if(info.getUserType().equalsIgnoreCase("user")){

                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else if(info.getUserType().equalsIgnoreCase("retailer")){
                        Intent intent = new Intent(SplashActivity.this, RetailerActivity.class);
                        startActivity(intent);
                    }
                    finish();

                } else {

                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }


    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if(Build.VERSION.SDK_INT >= 23){
            if (makeTranslucent) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }
}
