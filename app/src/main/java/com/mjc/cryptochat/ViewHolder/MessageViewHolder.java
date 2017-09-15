package com.mjc.cryptochat.ViewHolder;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mjc.cryptochat.Activity.ChatActivity;
import com.mjc.cryptochat.Activity.MainActivity;
import com.mjc.cryptochat.Model.Message;
import com.mjc.cryptochat.R;
import com.mjc.cryptochat.Utils.CryptManager;

/**
 * Created by Thecr on 11/09/2017.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private TextView authorView;
    private ImageView authorIcon;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public MessageViewHolder(View itemView) {
        super(itemView);

        textView = (TextView) itemView.findViewById(R.id.messageTileText);
        authorView = (TextView) itemView.findViewById(R.id.messageTileAuthor);
        authorIcon = (ImageView) itemView.findViewById(R.id.imageView);
    }

    public void bindToPost(Message message) {
        if(mAuth.getCurrentUser().getUid().equals(message.getUid())){
            authorIcon.setVisibility(View.INVISIBLE);
            textView.setBackground(ContextCompat.getDrawable(MainActivity.getmContext(),R.drawable.badge_chat));
        }else{
            authorIcon.setVisibility(View.VISIBLE);
            textView.setBackground(ContextCompat.getDrawable(MainActivity.getmContext(),R.drawable.badge));
        }

        String msg = message.getText();
        if (!ChatActivity.getHint().isEmpty()) msg = CryptManager.decryptMsg(message.getText(), ChatActivity.getHint());
        textView.setText(msg);
        authorView.setText("By "+message.getAuteur());
    }
}