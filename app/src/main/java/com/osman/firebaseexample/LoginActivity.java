package com.osman.firebaseexample;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private Button register;
    private ProgressBar loading;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initActions();
    }

    private void initViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        loading = findViewById(R.id.loading);

        this.mAuth = FirebaseAuth.getInstance();
    }

    private void initActions() {

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeypad();
                email.setError(null);
                password.setError(null);

                String inputEmail = email.getText().toString().trim();
                String inputPassword = password.getText().toString().trim();

                if (isNetworkAvailable() &&
                        isValidEmail(inputEmail) &&
                        isValidPassword(inputPassword)) {
                    loginUser(inputEmail, inputPassword);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void hideKeypad() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager mConnectivity =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivity.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            showNetWorkError(R.string.message_no_internet);
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            showEmailError(R.string.empty_email);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showEmailError(R.string.invalid_email);
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            showPasswordError(R.string.empty_password);
            return false;
        }
        if (password.trim().length() < 8) {
            showPasswordError(R.string.invalid_password);
            return false;
        }
        return true;
    }

    private void showNetWorkError(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showEmailError(int message) {
        email.setError(getText(message));
    }

    private void showPasswordError(int message) {
        password.setError(getText(message));
    }

    private void loginUser(String inputEmail, String inputPassword) {
        showLoadingAndDisableComponents();
        mAuth.signInWithEmailAndPassword(inputEmail, inputPassword).addOnFailureListener(failureListener)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        startMainActivity();
                    }
                });
    }

    private OnFailureListener failureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            hideLoadingAndEnableComponents();
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void showLoadingAndDisableComponents() {
        loading.setVisibility(View.VISIBLE);
        register.setEnabled(false);
        login.setEnabled(false);
        email.setEnabled(false);
        password.setEnabled(false);
    }

    private void hideLoadingAndEnableComponents() {
        loading.setVisibility(View.GONE);
        register.setEnabled(true);
        login.setEnabled(true);
        email.setEnabled(true);
        password.setEnabled(true);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
