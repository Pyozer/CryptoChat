package com.mjc.cryptochat.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mjc.cryptochat.Model.Message;
import com.mjc.cryptochat.Model.Saloon;
import com.mjc.cryptochat.Model.User;
import com.mjc.cryptochat.R;
import com.mjc.cryptochat.Utils.CryptManager;
import com.mjc.cryptochat.Utils.PrefManager;
import com.mjc.cryptochat.ViewHolder.MessageViewHolder;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends BaseActivity {

    private static final String TAG = "ChatActivity";
    public static final String EXTRA_SALOON_KEY = "saloon_key";

    private RecyclerView messageList;
    private EditText inputMessage;
    private FirebaseRecyclerAdapter mAdapter;

    private String saloonKey;

    private Saloon saloon;
    private static String keySupposed = "";

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.redirectToLogin = true; // On spécifie qu'il faut être connecté pour accéder ici
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        saloonKey = extras.getString(EXTRA_SALOON_KEY);

        prefManager = new PrefManager(this);
        keySupposed = prefManager.getSaloonHintSaved(saloonKey);

        setTitle(extras.getString("saloonName"));

        findViewById(R.id.sendMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!keySupposed.isEmpty()) validForm();
                else Toast.makeText(ChatActivity.this, R.string.need_hint, Toast.LENGTH_SHORT).show();
            }
        });
        messageList = findViewById(R.id.messageList);
        inputMessage = findViewById(R.id.inputMessage);

        showProgressDialog();
        mDatabase.child("saloons/" + saloonKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();
                Saloon saloonGet = dataSnapshot.getValue(Saloon.class);
                if (saloonGet != null) {
                    saloon = saloonGet;

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
                } else {  //The saloon has been removed
                    Toast.makeText(ChatActivity.this, R.string.saloon_deleted, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ChatActivity.this, MainActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        messageList.setHasFixedSize(true);
        messageList.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager mManager = new LinearLayoutManager(ChatActivity.this);
        mManager.setReverseLayout(false);
        mManager.setStackFromEnd(true);
        messageList.setLayoutManager(mManager);

        Query postsQuery = mDatabase.child("saloons").child(saloonKey).child("messages").orderByChild("timestamp");;
        mAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(Message.class, R.layout.message_tile_layout, MessageViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, final Message message, final int position) {
                viewHolder.bindToPost(getApplicationContext(), message);
            }
        };
        messageList.setAdapter(mAdapter);
    }

    private void validForm() {
        inputMessage.setError(null);

        String text = inputMessage.getText().toString().trim();
        text = CryptManager.encryptMsg(text, keySupposed);

        if (TextUtils.isEmpty(text)) {
            inputMessage.setError(getString(R.string.error_field_required));
        } else {
            writeNewMessage(text);
        }
    }

    private void writeNewMessage(final String text) {
        showProgressDialog();
        final String authorUid = getUid();
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
        mDatabase.child("saloons").child(saloonKey).child("msgNb").setValue(saloon.getMsgNb() + 1);

        String key = mDatabase.child("saloons").child(saloonKey).child("messages").push().getKey();
        Message msg = new Message(userId, userName, text);

        Map<String, Object> msgValues = msg.toMap();
        msgValues.put("timestamp", ServerValue.TIMESTAMP);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/saloons/" + saloonKey + "/messages/" + key, msgValues);

        mDatabase.updateChildren(childUpdates);

        inputMessage.setText("");
    }

    public static String getKeySupposed() {
        return keySupposed;
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

            dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            final TextView saloonHint = dialog.findViewById(R.id.saloonHint);
            final EditText supposedSaloonKey = dialog.findViewById(R.id.supposedSaloonKey);
            final TextView msgTemoinSaloon = dialog.findViewById(R.id.messageTemoin);

            supposedSaloonKey.setText(keySupposed);
            msgTemoinSaloon.setText(CryptManager.decryptMsg(saloon.getMsgDefaultCrypt(), keySupposed));
            saloonHint.setText(saloon.getHint());

            supposedSaloonKey.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                public void afterTextChanged(Editable s) {
                    String keyEnter = supposedSaloonKey.getText().toString();
                    String msgDefaultDecrypt = CryptManager.decryptMsg(saloon.getMsgDefaultCrypt(), keyEnter);

                    msgTemoinSaloon.setText(msgDefaultDecrypt);
                }
            });

            dialog.findViewById(R.id.validate_action).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    supposedSaloonKey.setError(null);

                    String supposedHint = supposedSaloonKey.getText().toString().trim();

                    if (TextUtils.isEmpty(supposedHint)) {
                        supposedSaloonKey.setError(getString(R.string.error_field_required));
                    } else {
                        prefManager.saveHintOfSaloon(saloonKey, supposedHint);

                        startActivity(getIntent());
                        finish();

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
