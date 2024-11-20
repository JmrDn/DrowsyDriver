package com.example.drowsydriver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.drowsydriver.utils.StorageUtil;

public class SplashScreen extends AppCompatActivity {

    StorageUtil storageUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        storageUtil = new StorageUtil(this);
        boolean isFirstVisit = storageUtil.getIsFirstVisit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isFirstVisit){

                    startActivity(new Intent(getApplicationContext(), GettingStarted.class));
                } else{
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        }, 3000);

    }
}