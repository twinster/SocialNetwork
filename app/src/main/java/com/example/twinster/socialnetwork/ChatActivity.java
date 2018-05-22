package com.example.twinster.socialnetwork;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String chatuser;
    private Toolbar chatToolBar;

    private DatabaseReference rootReference;

    private TextView chatToolBarUsername;
    private TextView chatToolBarLastseen;
    private CircleImageView chatToolBarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatToolBar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(chatToolBar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        rootReference = FirebaseDatabase.getInstance().getReference();


        chatuser = getIntent().getStringExtra("user_id");
        String displayName = getIntent().getStringExtra("user_name");
        //getSupportActionBar().setTitle(displayName);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_bar,null);

        actionBar.setCustomView(action_bar_view);


        chatToolBarUsername = findViewById(R.id.chat_toolbar_username);
        chatToolBarLastseen = findViewById(R.id.chat_toolbar_userslasteen);
        chatToolBarImage = findViewById(R.id.chat_toolbar_iamge);

        chatToolBarUsername.setText(displayName);

        rootReference.child("Users").child(chatuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                if (online.equals("true")) {

                    chatToolBarLastseen.setText("Online");
                } else {
                    chatToolBarLastseen.setText(online);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
