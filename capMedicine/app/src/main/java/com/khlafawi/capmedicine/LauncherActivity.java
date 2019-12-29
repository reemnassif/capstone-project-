package com.khlafawi.capmedicine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import pref.UserSession;

public class LauncherActivity extends AppCompatActivity {

    private UserSession session;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        session = new UserSession(this);
        statusBar();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (session.isUserLogged()) {
                    startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(LauncherActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 2000);
    }

    private void statusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }
}
