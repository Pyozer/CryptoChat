package com.mjc.cryptochat.ViewHolder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mjc.cryptochat.Activity.ChatActivity;
import com.mjc.cryptochat.Model.Message;
import com.mjc.cryptochat.R;
import com.mjc.cryptochat.Utils.CryptManager;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private TextView authorView;
    private ImageView authorIcon;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public MessageViewHolder(View itemView) {
        super(itemView);

        textView = itemView.findViewById(R.id.messageTileText);
        authorView = itemView.findViewById(R.id.messageTileAuthor);
        authorIcon = itemView.findViewById(R.id.imageView);
    }

    public void bindToPost(Context context, Message message) {
        if (mAuth.getCurrentUser().getUid().equals(message.getUid())) {
            authorIcon.setVisibility(View.INVISIBLE);
            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.badge_chat));
        } else {
            authorIcon.setVisibility(View.VISIBLE);
            textView.setBackground(ContextCompat.getDrawable(context, R.drawable.badge));
        }

        String msg = message.getText();
        if (!ChatActivity.getKeySupposed().isEmpty())
            msg = CryptManager.decryptMsg(message.getText(), ChatActivity.getKeySupposed());

        textView.setText(msg);
        String auteurText = "By " + message.getAuteur();
        authorView.setText(auteurText);
    }
}