package com.mjc.cryptochat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity {
    public static final String EXTRA_POST_KEY = "post_key";
    private Button sendMessage;
    private RecyclerView messageList;
    private EditText inputMessage;
    private FirebaseRecyclerAdapter mAdapter;

    private String postKey;


    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        postKey = extras.getString(EXTRA_POST_KEY);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setTitle(extras.getString("saloonName"));

        sendMessage = findViewById(R.id.sendMessage);
        messageList = findViewById(R.id.messageList);
        inputMessage = findViewById(R.id.inputMessage);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("saloons").child(postKey).child("messages").setValue(inputMessage.getText());
            }
        });


        messageList.setHasFixedSize(true);
        messageList.setItemAnimator(new DefaultItemAnimator());
        messageList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        LinearLayoutManager mManager = new LinearLayoutManager(ChatActivity.this);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        messageList.setLayoutManager(mManager);



        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(Message.class, R.layout.message_tile_layout, MessageViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, final Message message, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String messagePostKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //do somethings
                    }
                });

                viewHolder.bindToPost(message);
            }
        };
        messageList.setAdapter(mAdapter);

    }
    public Query getQuery(DatabaseReference databaseRef) {
        return databaseRef.child("saloons").child(postKey).child("messages");
    }
}
