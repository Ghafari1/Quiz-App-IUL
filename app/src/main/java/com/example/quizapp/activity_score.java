package com.example.quizapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class activity_score extends AppCompatActivity {

    TextView TextScore, TextCorrectAnswer;
    ImageView win_award;
    boolean isTest;
    int updatedScore;
    Map<String, Object> emptyData = new HashMap<>();

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

        String awardsType;
        String quizID = getIntent().getStringExtra("key_quizID");

        TextScore = findViewById(R.id.score);
        TextCorrectAnswer = findViewById(R.id.correct_answer);
        win_award = findViewById(R.id.win_award);

        int Score = getIntent().getIntExtra("key_score", 0);
        String numberOfAnswer = String.valueOf(getIntent().getIntExtra("key_answer_number", 0));
        String CorrectAnswer = String.valueOf(getIntent().getIntExtra("key_correctAnswer", 0));
        isTest = getIntent().getBooleanExtra("isTest", true);

        switch (Score) {
            case 50:
                awardsType = "bronze awards";
                win_award.setImageResource(R.drawable.bronze_win);
                break;
            case 75:
                awardsType = "silver awards";
                win_award.setImageResource(R.drawable.silver_win);
                break;
            case 100:
                awardsType = "gold awards";
                win_award.setImageResource(R.drawable.gold_win);
                break;
            default:
                awardsType = "no awards";
                break;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();



        if(quizID != null && !isTest && !awardsType.equals("no awards")) {

            database.collection("users")
                    .document(auth.getCurrentUser().getUid())
                    .collection("completedQuiz")
                    .document(quizID)
                    .set(emptyData)  // Clear the data (or you can use .delete() to remove the document)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                database.collection("users")
                                        .document(auth.getCurrentUser().getUid())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful() && task.getResult() != null) {
                                                    String currentScore = String.valueOf(task.getResult().get("score"));
                                                    updatedScore = Score + Integer.parseInt(currentScore);
                                                    database.collection("users")
                                                            .document(auth.getCurrentUser().getUid())
                                                            .update("score", updatedScore);
                                                }
                                            }
                                        })
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful() && !awardsType.equals("no awards")) {
                                                    database.collection("users")
                                                            .document(auth.getCurrentUser().getUid())
                                                            .update(awardsType, FieldValue.increment(1));

                                                }
                                            }
                                        })
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    Button backMenu = findViewById(R.id.restBtn);
                                                    backMenu.setEnabled(true);

                                                    TextScore.setText("Score: " + Score);
                                                    TextCorrectAnswer.setText("Correct Answers: " + CorrectAnswer + "/" + numberOfAnswer);

                                                    if(!awardsType.equals("no awards")) {

                                                        SharedPreferences sharedPref = getSharedPreferences("user_info", MODE_PRIVATE);
                                                        int old_awards = sharedPref.getInt(awardsType, 0);  // "0" is a fallback if not found

                                                        SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putInt("score", updatedScore);
                                                        editor.putInt(awardsType, old_awards + 1);
                                                        editor.apply();
                                                    }

                                                    backMenu.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(activity_score.this, MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                            }
                        }
                    });



        } else {

            if(Score > 0 && !isTest) {
                database.collection("users")
                        .document(auth.getCurrentUser().getUid())
                        .collection("completedQuiz")
                        .document(quizID)
                        .set(emptyData)  // Clear the data (or you can use .delete() to remove the document)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    database.collection("users")
                                            .document(auth.getCurrentUser().getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful() && task.getResult() != null) {
                                                        String currentScore = String.valueOf(task.getResult().get("score"));
                                                        updatedScore = Score + Integer.parseInt(currentScore);
                                                        database.collection("users")
                                                                .document(auth.getCurrentUser().getUid())
                                                                .update("score", updatedScore);
                                                    }
                                                }
                                            })
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    SharedPreferences sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putInt("score", updatedScore);
                                                    editor.apply();

                                                    Button backMenu = findViewById(R.id.restBtn);
                                                    backMenu.setEnabled(true);

                                                    TextScore.setText("Score: " + Score);
                                                    TextCorrectAnswer.setText("Correct Answers: " + CorrectAnswer + "/" + numberOfAnswer);

                                                    backMenu.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent intent = new Intent(activity_score.this, MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                }
                                            });
                                }
                            }
                        });
            }
            else {
                Button backMenu = findViewById(R.id.restBtn);
                backMenu.setEnabled(true);

                TextScore.setText("Score: " + Score);
                TextCorrectAnswer.setText("Correct Answers: " + CorrectAnswer + "/" + numberOfAnswer);

                backMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity_score.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}