package com.example.quizapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class activity_login extends AppCompatActivity {

    LinearLayout loading;
    Button logBtn;
    EditText emailAddress, password;
    TextView signUpRef;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loading = findViewById(R.id.signIn_loading);
        logBtn = findViewById(R.id.loginBtn);
        emailAddress = findViewById(R.id.login_emailAddress);
        password = findViewById(R.id.login_password);
        signUpRef = findViewById(R.id.ref_signUp);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        signUpRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_login.this, activity_register.class));
            }
        });

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logBtn.setEnabled(false);
                String email = emailAddress.getText().toString();
                String Password = password.getText().toString();

                // Check if email and password inputs are empty
                if(email.isEmpty()) {
                    logBtn.setEnabled(true);
                    emailAddress.setError("Email Address is required");
                    Toast.makeText(activity_login.this, "Please provide Email and Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Password.isEmpty()) {
                    logBtn.setEnabled(true);
                    password.setError("Password is required");
                    Toast.makeText(activity_login.this, "Please provide Email and Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // if not, then check if the login process is completed
                else {
                    firebaseAuth.signInWithEmailAndPassword(email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // if it's successful and their an actual account with that email and password, check if account verified
                            if(task.isSuccessful()) {
                                if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                                    // Do nothing, just continue...
                                }

                                // if not verified, send a message to tell him to verify
                                else {
                                    logBtn.setEnabled(true);
                                    Toast.makeText(activity_login.this, "Please Verify Your Email!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            // if login process didn't find account or something went wrong
                            else {
                                logBtn.setEnabled(true);
                                Toast.makeText(activity_login.this, "Couldn't find account with email and password", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            database.collection("users")
                                    .document(Objects.requireNonNull(task.getResult().getUser()).getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if(task.isSuccessful()) {

                                                String username     =  task.getResult().get("username").toString();
                                                int score           =  Integer.parseInt(task.getResult().get("score").toString());
                                                int gold_awards     = Integer.parseInt(task.getResult().get("gold awards").toString());
                                                int silver_awards   = Integer.parseInt(task.getResult().get("silver awards").toString());
                                                int bronze_awards   = Integer.parseInt(task.getResult().get("bronze awards").toString());

                                                SharedPreferences sharedPref = getSharedPreferences("user_info", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putString("username", username);
                                                editor.putInt("score", score);
                                                editor.putInt("gold awards", gold_awards);
                                                editor.putInt("silver awards", silver_awards);
                                                editor.putInt("bronze awards", bronze_awards);
                                                editor.apply();  // or editor.commit(); for synchronous


                                                Intent intent = new Intent(activity_login.this, MainActivity.class);

                                                startActivity(intent);
                                            }

                                            else {
                                                logBtn.setEnabled(true);
                                                Toast.makeText(activity_login.this, "Couldn't find account with email and password", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                }
            }
        });

    }
}