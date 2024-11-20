package com.example.drowsydriver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class About extends AppCompatActivity {
    AppCompatButton backToHomeBtn;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);

        backToHomeBtn = findViewById(R.id.backToHome_Button);

        backToHomeBtn.setOnClickListener(v->{gotoHome();});

    }

    private void gotoHome() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}