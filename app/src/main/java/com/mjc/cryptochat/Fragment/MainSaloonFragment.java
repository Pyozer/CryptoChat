package com.mjc.cryptochat.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by bijou on 13/09/2017.
 */

public class MainSaloonFragment extends SaloonFragment {

    public MainSaloonFragment() {
        super();
    }

    @Override
    public Query getQuery(DatabaseReference databaseRef) {
        return databaseRef.child("saloons").orderByChild("msgNb");
    }
}
