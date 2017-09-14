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
        String msg = message.getText();
        if (!ChatActivity.getHint().isEmpty()) msg = decryptMsg(message.getText());
        textView.setText(msg);
        authorView.setText("By "+message.getAuthorName());
    }
    public String decryptMsg(String text){
        char[] hintCharArray = ChatActivity.getHint().toCharArray();
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
            int ascii = (int)charArray[i] - (int)hintCharArray[y];
            //If the ASCII nb is superior to 255 then go to the start
            if(ascii > 0){
                ascii += 255;
            }
            finalCharArray[i] = (char)(ascii);
            y++;
        }
        return String.valueOf(finalCharArray);
    }
}