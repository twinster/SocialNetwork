package com.example.twinster.socialnetwork;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private RecyclerView conversationList;

    private DatabaseReference conversationDatabase;
    private DatabaseReference messageDatabase;
    private DatabaseReference usersDatabase;

    private FirebaseAuth mAuth;

    private String current_user_id;

    private View mainView;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.fragment_chat, container, false);

        conversationList = mainView.findViewById(R.id.rvConversationList);
        mAuth = FirebaseAuth.getInstance();

        current_user_id = mAuth.getCurrentUser().getUid();

        conversationDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(current_user_id);

        conversationDatabase.keepSynced(true);
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        messageDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(current_user_id);
        usersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        conversationList.setHasFixedSize(true);
        conversationList.setLayoutManager(linearLayoutManager);


        // Inflate the layout for this fragment
        return mainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        Query conversationQuery = conversationDatabase.orderByChild("timestamp");

        FirebaseRecyclerAdapter<Conversation, ConversationViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<Conversation, ConversationViewHolder>(
                Conversation.class,
                R.layout.users_row,
                ConversationViewHolder.class,
                conversationQuery
        ) {
            @Override
            protected void populateViewHolder(final ConversationViewHolder conversationViewHolder, final Conversation conversation, int position) {



                final String list_user_id = getRef(position).getKey();

                Query lastMessageQuery = messageDatabase.child(list_user_id).limitToLast(1);

                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();
                        conversationViewHolder.setMessage(data, conversation.isSeen());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                usersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            conversationViewHolder.setUserOnline(userOnline);

                        }

                        conversationViewHolder.setName(userName);
                        conversationViewHolder.setUserImage(userThumb, getContext());

                        conversationViewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("user_id", list_user_id);
                                chatIntent.putExtra("user_name", userName);
                                startActivity(chatIntent);

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        conversationList.setAdapter(firebaseConvAdapter);

    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {

        View view;

        public ConversationViewHolder(View itemView) {
            super(itemView);

            view = itemView;

        }

        public void setMessage(String message, boolean isSeen){

            TextView userStatusView = view.findViewById(R.id.tvUserDate);
            userStatusView.setText(message);

            if(!isSeen){
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            } else {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            }

        }

        public void setName(String name){

            TextView userNameView =  view.findViewById(R.id.tvUserName);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx){

            CircleImageView userImageView = view.findViewById(R.id.profileImgUsers);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.defaultpic).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = view.findViewById(R.id.ivOnlineIcon);

            if(online_status.equals("true")){

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


    }



}