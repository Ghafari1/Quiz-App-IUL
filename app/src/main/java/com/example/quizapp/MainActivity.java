package com.example.quizapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView userName;

    @SuppressLint("MissingInflatedId")
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

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        if(firebaseAuth.getCurrentUser() == null || !( firebaseAuth.getCurrentUser().isEmailVerified() )) {
            startActivity(new Intent(MainActivity.this, activity_login.class));
        }

        userName = findViewById(R.id.userName);

        SharedPreferences sharedPref = getSharedPreferences("user_info", MODE_PRIVATE);
        String username = sharedPref.getString("username", "default_username");  // "default_username" is a fallback if not found
        int score = sharedPref.getInt("score", 0);  // "0" is a fallback if not found

        Button startQuizBtn     = findViewById(R.id.StartBtn);
        Button createQuizBtn    = findViewById(R.id.CreateBtn);
        Button signOutBtn       = findViewById(R.id.signOutBtn);
        Button statisticBtn     = findViewById(R.id.statisticBtn);
        Button myQuizBtn        = findViewById(R.id.myQuizzesBtn);


        userName.setText("Welcome back, " + username);

//        else {
//            DocumentReference userData = database.collection("users").document(FirebaseAuth.getInstance().getUid());
//            userData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    DocumentSnapshot doc = task.getResult();
//                    userName.setText("Welcome back, " + doc.get("username").toString());
//                }
//            });
//        }


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

        statisticBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, activity_statistic.class);
                intent.putExtra("score", score);
                startActivity(intent);
            }
        });

        myQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, activity_myQuizzes.class);
                startActivity(intent);
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                if(firebaseAuth.getCurrentUser() == null) {
                    SharedPreferences.Editor sharedPreferences = sharedPref.edit();
                    sharedPreferences.clear();
                    sharedPreferences.commit();
                    Intent intent = new Intent(MainActivity.this, activity_login.class);
                    startActivity(intent);
                }

            }
        });
    }
}