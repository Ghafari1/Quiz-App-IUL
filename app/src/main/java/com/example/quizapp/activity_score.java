package com.example.quizapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_score extends AppCompatActivity {

    TextView TextScore, TextCorrectAnswer;

    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextScore = findViewById(R.id.score);
        TextCorrectAnswer = findViewById(R.id.correct_answer);

        String Score = String.valueOf(getIntent().getIntExtra("key_score", 0));
        String numberOfAnswer = String.valueOf(getIntent().getIntExtra("key_answer_number", 0));
        String CorrectAnswer = String.valueOf(getIntent().getIntExtra("key_correctAnswer", 0));

        TextScore.setText("Score: " + Score);
        TextCorrectAnswer.setText("Correct Answers: " + CorrectAnswer + "/" + numberOfAnswer);

        Button StartQuizBtn = findViewById(R.id.restBtn);
        StartQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_score.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}