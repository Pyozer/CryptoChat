package com.mjc.cryptochat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_POST_KEY = "post_key";
    private Button sendMessage;
    private RecyclerView messagesList;
    private EditText inputMessage;
    private FirebaseRecyclerAdapter mAdapter;


    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();

        setTitle(extras.getString("saloonName"));

        sendMessage = findViewById(R.id.sendMessage);
        messagesList = findViewById(R.id.messageList);
        inputMessage = findViewById(R.id.inputMessage);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add message to firebase
            }
        });


        messagesList = findViewById(R.id.saloonList);
        messagesList.setHasFixedSize(true);
        messagesList.setItemAnimator(new DefaultItemAnimator());
        messagesList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        LinearLayoutManager mManager = new LinearLayoutManager(ChatActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        messagesList.setLayoutManager(mManager);



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
                        //do somethings
                    }
                });

                viewHolder.bindToPost(saloon);
            }
        };
        messagesList.setAdapter(mAdapter);

    }
    public Query getQuery(DatabaseReference databaseRef) {
        return databaseRef.child("saloons").orderByChild("msgNb");
    }
}
