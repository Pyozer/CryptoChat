package com.mjc.cryptochat.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mjc.cryptochat.Model.Saloon;
import com.mjc.cryptochat.R;

/**
 * Created by Thecr on 11/09/2017.
 */

public class SaloonViewHolder extends RecyclerView.ViewHolder {

    private TextView titleView;
    private TextView authorView;
    private TextView msgNbView;

    public SaloonViewHolder(View itemView) {
        super(itemView);

        titleView = itemView.findViewById(R.id.saloonTileName);
        authorView = itemView.findViewById(R.id.saloonTileAuthor);
        msgNbView = itemView.findViewById(R.id.saloonMsgNb);
    }

    public void bindToPost(Saloon saloon) {
        String author = "By "+saloon.getAuthorName();
        titleView.setText(saloon.getName());
        authorView.setText(author);
        msgNbView.setText(String.valueOf(saloon.getMsgNb()));
    }
}