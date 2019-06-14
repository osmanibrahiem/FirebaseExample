package com.osman.firebaseexample;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText username;
    private EditText email;
    private EditText password;
    private Button register;
    private ProgressBar loading;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initActions();
    }

    private void initViews() {
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        loading = findViewById(R.id.loading);

        this.mAuth = FirebaseAuth.getInstance();
        this.mReference = FirebaseDatabase.getInstance().getReference();
    }

    private void initActions() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeypad();
                username.setError(null);
                email.setError(null);
                password.setError(null);

                String inputName = username.getText().toString().trim();
                String inputEmail = email.getText().toString().trim();
                String inputPassword = password.getText().toString().trim();

                if (isNetworkAvailable() &&
                        isValidName(inputName) &&
                        isValidEmail(inputEmail) &&
                        isValidPassword(inputPassword)) {
                    signUpUser(inputName, inputEmail, inputPassword);
                }
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

    private boolean isValidName(String name) {
        if (TextUtils.isEmpty(name)) {
            showNameError(R.string.empty_name);
            return false;
        }
        return true;
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

    private void showNameError(int message) {
        username.setError(getText(message));
    }

    private void showEmailError(int message) {
        email.setError(getText(message));
    }

    private void showPasswordError(int message) {
        password.setError(getText(message));
    }

    private void signUpUser(final String inputName, final String inputEmail, final String inputPassword) {
        showLoadingAndDisableComponents();
        mAuth.createUserWithEmailAndPassword(inputEmail, inputPassword).addOnFailureListener(failureListener)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        loginUser(inputName, inputEmail, inputPassword);
                    }
                });

    }

    private void loginUser(final String inputName, final String inputEmail, String inputPassword) {
        mAuth.signInWithEmailAndPassword(inputEmail, inputPassword).addOnFailureListener(failureListener)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String inputUid = authResult.getUser().getUid();
                        saveUser(inputUid, inputName, inputEmail);
                    }
                });
    }

    private void saveUser(String inputUid, String inputName, String inputEmail) {
        User user = new User();
        user.setDisplayName(inputName);
        user.setEmail(inputEmail);
        mReference.child("Users").child(inputUid).setValue(user).addOnFailureListener(failureListener)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideLoadingAndEnableComponents();
                        startMainActivity();
                    }
                });
    }

    private OnFailureListener failureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            hideLoadingAndEnableComponents();
            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void showLoadingAndDisableComponents() {
        loading.setVisibility(View.VISIBLE);
        register.setEnabled(false);
        username.setEnabled(false);
        email.setEnabled(false);
        password.setEnabled(false);
    }

    private void hideLoadingAndEnableComponents() {
        loading.setVisibility(View.GONE);
        register.setEnabled(true);
        username.setEnabled(true);
        email.setEnabled(true);
        password.setEnabled(true);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
