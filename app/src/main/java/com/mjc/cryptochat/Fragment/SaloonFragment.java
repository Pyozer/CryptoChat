package com.mjc.cryptochat.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.mjc.cryptochat.ChatActivity;
import com.mjc.cryptochat.MainActivity;
import com.mjc.cryptochat.Model.Saloon;
import com.mjc.cryptochat.R;
import com.mjc.cryptochat.SaloonViewHolder;

/**
 * Created by bijou on 13/09/2017.
 */

public abstract class SaloonFragment extends Fragment {
    private static final String TAG = "SaloonFragment";

    //Variables used by the UI
    private RecyclerView mSaloonList;

    //Variables used for the database
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.saloon_fragment_layout, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        mSaloonList = mView.findViewById(R.id.saloonList);
        mSaloonList.setHasFixedSize(true);
        mSaloonList.setItemAnimator(new DefaultItemAnimator());
        mSaloonList.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mSaloonList.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Saloon, SaloonViewHolder>(Saloon.class, R.layout.saloon_tile_layout, SaloonViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final SaloonViewHolder viewHolder, final Saloon saloon, final int position) {
                final DatabaseReference postRef = getRef(position);

                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Launch PostDetailActivity
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra(ChatActivity.EXTRA_POST_KEY, postKey);
                        intent.putExtra("saloonName",saloon.getName());
                        startActivity(intent);
                    }
                });

                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure to want to remove "+saloon.getName()+" ?")
                                .setTitle(R.string.remove_saloon);
                        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mDatabase.child("saloons").child(postKey).removeValue();
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return false;
                    }
                });

                viewHolder.bindToPost(saloon);
            }
        };
        mSaloonList.setAdapter(mAdapter);
    }


    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }
}
