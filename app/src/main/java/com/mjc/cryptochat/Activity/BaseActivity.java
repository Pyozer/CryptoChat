package com.mjc.cryptochat.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mjc.cryptochat.R;

public class BaseActivity extends AppCompatActivity {

    public final static String INTENT_SNACKBAR = "intent_snackbar_msg";

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public boolean redirectToLogin = false;

    public static boolean isInitialized = false;

    public FirebaseAuth mAuth;
    public DatabaseReference mDatabase;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(!isInitialized) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            isInitialized = true;
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
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

    public void showSnackbar(int stringId) {
        showSnackbar(getString(stringId));
    }
    public void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG);
        snackbar.show();
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