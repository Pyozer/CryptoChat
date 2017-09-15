package com.mjc.cryptochat.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mjc.cryptochat.Fragment.MainSaloonFragment;
import com.mjc.cryptochat.Model.Saloon;
import com.mjc.cryptochat.Model.User;
import com.mjc.cryptochat.R;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    //Variables used for the UI
    private EditText nameDialog;
    private EditText hintDialog;

    //Variables used for load the fragment
    private FragmentManager mFragmentManager;

    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        super.redirectToLogin = true; // On spécifie qu'il faut être connecté pour accéder ici

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentManager = getSupportFragmentManager();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAddingSaloonDialog();
            }
        });

        loadFragment(new MainSaloonFragment());
    }

    private void loadFragment(Fragment fragment) {
        final FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.main_content, fragment).commit();
    }

    public void displayAddingSaloonDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_saloon);

        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        nameDialog = dialog.findViewById(R.id.saloonName);
        hintDialog = dialog.findViewById(R.id.saloonHint);

        dialog.findViewById(R.id.addSaloon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.findViewById(R.id.cancelSaloon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public boolean validate() {
        nameDialog.setError(null);
        hintDialog.setError(null);

        String nameView = nameDialog.getText().toString().trim();
        String hintView = hintDialog.getText().toString().trim();

        boolean cancel = false;

        if (TextUtils.isEmpty(nameView)) {
            nameDialog.setError(getString(R.string.error_field_required));
            cancel = true;
        }
        if (TextUtils.isEmpty(hintView)) {
            hintDialog.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (!cancel) {
            writeNewSaloon(nameView, hintView);
            return true;
        }
        return false;
    }

    private void writeNewSaloon(final String name, final String hint) {
        final String authorUid = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(authorUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();
                // Get user value
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    createNewSaloon(name, hint, authorUid, user.getUsername());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                showSnackbar("Utilisateur inconnu, reconnectez-vous.");
            }
        });
    }

    public void createNewSaloon(String name, String hint, String authorId, String authorName) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("saloons").push().getKey();
        Saloon post = new Saloon(0, name, authorId, authorName, hint);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/saloons/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    public static Context getmContext(){
        return mContext;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        }
                    });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
