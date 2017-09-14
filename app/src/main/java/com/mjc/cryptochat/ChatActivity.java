package com.mjc.cryptochat;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mjc.cryptochat.Model.Message;
import com.mjc.cryptochat.Model.Saloon;
import com.mjc.cryptochat.Model.User;

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

    private Saloon saloon;
    private static String hint = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        super.redirectToLogin = true; // On spécifie qu'il faut être connecté pour accéder ici

        Bundle extras = getIntent().getExtras();
        postKey = extras.getString(EXTRA_POST_KEY);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        SharedPreferences sharedPref = ChatActivity.this.getPreferences(Context.MODE_PRIVATE);
        hint = sharedPref.getString(postKey, "");

        setTitle(extras.getString("saloonName"));

        sendMessage = findViewById(R.id.sendMessage);
        messageList = findViewById(R.id.messageList);
        inputMessage = findViewById(R.id.inputMessage);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("saloons/" + postKey);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                saloon = dataSnapshot.getValue(Saloon.class);

                if (messageList.getScrollState() != 0) {
                    Snackbar mySnackbar = Snackbar.make(findViewById(android.R.id.content),
                            R.string.new_message, Snackbar.LENGTH_LONG);
                    mySnackbar.setAction(R.string.see_new_message, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            messageList.scrollToPosition(mAdapter.getItemCount() - 1);
                        }
                    });
                    mySnackbar.show();
                } else {
                    messageList.scrollToPosition(mAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hint.isEmpty()){
                    validForm();
                }else{
                    Toast.makeText(ChatActivity.this, R.string.need_hint, Toast.LENGTH_SHORT).show();
                }
            }
        });

        messageList.setHasFixedSize(true);
        messageList.setItemAnimator(new DefaultItemAnimator());
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

    private void validForm() {
        inputMessage.setError(null);

        String text = inputMessage.getText().toString().trim();

        text = encryptMsg(text);

        if (TextUtils.isEmpty(text)) {
            inputMessage.setError(getString(R.string.error_field_required));
        } else {
            writeNewMessage(text);
        }
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
                    createNewMessage(authorUid, user.getUsername(), text);
                } else {
                    showSnackbar(getString(R.string.unknown_account));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                showSnackbar(databaseError.toException().getMessage());
            }
        });


    }

    public void createNewMessage(String userId, String userName, String text) {

        mDatabase.child("saloons").child(postKey).child("msgNb").setValue(saloon.getMsgNb() + 1);

        String key = mDatabase.child("saloons").child(postKey).child("messages").push().getKey();
        Message msg = new Message(userId, userName, text);

        Map<String, Object> msgValues = msg.toMap();
        msgValues.put("timestamp", ServerValue.TIMESTAMP);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/saloons/" + postKey + "/messages/" + key, msgValues);

        mDatabase.updateChildren(childUpdates);

        inputMessage.setText("");
    }

    public String encryptMsg(String text){
        char[] hintCharArray = hint.toCharArray();
        char[] charArray = text.toCharArray();
        char[] finalCharArray = new char[charArray.length];
        //int totalAscii = 0;

        //Calculating the total ascii
//        for(char ch : charArray){
//            totalAscii += (int) ch;
//        }
        int y = 0;
        for(int i = 0 ; i < charArray.length ; i++){
            if(i>=hintCharArray.length)y=0;

            int ascii = (int)charArray[i] + (int)hintCharArray[y];
            //If the ASCII nb is superior to 255 then go to the start
            if(ascii > 255){
                ascii -= 255;
            }
            finalCharArray[i] = (char)(ascii);
            y++;
        }
        return String.valueOf(finalCharArray);
    }

    public static String getHint() {
        return hint;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            final Dialog dialog = new Dialog(this);

            dialog.setContentView(R.layout.info_saloon);
            dialog.setTitle("Saloon's infos");

            dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            final TextView saloonHint = dialog.findViewById(R.id.saloonHint);
            final EditText supposedSaloonHint = dialog.findViewById(R.id.supposedSaloonHint);

            supposedSaloonHint.setText(hint);

            saloonHint.setText(saloon.getHint());

            dialog.findViewById(R.id.validate_action).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    supposedSaloonHint.setError(null);

                    String supposedHint = supposedSaloonHint.getText().toString().trim();

                    if (TextUtils.isEmpty(supposedHint)) {
                        supposedSaloonHint.setError(getString(R.string.error_field_required));
                    } else {
                        hint = supposedHint;
                        SharedPreferences sharedPref = ChatActivity.this.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(postKey, hint);
                        editor.commit();

                        finish();
                        startActivity(getIntent());


                        dialog.dismiss();
                    }
                }
            });

            dialog.findViewById(R.id.cancel_action).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
