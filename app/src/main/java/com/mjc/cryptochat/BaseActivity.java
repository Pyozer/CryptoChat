package com.mjc.cryptochat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class BaseActivity extends AppCompatActivity {

    private final static String INTENT_SNACKBAR = "intent_snackbar_msg";

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public boolean redirectToLogin = false;

    static boolean isInitialized = false;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(redirectToLogin) {
            onAuthFailed(mAuth, currentUser);
        } else {
            onAuthSuccess(currentUser);
        }
    }

    public void showProgressDialog() {
        showProgressDialog(getString(R.string.loading));
    }

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void onAuthFailed(FirebaseAuth auth, FirebaseUser user) {
        if(user == null) {
            auth.signOut();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra(INTENT_SNACKBAR, getString(R.string.error_must_be_login));
            startActivity(intent);
            finish();
        }
    }

    public void onAuthSuccess(FirebaseUser user) {
        if (user != null) {
            // Go to MainActivity
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}