package com.mjc.cryptochat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    //Variables used by the UI
    private RecyclerView mSaloonList;

    private EditText nameDialog;
    private EditText hintDialog;

    //Variables used for the database
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        super.redirectToLogin = true; // On spécifie qu'il faut être connecté pour accéder ici

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        mSaloonList = findViewById(R.id.saloonList);
        mSaloonList.setHasFixedSize(true);
        mSaloonList.setItemAnimator(new DefaultItemAnimator());
        mSaloonList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        LinearLayoutManager mManager = new LinearLayoutManager(MainActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mSaloonList.setLayoutManager(mManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAddingSaloonDialog();
            }
        });

        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Saloon, SaloonViewHolder>(Saloon.class, R.layout.saloon_tile_layout, SaloonViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final SaloonViewHolder viewHolder, final Saloon saloon, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Log.e(TAG, postKey);
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        intent.putExtra(ChatActivity.EXTRA_POST_KEY, postKey);
                        intent.putExtra("saloonName",saloon.getName());
                        startActivity(intent);
                        Log.e(TAG, postKey);
                    }
                });

                viewHolder.bindToPost(saloon);
            }
        };
        mSaloonList.setAdapter(mAdapter);
    }

    public Query getQuery(DatabaseReference databaseRef) {
        return databaseRef.child("saloons").orderByChild("msgNb");
    }

    public void displayAddingSaloonDialog() {
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.add_saloon);
        dialog.setTitle("Add a saloon");

        nameDialog = dialog.findViewById(R.id.saloonName);
        hintDialog = dialog.findViewById(R.id.saloonHint);

        dialog.findViewById(R.id.addSaloon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
                dialog.dismiss();
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

    public void validate() {
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

        if (!cancel) writeNewSaloon(nameView, hintView);
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
                    createNewSaloon(name, hint, authorUid, user.getName());
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
