package com.mjc.cryptochat;

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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends BaseActivity {
    private static final String TAG = "ChatActivity";
    public static final String EXTRA_POST_KEY = "post_key";
    private Button sendMessage;
    private RecyclerView messageList;
    private EditText inputMessage;
    private FirebaseRecyclerAdapter mAdapter;

    private String postKey;

    private DatabaseReference mDatabase;

    private  Saloon saloon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        super.redirectToLogin = true; // On spécifie qu'il faut être connecté pour accéder ici

        Bundle extras = getIntent().getExtras();
        postKey = extras.getString(EXTRA_POST_KEY);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setTitle(extras.getString("saloonName"));

        sendMessage = findViewById(R.id.sendMessage);
        messageList = findViewById(R.id.messageList);
        inputMessage = findViewById(R.id.inputMessage);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("saloons/"+postKey);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                saloon = dataSnapshot.getValue(Saloon.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeNewMessage(inputMessage.getText().toString());
            }
        });


        messageList.setHasFixedSize(true);
        messageList.setItemAnimator(new DefaultItemAnimator());
        messageList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        LinearLayoutManager mManager = new LinearLayoutManager(ChatActivity.this);
        mManager.setReverseLayout(false);
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
        return databaseRef.child("saloons").child(postKey).child("messages").orderByChild("timestamp");
    }

    private void writeNewMessage(final String text) {
        final String authorUid = mAuth.getCurrentUser().getUid();
        mDatabase.child("users").child(authorUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();
                // Get user value
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    createNewMessage(authorUid, user.getName(),text);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                showSnackbar("Utilisateur inconnu, reconnectez-vous.");
            }
        });


    }
    public void createNewMessage(String userId, String userName, String text){

        mDatabase.child("saloons").child(postKey).child("msgNb").setValue(saloon.getMsgNb()+1);

        String key = mDatabase.child("saloons").child(postKey).child("messages").push().getKey();
        Message msg = new Message(userId, userName, text);

        Map<String, Object> msgValues = msg.toMap();
        msgValues.put("timestamp", ServerValue.TIMESTAMP);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/saloons/"+postKey+"/messages/" + key, msgValues);

        mDatabase.updateChildren(childUpdates);
    }
}
