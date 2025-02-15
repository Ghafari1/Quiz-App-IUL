package com.example.quizapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class activity_quiz_menu extends AppCompatActivity {

    ProgressBar loading;
    LinearLayout mainLayout;
    ArrayList<String> completedQuizArray = new ArrayList<>();
    int randomSelector;
    DocumentSnapshot userIndex;
    DocumentSnapshot startIndex;
    DocumentSnapshot rArrow;
    DocumentSnapshot lArrow;
    Task<QuerySnapshot> allUsers;
    int usersSize;
    int userIncrement = 0;
    int userDecrement = 0;
    List<QuizUser> selectedQuizzes = new ArrayList<>();
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    AtomicInteger shifted_user = new AtomicInteger();
    List<Task<QuerySnapshot>> quizFetchTasks    = new ArrayList<>(); // Declare a List to store fetched quiz for that user on successful

    // This method will handle the logic to fetch data asynchronously
    private void fetchData(final int[] neededCount,DocumentSnapshot userIdx, int firstSelectedUserIndex, int lastSelectedUserIndex, final Runnable onComplete) {
        Log.d("Last Index", String.valueOf(lastSelectedUserIndex));
        Log.d("First Index", String.valueOf(firstSelectedUserIndex));
        Log.d("Needed count", String.valueOf(neededCount[0]));
        if (neededCount[0] > 0 && (firstSelectedUserIndex > 0 || lastSelectedUserIndex > 0)) {
            fetchUsersAndQuizzes(neededCount[0], firstSelectedUserIndex, lastSelectedUserIndex, userIdx,new OnDataFetchedListener() {
                @Override
                public void onDataFetched(int foundQuizzes) {
                    // Update the number of required quizzes based on the found ones
                    neededCount[0] -= foundQuizzes;
                    if(lastSelectedUserIndex >= firstSelectedUserIndex) {
                        userIncrement += neededCount[0];
                        int newLastUserIndex = usersSize - (userIncrement + randomSelector + 9);
                        Log.d("user Size", String.valueOf(usersSize));
                        Log.d("user Increment", String.valueOf(userIncrement));
                        Log.d("Last Index updated", String.valueOf(newLastUserIndex));
                        fetchData(neededCount, rArrow, userDecrement, newLastUserIndex, onComplete); // Recurse with the new count
                    } else {
                        Log.d("Fetch count:", String.valueOf(neededCount[0]));
                        //userDecrement = (randomSelector - 1);
                        userDecrement -= neededCount[0];
                        if(firstSelectedUserIndex - neededCount[0] > 0 ) {
                            lArrow = allUsers.getResult().getDocuments().get(randomSelector - neededCount[0]);
                        }
                        int newLastUserIndex = usersSize - (userIncrement + randomSelector + 9);
                        Log.d("user Size", String.valueOf(usersSize));
                        Log.d("user Decrement", String.valueOf(userDecrement));
                        Log.d("Last Index updated - 2", String.valueOf(newLastUserIndex));
                        fetchData(neededCount, lArrow, userDecrement, newLastUserIndex, onComplete); // Recurse with the new count
                    }
                }
            });
        }

        else {
            onComplete.run();
        }
    }

    private void fetchUsersAndQuizzes(int neededCount, int firstSelectedUserIndex, int lastSelectedUserIndex,DocumentSnapshot userIdx, OnDataFetchedListener listener) {

        if(lastSelectedUserIndex >= firstSelectedUserIndex) {

            database.collection("users")
                    .whereNotEqualTo("userID", firebaseAuth.getCurrentUser().getUid())
                    .startAfter(userIdx)
                    .limit(neededCount)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> selectedUsers) {
                            if (selectedUsers.isSuccessful()) {
                                AtomicInteger nbQuizFound = new AtomicInteger();
                                int sizeDocs = selectedUsers.getResult().size();
                                Log.d("Step:", "verified");
                                if(sizeDocs - 1 > 0) {
                                    rArrow = selectedUsers.getResult().getDocuments().get(sizeDocs - 1);
                                }
                                for (QueryDocumentSnapshot user : selectedUsers.getResult()) {
                                    // Get all quizzes available for this specific user
                                    Task<QuerySnapshot> quizTask = database.collection("users")
                                            .document(user.getId())
                                            .collection("quizzes")
                                            .get();

                                    // Add the task to the list
                                    quizFetchTasks.add(quizTask);

                                    // Handle the result of fetching quizzes for this user
                                    quizTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                boolean isFounded = false;
                                                String userDoc = user.getId();
                                                // Iterate through quizzes and find uncompleted ones
                                                for (DocumentSnapshot quiz : task.getResult()) {
                                                    Log.d("User Searched:", userDoc);
                                                    if (!completedQuizArray.contains(quiz.getId())) {
                                                        Log.d("Founded:", "True");
                                                        nbQuizFound.incrementAndGet();
                                                        selectedQuizzes.add(new QuizUser(userDoc, quiz.getId(), quiz.get("title").toString()));
                                                        isFounded = true;
                                                        break;
                                                    }
                                                }
                                                if (!isFounded) {
                                                    shifted_user.incrementAndGet();
                                                }
                                            }
                                        }
                                    });
                                }
                                Tasks.whenAllSuccess(quizFetchTasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                                    @Override
                                    public void onSuccess(List<Object> objects) {
                                        listener.onDataFetched(nbQuizFound.get());
                                    }
                                });
                                // Pass back the number of found quizzes to the listener
                            } else {
                                // Handle failure
                            }
                        }
                    });
        }   else {

            database.collection("users")
                    .whereNotEqualTo("userID", firebaseAuth.getCurrentUser().getUid())
                    .startAt(userIdx)
                    .limit(neededCount)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> selectedUsers) {
                            Log.d("Started User:", userIdx.getId());
                            if (selectedUsers.isSuccessful()) {
                                AtomicInteger nbQuizFound = new AtomicInteger();

                                Log.d("Step-2:", "verified");
                                for (QueryDocumentSnapshot user : selectedUsers.getResult()) {
                                    // Get all quizzes available for this specific user
                                    Task<QuerySnapshot> quizTask = database.collection("users")
                                            .document(user.getId())
                                            .collection("quizzes")
                                            .get();

                                    // Add the task to the list
                                    quizFetchTasks.add(quizTask);

                                    // Handle the result of fetching quizzes for this user
                                    quizTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                boolean isFounded = false;
                                                String userDoc = user.getId();
                                                // Iterate through quizzes and find uncompleted ones
                                                for (DocumentSnapshot quiz : task.getResult()) {
                                                    Log.d("User Searched:", userDoc);
                                                    if (!completedQuizArray.contains(quiz.getId())) {
                                                        nbQuizFound.incrementAndGet();
                                                        selectedQuizzes.add(new QuizUser(userDoc, quiz.getId(), quiz.get("title").toString()));
                                                        isFounded = true;
                                                        break;
                                                    }
                                                }
                                                if (!isFounded) {
                                                    shifted_user.incrementAndGet();
                                                }
                                            }
                                        }
                                    });
                                }
                                Tasks.whenAllSuccess(quizFetchTasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                                    @Override
                                    public void onSuccess(List<Object> objects) {
                                        Log.d("Step-2:", "Complete");
                                        Log.d("needed Count:", String.valueOf(neededCount));
                                        Log.d("count:", String.valueOf(nbQuizFound.get()));
                                        listener.onDataFetched(nbQuizFound.get());
                                    }
                                });
                            } else {
                                // Handle failure
                            }
                        }
                    });
        }

    }

    // Define an interface for handling data fetching completion
    interface OnDataFetchedListener {
        void onDataFetched(int foundQuizzes);
    }



    @SuppressLint("MissingInflatedId")
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


        mainLayout = findViewById(R.id.main);
        loading = findViewById(R.id.quiz_menu_loading);

         database.collection("users")
                .document(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                .collection("completedQuiz")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         if(task.isSuccessful()) {
                             for(DocumentSnapshot completedQuiz : task.getResult()) {
                                 completedQuizArray.add(completedQuiz.getId());
                             }

                             database.collection("users")
                                     .whereNotEqualTo("userID", firebaseAuth.getCurrentUser().getUid())
                                     .get()
                                     .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                         @Override
                                         public void onComplete(@NonNull Task<QuerySnapshot> users_all) {
                                             if(users_all.isSuccessful()) {
                                                 usersSize = users_all.getResult().size();                               // Size of users collection
                                                 allUsers = users_all;
                                                 if(usersSize >= 10) {
                                                     randomSelector  = new Random().nextInt(usersSize - 10) + 1;        // Generate a random index number between [1 - size of collection]
                                                     userIndex = users_all.getResult().getDocuments().get(randomSelector);  //  Get that user at the generated index to start getting other users
                                                 } else {
                                                     randomSelector  = 0;        // Generate a random index number between [1 - size of collection]
                                                     userIndex = users_all.getResult().getDocuments().get(0);  //  Get that user at the generated index to start getting other users
                                                 }



                                                 database.collection("users")
                                                         .whereNotEqualTo("userID", firebaseAuth.getCurrentUser().getUid())
                                                         .startAt(userIndex)
                                                         .limit(10)
                                                         .get()
                                                         .continueWithTask(new Continuation<QuerySnapshot, Task<QuerySnapshot>>() {
                                                             @Override
                                                             public Task<QuerySnapshot> then(@NonNull Task<QuerySnapshot> users) throws Exception {
                                                                 if(users.isSuccessful()) {

                                                                     rArrow = users.getResult().getDocuments().get(users.getResult().size() - 1);
                                                                     for (QueryDocumentSnapshot user : users.getResult()) {
                                                                         // Get all quizzes available for this specific user
                                                                         Task<QuerySnapshot> quizTask = database.collection("users")
                                                                                 .document(user.getId())
                                                                                 .collection("quizzes")
                                                                                 .get();

                                                                         // Add the task to the list
                                                                         quizFetchTasks.add(quizTask);

                                                                         // Handle the result of fetching quizzes for this user
                                                                         quizTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                             @Override
                                                                             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                 if (task.isSuccessful()) {
                                                                                     boolean isFounded = false;
                                                                                     String userDoc = user.getId();
                                                                                     // Iterate through quizzes and find uncompleted ones
                                                                                     for (DocumentSnapshot quiz : task.getResult()) {
                                                                                         if (!completedQuizArray.contains(quiz.getId())) {
                                                                                             selectedQuizzes.add(new QuizUser(userDoc, quiz.getId(), quiz.get("title").toString()));
                                                                                             isFounded = true;
                                                                                             break;
                                                                                         }
                                                                                     }
                                                                                     if (!isFounded) {
                                                                                         shifted_user.incrementAndGet();
                                                                                     }
                                                                                 }
                                                                             }
                                                                         });
                                                                     }
                                                                     Tasks.whenAllSuccess(quizFetchTasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                                                                         @Override
                                                                         public void onSuccess(List<Object> objects) {

                                                                             // Handle Exception case where we didn't find a single available quiz for selected user(s)
                                                                             final int[] neededUser = {shifted_user.get()};                  // Define number of shifted users
                                                                             int lastSelectedUserIndex = (randomSelector + 9); // Define how close last index of selected users from end of collection
                                                                             int firstSelectedUserIndex = randomSelector - 1;               // Define how close one-before first selected users index from start of collection
                                                                             userDecrement = randomSelector - 1;
                                                                             if(randomSelector - neededUser[0] > 0) {
                                                                                 lArrow = allUsers.getResult().getDocuments().get(randomSelector - neededUser[0]);
                                                                             }


                                                                             if(usersSize - lastSelectedUserIndex >= firstSelectedUserIndex) {
                                                                                 startIndex = rArrow;
                                                                                 Log.d("SELECTED-right:", startIndex.getId());
                                                                             } else {
                                                                                 startIndex = lArrow;
                                                                                 Log.d("SELECTED-left:", startIndex.getId());
                                                                             }

                                                                             fetchData(neededUser, startIndex, firstSelectedUserIndex, (usersSize - lastSelectedUserIndex), new Runnable() {
                                                                                 @Override
                                                                                 public void run() {

                                                                                     for (int i = 0; i < selectedQuizzes.size(); i++) {
                                                                                         loading.setVisibility(View.GONE);
                                                                                         QuizUser quizUser = selectedQuizzes.get(i);
                                                                                         String userID = quizUser.userID;
                                                                                         String quizID = quizUser.quizID;
                                                                                         String quizTitle = quizUser.quizTitle;

                                                                                         LayoutInflater inflater = LayoutInflater.from(activity_quiz_menu.this);
                                                                                         Button SelectBtn = (Button) inflater.inflate(R.layout.select_button, mainLayout, false);
                                                                                         SelectBtn.setText(quizTitle);

                                                                                         SelectBtn.setOnClickListener(new View.OnClickListener() {
                                                                                             @Override
                                                                                             public void onClick(View v) {
                                                                                                 Intent intent = new Intent(activity_quiz_menu.this, activity_quiz.class);
                                                                                                 intent.putExtra("isTest", false);
                                                                                                 intent.putExtra("key_quiz", quizID);
                                                                                                 intent.putExtra("key_user", userID);

                                                                                                 startActivity(intent);
                                                                                             }
                                                                                         });
                                                                                         mainLayout.addView(SelectBtn);
                                                                                     }
                                                                                 }
                                                                             });
                                                                         }
                                                                     });
                                                                 }
                                                                 return task;
                                                             }
                                                         });

                                             }
                                         }
                                     });

                         }
                     }
                 });

    }
}