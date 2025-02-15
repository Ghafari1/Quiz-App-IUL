package com.example.quizapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class activity_register extends AppCompatActivity {

    LinearLayout loading;
    Button regBtn;
    EditText emailAddress, password, username;
    TextView signInRef;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        FirebaseFirestore database = FirebaseFirestore.getInstance();
        Map<String, Object> document= new HashMap<String, Object>();


        loading = findViewById(R.id.signIn_loading);
        username = findViewById(R.id.username);
        emailAddress = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        regBtn = findViewById(R.id.registerBtn);
        signInRef = findViewById(R.id.ref_signIn);

        signInRef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity_register.this, activity_login.class));
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Define email and Password as strings
                String email = emailAddress.getText().toString();
                String Password = password.getText().toString();
                String name = username.getText().toString();

                // Check if email and password inputs are empty
                if(email.isEmpty()) {
                    emailAddress.setError("Email Address is required");
                    Toast.makeText(activity_register.this, "Please provide Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Password.isEmpty()) {
                    password.setError("Password is required");
                    Toast.makeText(activity_register.this, "Please provide Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(name.isEmpty()) {
                    username.setError("Username is required");
                    Toast.makeText(activity_register.this, "Please provide Username", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.createUserWithEmailAndPassword(email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Checking condition of registration, either successful or failed
                        if(task.isSuccessful()) {
                            loading.setVisibility(View.VISIBLE);
                            regBtn.setVisibility(View.GONE);
                            // Sending verification to the registered email
                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> verificationTask) {
                                    if(verificationTask.isSuccessful()) {

                                        document.put("username", name);
                                        document.put("score", 0);
                                        document.put("userID", firebaseAuth.getCurrentUser().getUid());
                                        document.put("bronze awards", 0);
                                        document.put("silver awards", 0);
                                        document.put("gold awards", 0);

                                        database.collection("users").document(firebaseAuth.getCurrentUser().getUid()).set(document)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {
                                                            Toast.makeText(activity_register.this, "A verification has been sent into your mail", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(activity_register.this, activity_login.class));
                                                        }
                                                        else {
                                                            loading.setVisibility(View.GONE);
                                                            regBtn.setVisibility(View.VISIBLE);

                                                            username.setText("");
                                                            emailAddress.setText("");
                                                            password.setText("");

                                                            loading.setVisibility(View.GONE);
                                                            regBtn.setVisibility(View.VISIBLE);
                                                            Toast.makeText(activity_register.this, "Couldn't send verification into your mail, please try again.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                    }

                                    else {
                                        username.setText("");
                                        emailAddress.setText("");
                                        password.setText("");

                                        loading.setVisibility(View.GONE);
                                        regBtn.setVisibility(View.VISIBLE);

                                        Toast.makeText(activity_register.this, "Couldn't send verification into your mail, please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        // if failed to pass registration
                        else {
                            Toast.makeText(activity_register.this, "Failed to register", Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            regBtn.setVisibility(View.VISIBLE);
                            return;
                        }
                    }
                });
            }
        });

    }
}