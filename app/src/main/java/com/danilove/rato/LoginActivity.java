package com.danilove.rato;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialButton mLoginButton;
    private MaterialTextView mRegisterLink;
    private TextInputLayout mPasswordInput;
    private TextInputLayout mEmailInput;
    private String mEmailValue;
    private String mPasswordValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setup();
    }

    private void setup() {

        findViews();

        setupOnClickListenerElements();
    }

    private void setupOnClickListenerElements() {

        mLoginButton.setOnClickListener(this);
        mRegisterLink.setOnClickListener(this);
    }

    private void findViews() {

        mLoginButton = findViewById(R.id.loginButton);
        mRegisterLink = findViewById(R.id.registerLink);
        mPasswordInput = findViewById(R.id.passwordInput);
        mEmailInput = findViewById(R.id.emailInput);
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.registerLink:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.loginButton:
                signIn();
                break;
        }
    }

    private void signIn() {

        if (isInvalidForm()) {
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmailValue, mPasswordValue)
                .addOnSuccessListener(this, authResult -> {

                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(this, e -> {

                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private boolean isInvalidForm() {

        mEmailValue = mEmailInput.getEditText().getText().toString();
        mPasswordValue = mPasswordInput.getEditText().getText().toString();

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