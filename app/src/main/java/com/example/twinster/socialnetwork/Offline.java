package com.example.twinster.socialnetwork;

import android.app.Application;
import android.support.v7.app.WindowDecorActionBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

public class Offline extends Application{

    private FirebaseAuth myAuth;
    private DatabaseReference dbUser;


    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);

        myAuth = FirebaseAuth.getInstance();

        if (myAuth.getCurrentUser() != null){
            dbUser = FirebaseDatabase.getInstance()
                    .getReference().child("Users").child(myAuth.getCurrentUser().getUid());

            dbUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null){
                        dbUser.child("online").onDisconnect().setValue(false);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }
}
