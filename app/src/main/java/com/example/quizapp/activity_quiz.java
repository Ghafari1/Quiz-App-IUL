package com.example.quizapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


public class activity_quiz extends AppCompatActivity implements View.OnClickListener {

    TextView questionTextView;
    Button ansA, ansB, ansC, ansD;
    Button nextBtn;
    ProgressBar LoadingQuestion;
    String userKey;
    String quizKey;
    boolean isTest;
    int TotalQuestion = 4;
    int correctAnswer = 0;
    int currentQuestion = 0;
    int score = 0;
    ArrayList<String> questions;
    ArrayList<String> q1_answers;
    ArrayList<String> q2_answers;
    ArrayList<String> q3_answers;
    ArrayList<String> q4_answers;
    ArrayList<String> correct_answers;

    @SuppressLint("MissingInflatedId")
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

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        LoadingQuestion = findViewById(R.id.questionLoading);
        isTest = getIntent().getBooleanExtra("isTest", true);
        userKey = String.valueOf(getIntent().getStringExtra("key_user"));
        quizKey = String.valueOf(getIntent().getStringExtra("key_quiz"));

        DocumentReference docRef = database.collection("users").document(userKey).collection("quizzes").document(quizKey);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();

                    questions  = (ArrayList<String>) document.get("questions");
                    q1_answers = (ArrayList<String>) document.get("q1_answers");
                    q2_answers = (ArrayList<String>) document.get("q2_answers");
                    q3_answers = (ArrayList<String>) document.get("q3_answers");
                    q4_answers = (ArrayList<String>) document.get("q4_answers");
                    correct_answers = (ArrayList<String>) document.get("correct_answers");

                    questionTextView = findViewById(R.id.question);
                    ansA = findViewById(R.id.answerA);
                    ansB = findViewById(R.id.answerB);
                    ansC = findViewById(R.id.answerC);
                    ansD = findViewById(R.id.answerD);
                    nextBtn = findViewById(R.id.loadNext);

                    ansA.setOnClickListener(activity_quiz.this);
                    ansB.setOnClickListener(activity_quiz.this);
                    ansC.setOnClickListener(activity_quiz.this);
                    ansD.setOnClickListener(activity_quiz.this);
                    nextBtn.setOnClickListener(activity_quiz.this);

                    loadNewQuestion();
                }

            }
        });
    }


    @Override
    public void onClick(View v) {

        String QuizCAnswer = correct_answers.get(currentQuestion).trim();
        MediaPlayer correctAns = MediaPlayer.create(this, R.raw.right_answer);

        Button clickedBtn = (Button) v;

        System.out.println(clickedBtn.getText());
        System.out.println(QuizCAnswer);

        if(Objects.equals(QuizCAnswer, clickedBtn.getText())) {
            clickedBtn.setBackgroundColor(Color.GREEN);
            correctAns.start();

            ++correctAnswer;
            score += 25;
            System.out.println("CLICKED!");
        }

        else if(clickedBtn.getId() != R.id.loadNext) {
            MediaPlayer wrongAns = MediaPlayer.create(this, R.raw.wrong_answer);
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
            wrongAns.start();
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
        LoadingQuestion.setVisibility(View.GONE);
        ansA.setVisibility(View.VISIBLE);
        ansB.setVisibility(View.VISIBLE);
        ansC.setVisibility(View.VISIBLE);
        ansD.setVisibility(View.VISIBLE);
        nextBtn.setVisibility(View.VISIBLE);
        questionTextView.setVisibility(View.VISIBLE);

        if(currentQuestion < TotalQuestion) {

            ArrayList<ArrayList<String>> allQuestionAnswers = new ArrayList<>();
            allQuestionAnswers.add(q1_answers);
            allQuestionAnswers.add(q2_answers);
            allQuestionAnswers.add(q3_answers);
            allQuestionAnswers.add(q4_answers);



            ansA.setEnabled(true);
            ansB.setEnabled(true);
            ansC.setEnabled(true);
            ansD.setEnabled(true);


            ansA.setBackgroundColor(Color.BLUE);
            ansB.setBackgroundColor(Color.BLUE);
            ansC.setBackgroundColor(Color.BLUE);
            ansD.setBackgroundColor(Color.BLUE);

            ansA.setTextColor(Color.WHITE);
            ansB.setTextColor(Color.WHITE);
            ansC.setTextColor(Color.WHITE);
            ansD.setTextColor(Color.WHITE);

            nextBtn.setEnabled(false);
            nextBtn.setTextColor(Color.BLACK);

            questionTextView.setText(questions.get(currentQuestion));

            ansA.setText(allQuestionAnswers.get(currentQuestion).get(0).trim());
            ansB.setText(allQuestionAnswers.get(currentQuestion).get(1).trim());
            ansC.setText(allQuestionAnswers.get(currentQuestion).get(2).trim());
            ansD.setText(allQuestionAnswers.get(currentQuestion).get(3).trim());
        } else {
            Intent intent = new Intent(activity_quiz.this, activity_score.class);
            intent.putExtra("key_quizID", quizKey);
            intent.putExtra("key_score", score);
            intent.putExtra("key_correctAnswer", correctAnswer);
            intent.putExtra("key_answer_number", TotalQuestion);
            intent.putExtra("isTest", isTest);
            startActivity(intent);
        }
    }
}