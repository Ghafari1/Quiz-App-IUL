package com.example.quizapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public class activity_quiz extends AppCompatActivity implements View.OnClickListener {

    SQLiteDatabase QuizDB;
    TextView questionTextView;
    Button ansA, ansB, ansC, ansD;
    Button nextBtn;

    int TotalQuestion = 4;
    int correctAnswer = 0;
    int currentQuestion = 0;
    int score = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        questionTextView = findViewById(R.id.question);
        ansA = findViewById(R.id.answerA);
        ansB = findViewById(R.id.answerB);
        ansC = findViewById(R.id.answerC);
        ansD = findViewById(R.id.answerD);
        nextBtn = findViewById(R.id.loadNext);

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        loadNewQuestion();
    }


    @Override
    public void onClick(View v) {

        String quizKey = String.valueOf(getIntent().getStringExtra("key_quiz"));

        QuizDB = openOrCreateDatabase("QuizDB", MODE_PRIVATE, null);
        Cursor c = QuizDB.rawQuery("SELECT QuizCorrectAnswer FROM quiz WHERE QuizTitle ='" + quizKey + "'", null);

        ArrayList<String> QuizCorrectAnswers = new ArrayList<>();

        while (c.moveToNext()) {
            QuizCorrectAnswers.add(c.getString(0));
        }

        String QuizCAnswer = QuizCorrectAnswers.get(currentQuestion).trim();



        Button clickedBtn = (Button) v;

        System.out.println(clickedBtn.getText());
        System.out.println(QuizCAnswer);

        if(Objects.equals(QuizCAnswer, clickedBtn.getText())) {
            clickedBtn.setBackgroundColor(Color.GREEN);
            ++correctAnswer;
            score += 25;
            System.out.println("CLICKED!");
        }

        else if(clickedBtn.getId() != R.id.loadNext) {

            String ansAString = (String) ansA.getText();
            String ansBString = (String) ansB.getText();
            String ansCString = (String) ansC.getText();
            String ansDString = (String) ansD.getText();

            if(Objects.equals(QuizCAnswer, ansAString)) {
                ansA.setBackgroundColor(Color.GREEN);
                ansB.setBackgroundColor(Color.RED);
                ansC.setBackgroundColor(Color.RED);
                ansD.setBackgroundColor(Color.RED);
            } else if (Objects.equals(QuizCAnswer, ansBString)) {
                ansA.setBackgroundColor(Color.RED);
                ansB.setBackgroundColor(Color.GREEN);
                ansC.setBackgroundColor(Color.RED);
                ansD.setBackgroundColor(Color.RED);
            } else if (Objects.equals(QuizCAnswer, ansCString)) {
                ansA.setBackgroundColor(Color.RED);
                ansB.setBackgroundColor(Color.RED);
                ansC.setBackgroundColor(Color.GREEN);
                ansD.setBackgroundColor(Color.RED);
            } else if (Objects.equals(QuizCAnswer, ansDString)) {
                ansA.setBackgroundColor(Color.RED);
                ansB.setBackgroundColor(Color.RED);
                ansC.setBackgroundColor(Color.RED);
                ansD.setBackgroundColor(Color.GREEN);
            }

        }

        ansA.setEnabled(false);
        ansB.setEnabled(false);
        ansC.setEnabled(false);
        ansD.setEnabled(false);

        nextBtn.setEnabled(true);

        if(clickedBtn.getId() == R.id.loadNext) {
            ++currentQuestion;
            loadNewQuestion();
        }
    }

    void loadNewQuestion() {

        if(currentQuestion < TotalQuestion) {

            String quizKey = String.valueOf(getIntent().getStringExtra("key_quiz"));

            QuizDB = openOrCreateDatabase("QuizDB", MODE_PRIVATE, null);
            Cursor c = QuizDB.rawQuery("SELECT QuizQuestion, QuizAnswers FROM quiz WHERE QuizTitle ='" + quizKey + "'", null);

            ArrayList<String> questions = new ArrayList<>();
            ArrayList<String> QuizAnswers = new ArrayList<>();

            while (c.moveToNext()) {
                questions.add(c.getString(0));
                QuizAnswers.add(c.getString(1));
            }

            String[] Questions = questions.toArray(new String[0]);

            String QuizAnswer = QuizAnswers.get(currentQuestion).substring(1, QuizAnswers.get(currentQuestion).length() - 1);
            String[] QuizAnswersArray = QuizAnswer.split(",");

            ansA.setEnabled(true);
            ansB.setEnabled(true);
            ansC.setEnabled(true);
            ansD.setEnabled(true);


            ansA.setBackgroundColor(Color.BLUE);
            ansB.setBackgroundColor(Color.BLUE);
            ansC.setBackgroundColor(Color.BLUE);
            ansD.setBackgroundColor(Color.BLUE);

            nextBtn.setEnabled(false);

            questionTextView.setText(Questions[currentQuestion]);

            ansA.setText(QuizAnswersArray[0].trim());
            ansB.setText(QuizAnswersArray[1].trim());
            ansC.setText(QuizAnswersArray[2].trim());
            ansD.setText(QuizAnswersArray[3].trim());
        } else {
            Intent intent = new Intent(activity_quiz.this, activity_score.class);
            intent.putExtra("key_score", score);
            intent.putExtra("key_correctAnswer", correctAnswer);
            intent.putExtra("key_answer_number", TotalQuestion);
            startActivity(intent);
        }
    }
}