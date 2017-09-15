package com.mjc.cryptochat.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mjc.cryptochat.Activity.ChatActivity;
import com.mjc.cryptochat.Model.Message;
import com.mjc.cryptochat.R;
import com.mjc.cryptochat.Utils.CryptManager;

/**
 * Created by Thecr on 11/09/2017.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private TextView authorView;


    public MessageViewHolder(View itemView) {
        super(itemView);

        textView = (TextView) itemView.findViewById(R.id.messageTileText);
        authorView = (TextView) itemView.findViewById(R.id.messageTileAuthor);
    }

    public void bindToPost(Message message) {
        String msg = message.getText();
        if (!ChatActivity.getHint().isEmpty()) msg = CryptManager.decryptMsg(message.getText());
        textView.setText(msg);
        authorView.setText("By "+message.getAuthorName());
    }
}