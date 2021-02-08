package com.danilove.rato;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.danilove.rato.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialButton mRegisterButton;
    private TextInputLayout mPasswordInput;
    private TextInputLayout mEmailInput;
    private TextInputLayout mNameInput;
    private String mNameValue;
    private String mEmailValue;
    private String mPasswordValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setup();
    }

    private void setup() {

        findViews();

        setupOnClickListenerElements();
    }

    private void setupOnClickListenerElements() {

        mRegisterButton.setOnClickListener(this);
    }

    private void findViews() {

        mRegisterButton = findViewById(R.id.registerButton);
        mPasswordInput = findViewById(R.id.passwordInput);
        mEmailInput = findViewById(R.id.emailInput);
        mNameInput = findViewById(R.id.nameInput);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.registerButton:
                signUp();
                break;
        }
    }

    private void signUp() {

        if (isInvalidForm()) {
            return;
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(mEmailValue, mPasswordValue)
                .addOnSuccessListener(this, authResult -> {

                    UserProfileChangeRequest profileUpdateRequest = new UserProfileChangeRequest.Builder().setDisplayName(mNameValue).build();
                    FirebaseUser firebaseUser = authResult.getUser();
                    firebaseUser.updateProfile(profileUpdateRequest)
                            .addOnSuccessListener(this, aVoid -> {

                                FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(firebaseUser.getUid())
                                        .set(new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail()))
                                        .addOnSuccessListener(this, aVoid1 -> {
                                            Intent intent = new Intent(this, MainActivity.class);
                                            intent.putExtra("uid", firebaseUser.getUid());
                                            startActivity(intent);
                                            finish();
                                        });
                            })
                            .addOnFailureListener(this, e -> {

                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(this, e -> {

                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isInvalidForm() {

        mNameValue = mNameInput.getEditText().getText().toString();
        mEmailValue = mEmailInput.getEditText().getText().toString();
        mPasswordValue = mPasswordInput.getEditText().getText().toString();

        if (TextUtils.isEmpty(mNameValue)) {

            mNameInput.setError("campo obrigatório");
            return true;
        } else {
            mNameInput.setError(null);
        }

        if (TextUtils.isEmpty(mEmailValue) || !Patterns.EMAIL_ADDRESS.matcher(mEmailValue).matches()) {

            mEmailInput.setError("email inválido");
            return true;
        } else {
            mEmailInput.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordValue) || mPasswordValue.length() < 6) {

            mPasswordInput.setError("senha inválida");
            return true;
        } else {
            mPasswordInput.setError(null);
        }

        return false;
    }


}