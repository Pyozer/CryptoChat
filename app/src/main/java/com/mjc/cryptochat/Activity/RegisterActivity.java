package com.mjc.cryptochat.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.mjc.cryptochat.AppConfig;
import com.mjc.cryptochat.R;

public class RegisterActivity extends BaseActivity {

    private final static String TAG = "RegisterActivity";

    private EditText mRegisterEmail;
    private EditText mRegisterPass;
    private EditText mRegisterPassConf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegisterEmail = findViewById(R.id.register_email);
        mRegisterPass = findViewById(R.id.register_password);
        mRegisterPassConf = findViewById(R.id.register_password_conf);

        findViewById(R.id.register_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validForm();
            }
        });
        findViewById(R.id.register_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void validForm() {
        mRegisterEmail.setError(null);
        mRegisterPass.setError(null);
        mRegisterPassConf.setError(null);

        String email = mRegisterEmail.getText().toString();
        String password = mRegisterPass.getText().toString();
        String passwordConf = mRegisterPassConf.getText().toString();

        boolean cancel = false;

        if (TextUtils.isEmpty(email)) {
            mRegisterEmail.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            mRegisterPass.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(passwordConf)) {
            mRegisterPassConf.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (password.length() < AppConfig.MIN_PASSWORD_LENGTH) {
            mRegisterPassConf.setError(String.format(getString(R.string.error_short_password), AppConfig.MIN_PASSWORD_LENGTH));
            cancel = true;
        }
        if (!password.equals(passwordConf)) {
            mRegisterPassConf.setError(getString(R.string.error_password_different));
            cancel = true;
        }

        if (!cancel) {
            registerUser(email, password);
        }
    }

    private void registerUser(String email, String password) {
        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            onAuthSuccess(user);
                        } else {
                            try {
                                throw task.getException();
                            } catch(Exception e) {
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }
}