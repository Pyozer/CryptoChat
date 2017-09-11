package com.mjc.cryptochat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

    private TextView nameDialog;
    private TextView hintDialog;

    //Variables used for the database
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Saloon, MainActivity.SaloonViewHolder> mAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        super.redirectToLogin = true; // On spécifie qu'il faut être connecté pour accéder ici

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSaloonList = findViewById(R.id.saloonList);
        mSaloonList.setHasFixedSize(true);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAddingSaloonDialog();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mSaloonList.setLayoutManager(new LinearLayoutManager(this));


        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Saloon, SaloonViewHolder>(Saloon.class, R.layout.saloon_tile_layout,
                SaloonViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final SaloonViewHolder viewHolder, final Saloon saloon, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        intent.putExtra(ChatActivity.EXTRA_POST_KEY, postKey);
                        startActivity(intent);
                    }
                });
            }
        };
        mSaloonList.setAdapter(mAdapter);
    }
    public Query getQuery(DatabaseReference databaseRef){
        Query salonQuery = databaseRef.child("saloons").orderByChild("nbMsg");
        return salonQuery;
    }

    public void displayAddingSaloonDialog(){
        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.add_saloon);
        dialog.setTitle("Add a saloon");

        nameDialog = dialog.findViewById(R.id.saloonName);
        hintDialog = dialog.findViewById(R.id.saloonHint);

        dialog.findViewById(R.id.addSaloon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(0,nameDialog.getText().toString(),mAuth.getCurrentUser().getUid(),hintDialog.getText().toString());
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
    public void validate(int msgNb, String name, String authorId, String hint){
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

        if (!cancel) writeNewSaloon(msgNb, name,authorId, hint);
    }
    private void writeNewSaloon(final int msgNb, final String name, final String authorId, final String hint) {
        mDatabase.child("users").child(authorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();
                // Get user value
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    createNewSaloon(msgNb, name,authorId,user.toString(), hint);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }
    public void createNewSaloon(int msgNb, String name, String authorId,String authorName, String hint){
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("saloon").push().getKey();
        Saloon post = new Saloon(msgNb, name, authorId, authorName, hint);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/saloons/" + key, postValues);
        //childUpdates.put("/user-posts/" + id + "/" + key, postValues);

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

    class SaloonViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView;
        public TextView authorView;

        public SaloonViewHolder(View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.saloonTileName);
            authorView = (TextView) itemView.findViewById(R.id.saloonTileAuthor);

        }

        public void bindToPost(Saloon saloon, View.OnClickListener starClickListener) {
            titleView.setText(saloon.getName());
            authorView.setText(saloon.getHint());
        }
    }
}
