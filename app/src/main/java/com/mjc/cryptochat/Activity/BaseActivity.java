package com.mjc.cryptochat.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mjc.cryptochat.Model.User;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    public String getUid() {
        return mAuth.getCurrentUser().getUid();
    }

    public void checkIfUsernameExist() {
        mDatabase.child("users").child(getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get user value
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    showDialogToSetUsername();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void showDialogToSetUsername() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.setup_username);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        final EditText username = dialog.findViewById(R.id.username);

        dialog.findViewById(R.id.button_username_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username.setError(null);
                String usernameText = username.getText().toString();
                if (!TextUtils.isEmpty(usernameText)) {
                    mDatabase.child("users").child(getUid()).setValue(new User(usernameText));
                    dialog.dismiss();
                } else {
                    username.setError(getString(R.string.error_field_required));
                }
            }
        });

        dialog.findViewById(R.id.cancelUsername).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}