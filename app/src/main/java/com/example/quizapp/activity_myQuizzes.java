package com.example.quizapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class activity_myQuizzes extends AppCompatActivity {

    List<QuizUser> selectedQuizzes = new ArrayList<>();
    ProgressBar loading;
    LinearLayout mainLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_quizzes);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loading = findViewById(R.id.myQuiz_menu_loading);
        mainLayout = findViewById(R.id.main);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        database.collection("users")
                .document(auth.getUid())
                .collection("quizzes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> quizzes) {
                        if(quizzes.isSuccessful()) {
                            for(DocumentSnapshot quiz : quizzes.getResult()) {
                                selectedQuizzes.add(new QuizUser(auth.getUid(), quiz.getId(), quiz.get("title").toString()));
                            }
                        }
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (int i = 0; i < selectedQuizzes.size(); i++) {
                                loading.setVisibility(View.GONE);
                                QuizUser quizUser = selectedQuizzes.get(i);
                                String userID = quizUser.userID;
                                String quizID = quizUser.quizID;
                                String quizTitle = quizUser.quizTitle;

                                LayoutInflater inflater = LayoutInflater.from(activity_myQuizzes.this);
                                Button SelectBtn = (Button) inflater.inflate(R.layout.select_button, mainLayout, false);
                                SelectBtn.setText(quizTitle);

                                SelectBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(activity_myQuizzes.this, activity_quiz.class);
                                        intent.putExtra("isTest", true);
                                        intent.putExtra("key_quiz", quizID);
                                        intent.putExtra("key_user", userID);

                                        startActivity(intent);
                                    }
                                });
                                mainLayout.addView(SelectBtn);
                            }
                        }
                    }
                });

    }
}