package com.example.quizapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_statistic extends AppCompatActivity {

    TextView gold, silver, bronze, totalScore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistic);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPref = getSharedPreferences("user_info", MODE_PRIVATE);

        int gold_awards = sharedPref.getInt("gold awards", 0);  // "default_username" is a fallback if not found
        int silver_awards = sharedPref.getInt("silver awards", 0);  // "0" is a fallback if not found
        int bronze_awards = sharedPref.getInt("bronze awards", 0);
        int score = sharedPref.getInt("score", 0);



        gold = findViewById(R.id.goldMedals);
        silver = findViewById(R.id.silverMedals);
        bronze = findViewById(R.id.bronzeMedals);
        totalScore = findViewById(R.id.TotalScore);

        gold.setText("Gold Awards: " + String.valueOf(gold_awards));
        silver.setText("Silver Awards: " + String.valueOf(silver_awards));
        bronze.setText("Bronze Awards: " + String.valueOf(bronze_awards));

        totalScore.setText(String.valueOf(score));

    }
}