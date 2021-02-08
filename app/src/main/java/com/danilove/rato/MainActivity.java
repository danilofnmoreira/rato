package com.danilove.rato;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.danilove.rato.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private TextView mUserToString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser == null) {
            return;
        }

        setup();
    }

    private void setup() {

        findViews();
        showCurrentUser();
    }

    private void findViews() {

        mUserToString = findViewById(R.id.userToString);
    }

    private void showCurrentUser() {

        String uid = getIntent().getStringExtra("uid");

        if (uid == null) {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            uid = firebaseUser.getUid();
        }

        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(this, documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if(user != null) {
                        mUserToString.setText(user.toString());
                    }
                });

    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {

            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Task<GetTokenResult> tokenResultTask = currentUser.getIdToken(true);

        if (tokenResultTask == null) {

            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        tokenResultTask.addOnFailureListener(this, e -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseAuth.getInstance().removeAuthStateListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseAuth.getInstance().addAuthStateListener(this);
    }
}