package com.mjc.cryptochat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mjc.cryptochat.Model.Message;

/**
 * Created by Thecr on 11/09/2017.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView textView;
    public TextView authorView;

    public MessageViewHolder(View itemView) {
        super(itemView);

        textView = (TextView) itemView.findViewById(R.id.messageTileText);
        authorView = (TextView) itemView.findViewById(R.id.messageTileAuthor);
    }

    public void bindToPost(Message message) {
        textView.setText(message.getText());
        authorView.setText("By "+message.getAuthorName());
    }
}