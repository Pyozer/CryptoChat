package com.mjc.cryptochat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Thecr on 11/09/2017.
 */

public class SaloonViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;

    public SaloonViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.saloonTileName);
        authorView = (TextView) itemView.findViewById(R.id.saloonTileAuthor);
    }

    public void bindToPost(Saloon saloon) {
        titleView.setText(saloon.getName());
        authorView.setText(saloon.getHint());
    }
}