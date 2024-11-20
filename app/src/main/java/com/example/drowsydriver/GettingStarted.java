package com.example.drowsydriver;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.drowsydriver.utils.StorageUtil;

public class GettingStarted extends AppCompatActivity {
    AppCompatButton getStartBtn;
    AppCompatButton aboutBtn;
    StorageUtil storageUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_getting_started);

        initWidgets();
         storageUtil = new StorageUtil(this);
        getStartBtn.setOnClickListener(v->{gotoHome();});
        aboutBtn.setOnClickListener(v->{gotoAbout();});

    }

    private void gotoHome() {
        storageUtil.setFirstVisit(false);
        startActivity(new Intent(getApplicationContext(), MainActivity.class ));
    }

    private void gotoAbout() {
        storageUtil.setFirstVisit(false);
        startActivity(new Intent(getApplicationContext(), About.class ));
    }

    private void initWidgets() {
        getStartBtn = findViewById(R.id.getStarted_Btn);
        aboutBtn = findViewById(R.id.about_Btn);
    }
}