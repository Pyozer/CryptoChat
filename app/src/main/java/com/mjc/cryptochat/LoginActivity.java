package com.mjc.cryptochat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    private EditText mInputLogin;
    private EditText mInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mInputLogin = findViewById(R.id.login_email);
        mInputPassword = findViewById(R.id.login_password);

        findViewById(R.id.login_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validForm();
            }
        });
        findViewById(R.id.login_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }
        });
    }

    private void validForm() {
        mInputLogin.setError(null);
        mInputPassword.setError(null);

        String email = mInputLogin.getText().toString().trim();
        String password = mInputLogin.getText().toString().trim();

        boolean cancel = false;

        if (TextUtils.isEmpty(email)) {
            mInputLogin.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mInputLogin.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (!cancel) connectUser(email, password);
    }

    private void connectUser(String email, String password) {
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            onAuthSuccess(user);
                        } else {
                            try {
                                throw task.getException();
                            } catch (Exception e) {
                                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                        }
                    }
                });
    }
}