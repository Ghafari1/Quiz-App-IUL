package com.example.quizapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class activity_create extends AppCompatActivity {

    int numberOfDocs = 0;
    boolean hasNumberOfDocs = false;
    boolean isFilled = true;
    String[] items = {"Small", "Medium", "Large"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    RadioGroup rg1;
    RadioGroup rg2;
    RadioGroup rg3;
    RadioGroup rg4;
    LinearLayout answerGroup1;
    LinearLayout answerGroup2;
    LinearLayout answerGroup3;
    LinearLayout answerGroup4;
    TextInputEditText  Question1, Question2, Question3, Question4, quizTitle;
    Button createBtn, TestBtn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Map<String, Object> document = new HashMap<String, Object>();

        database.collection("users")
                .document(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .collection("quizzes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            numberOfDocs    = task.getResult().size();
                            hasNumberOfDocs = true;
                            Toast.makeText(activity_create.this, String.valueOf(numberOfDocs), Toast.LENGTH_SHORT).show();
                        }

                        else {
                            hasNumberOfDocs = false;
                        }
                    }
                });

        quizTitle = findViewById(R.id.quizTitle);

        rg1 = findViewById(R.id.rg1);
        rg2 = findViewById(R.id.rg2);
        rg3 = findViewById(R.id.rg3);
        rg4 = findViewById(R.id.rg4);

        answerGroup1 = findViewById(R.id.answerGroup1);
        answerGroup2 = findViewById(R.id.answerGroup2);
        answerGroup3 = findViewById(R.id.answerGroup3);
        answerGroup4 = findViewById(R.id.answerGroup4);

        Question1 = findViewById(R.id.Question1);
        Question2 = findViewById(R.id.Question2);
        Question3 = findViewById(R.id.Question3);
        Question4 = findViewById(R.id.Question4);

        createBtn = findViewById(R.id.createBtn);


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rgSelected1 = rg1.getCheckedRadioButtonId();
                int rgSelected2 = rg2.getCheckedRadioButtonId();
                int rgSelected3 = rg3.getCheckedRadioButtonId();
                int rgSelected4 = rg4.getCheckedRadioButtonId();

                String Title = quizTitle.getText().toString();
                String UID = UUID.randomUUID().toString();
                String QuizID = numberOfDocs + "-" + UID;

                String question1 = Question1.getText().toString();
                String question2 = Question2.getText().toString();
                String question3 = Question3.getText().toString();
                String question4 = Question4.getText().toString();

                if(
                        question1.trim().isEmpty()
                        || question2.trim().isEmpty()
                        || question3.trim().isEmpty()
                        || question4.trim().isEmpty()
                ) {
                    Toast.makeText(activity_create.this, "Please Fill All Questions and Answers", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] questions = {question1, question2, question3, question4};
                List<String> questionsList = Arrays.asList(questions);

                String correctAnswer1 = ( (TextInputLayout)answerGroup1.getChildAt(rgSelected1 - 1) ).getEditText().getText().toString();
                String correctAnswer2 = ( (TextInputLayout)answerGroup2.getChildAt(rgSelected2 - 5) ).getEditText().getText().toString();
                String correctAnswer3 = ( (TextInputLayout)answerGroup3.getChildAt(rgSelected3 - 9) ).getEditText().getText().toString();
                String correctAnswer4 = ( (TextInputLayout)answerGroup4.getChildAt(rgSelected4 - 13) ).getEditText().getText().toString();

                String[] correctAnswers = {correctAnswer1, correctAnswer2, correctAnswer3, correctAnswer4};
                List<String> correctAnswersList = Arrays.asList(correctAnswers);

                ArrayList<String> answerAll1 = new ArrayList<String>();
                ArrayList<String> answerAll2 = new ArrayList<>();
                ArrayList<String> answerAll3 = new ArrayList<>();
                ArrayList<String> answerAll4 = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    String answer = ( (TextInputLayout)answerGroup1.getChildAt(i) ).getEditText().getText().toString();
                    if(answer.trim().isEmpty()) {
                        isFilled = false;
                    }
                    answerAll1.add(answer);
                }

                for (int i = 0; i < 4; i++) {
                    String answer = ( (TextInputLayout)answerGroup2.getChildAt(i) ).getEditText().getText().toString();
                    if(answer.trim().isEmpty()) {
                        isFilled = false;
                    }
                    answerAll2.add(answer);
                }

                for (int i = 0; i < 4; i++) {
                    String answer = ( (TextInputLayout)answerGroup3.getChildAt(i) ).getEditText().getText().toString();
                    if(answer.trim().isEmpty()) {
                        isFilled = false;
                    }
                    answerAll3.add(answer);
                }

                for (int i = 0; i < 4; i++) {
                    String answer = ( (TextInputLayout)answerGroup4.getChildAt(i) ).getEditText().getText().toString();
                    if(answer.trim().isEmpty()) {
                        isFilled = false;
                    }
                    answerAll4.add(answer);
                }

                document.put("title", Title);
                document.put("questions", questionsList);
                document.put("q1_answers", answerAll1);
                document.put("q2_answers", answerAll2);
                document.put("q3_answers", answerAll3);
                document.put("q4_answers", answerAll4);
                document.put("correct_answers", correctAnswersList);

                if(hasNumberOfDocs && isFilled) {

                    database.collection("users")
                            .document(firebaseAuth.getCurrentUser().getUid())
                            .collection("quizzes")
                            .document(QuizID)
                            .set(document).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(activity_create.this, "Quiz Created!", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(activity_create.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                } else {
                    Toast.makeText(activity_create.this, "Please Fill All Questions and Answers", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}