package com.example.quizapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_quiz_menu extends AppCompatActivity {

    SQLiteDatabase QuizDB;
    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        QuizDB = openOrCreateDatabase("QuizDB", MODE_PRIVATE, null);
        Cursor c = QuizDB.rawQuery("SELECT DISTINCT QuizTitle FROM quiz", null);

        mainLayout = findViewById(R.id.main);

        while (c.moveToNext()) {
            LayoutInflater inflater = LayoutInflater.from(this);
            Button SelectBtn = (Button) inflater.inflate(R.layout.select_button, mainLayout, false);
            SelectBtn.setText(c.getString(0));
            SelectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity_quiz_menu.this, activity_quiz.class);
                    intent.putExtra("key_quiz", SelectBtn.getText());
                    startActivity(intent);
                }
            });
            mainLayout.addView(SelectBtn);
        }

    }
}