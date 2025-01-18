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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;

import java.util.ArrayList;

public class activity_create extends AppCompatActivity {

    SQLiteDatabase QuizDB;
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

//        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
//
//        autoCompleteTextView.setText("Small");
//
//        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, items);
//        autoCompleteTextView.setAdapter(adapterItems);

        QuizDB = openOrCreateDatabase("QuizDB", MODE_PRIVATE, null);

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

//        TestBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ArrayList<String> answerAll1 = new ArrayList<String>();
//
//                for (int i = 0; i < 4; i++) {
//                    String answer = ( (TextInputLayout)answerGroup1.getChildAt(i) ).getEditText().getText().toString();
//                    answerAll1.add(answer);
//                }
//
//                System.out.println(answerAll1);
//            }
//        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rgSelected1 = rg1.getCheckedRadioButtonId();
                int rgSelected2 = rg2.getCheckedRadioButtonId();
                int rgSelected3 = rg3.getCheckedRadioButtonId();
                int rgSelected4 = rg4.getCheckedRadioButtonId();

                String Title = quizTitle.getText().toString();

                String question1 = Question1.getText().toString();
                String question2 = Question2.getText().toString();
                String question3 = Question3.getText().toString();
                String question4 = Question4.getText().toString();

//                String[] questions = {question1, question2, question3, question4};

                String correctAnswer1 = ( (TextInputLayout)answerGroup1.getChildAt(rgSelected1 - 1) ).getEditText().getText().toString();
                String correctAnswer2 = ( (TextInputLayout)answerGroup2.getChildAt(rgSelected2 - 5) ).getEditText().getText().toString();
                String correctAnswer3 = ( (TextInputLayout)answerGroup3.getChildAt(rgSelected3 - 9) ).getEditText().getText().toString();
                String correctAnswer4 = ( (TextInputLayout)answerGroup4.getChildAt(rgSelected4 - 13) ).getEditText().getText().toString();

//                String[] correctAnswers = {correctAnswer1, correctAnswer2, correctAnswer3, correctAnswer4};

                ArrayList<String> answerAll1 = new ArrayList<String>();
                ArrayList<String> answerAll2 = new ArrayList<>();
                ArrayList<String> answerAll3 = new ArrayList<>();
                ArrayList<String> answerAll4 = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    String answer = ( (TextInputLayout)answerGroup1.getChildAt(i) ).getEditText().getText().toString();
                    answerAll1.add(answer);
                }

                for (int i = 0; i < 4; i++) {
                    String answer = ( (TextInputLayout)answerGroup2.getChildAt(i) ).getEditText().getText().toString();
                    answerAll2.add(answer);
                }

                for (int i = 0; i < 4; i++) {
                    String answer = ( (TextInputLayout)answerGroup3.getChildAt(i) ).getEditText().getText().toString();
                    answerAll3.add(answer);
                }

                for (int i = 0; i < 4; i++) {
                    String answer = ( (TextInputLayout)answerGroup4.getChildAt(i) ).getEditText().getText().toString();
                    answerAll4.add(answer);
                }

                QuizDB.execSQL("INSERT INTO quiz(QuizTitle, QuizQuestion, QuizAnswers, QuizCorrectAnswer) VALUES('"+ Title +"','"+ question1 +"','"+ answerAll1 +"', '"+ correctAnswer1 +"')");
                QuizDB.execSQL("INSERT INTO quiz(QuizTitle, QuizQuestion, QuizAnswers, QuizCorrectAnswer) VALUES('"+ Title +"','"+ question2 +"','"+ answerAll2 +"', '"+ correctAnswer2 +"')");
                QuizDB.execSQL("INSERT INTO quiz(QuizTitle, QuizQuestion, QuizAnswers, QuizCorrectAnswer) VALUES('"+ Title +"','"+ question3 +"','"+ answerAll3 +"', '"+ correctAnswer3 +"')");
                QuizDB.execSQL("INSERT INTO quiz(QuizTitle, QuizQuestion, QuizAnswers, QuizCorrectAnswer) VALUES('"+ Title +"','"+ question4 +"','"+ answerAll4 +"', '"+ correctAnswer4 +"')");

                Toast.makeText(activity_create.this, "Quiz Created!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(activity_create.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}