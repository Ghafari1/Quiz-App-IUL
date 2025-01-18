package com.example.quizapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    SQLiteDatabase QuizDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        QuizDB = openOrCreateDatabase("QuizDB", MODE_PRIVATE, null);
        QuizDB.execSQL("CREATE TABLE IF NOT EXISTS quiz(ID INTEGER PRIMARY KEY AUTOINCREMENT, QuizTitle TEXT, QuizQuestion TEXT, QuizAnswers TEXT, QuizCorrectAnswer TEXT)");

        Button startQuizBtn = findViewById(R.id.StartBtn);
        Button createQuizBtn = findViewById(R.id.CreateBtn);

        startQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, activity_quiz_menu.class);
                startActivity(intent);
            }
        });

        createQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, activity_create.class);
                startActivity(intent);
            }
        });
    }
}