package com.example.twinster.socialnetwork;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView rvFriendList;

    private DatabaseReference dbFriendsDatabase, dbUsers;
    private FirebaseAuth myAuth;

    private String current_user_id;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        rvFriendList = view.findViewById(R.id.rvFriendList);
        myAuth = FirebaseAuth.getInstance();
        current_user_id = myAuth.getCurrentUser().getUid();

        dbFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_user_id);
        dbFriendsDatabase.keepSynced(true );
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        dbUsers.keepSynced(true );


        rvFriendList.setHasFixedSize(true);
        rvFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        final FirebaseRecyclerAdapter<Friends, FriendsViewHolder> friendsAdapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(
                Friends.class,
                R.layout.users_row,
                FriendsViewHolder.class,
                dbFriendsDatabase
        ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends friend, int position) {
                viewHolder.setDate(friend.getDate());

                final String list_user_id = getRef(position).getKey();

                dbUsers.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String displayName = dataSnapshot.child("name").getValue().toString();
                        String thumbImage = dataSnapshot.child("thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")){
                            String userOnline =  dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);
                        }
                        viewHolder.setName(displayName);
                        viewHolder.setImage(thumbImage, getContext());


                        viewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CharSequence options[] = new CharSequence[]{getString(R.string.open_profile), getString(R.string.send_message)};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle(R.string.select_options);
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {

                                        if(position == 0){

                                            Intent profileIntent = new Intent(getContext(),ProfileActivity.class);
                                            profileIntent.putExtra("user_id",list_user_id);
                                            startActivity(profileIntent);
                                        }

                                        if(position == 1){

                                            Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                            chatIntent.putExtra("user_id",list_user_id);
                                            chatIntent.putExtra("user_name",displayName);
                                            startActivity(chatIntent);

                                        }

                                    }
                                });

                                builder.show();

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        rvFriendList.setAdapter(friendsAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tvUserDate, tvUserName;
        ImageView ivOnlineIcon;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            tvUserDate = view.findViewById(R.id.tvUserDate);
            tvUserName = view.findViewById(R.id.tvUserName);
            ivOnlineIcon = view.findViewById(R.id.ivOnlineIcon);
        }

        public void setDate(String date){
            tvUserDate.setText(date);
        }

        public void setName(String name){
            tvUserName.setText(name);
        }

        public void setImage(final String thumb_image, final Context context) {

            final CircleImageView userImageView =  view.findViewById(R.id.profileImgUsers);

            Picasso.with(context).load(thumb_image).
                    networkPolicy(NetworkPolicy.OFFLINE).
                    placeholder(R.drawable.defaultpic).
                    into(userImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(context).load(thumb_image).
                                    placeholder(R.drawable.defaultpic).
                                    into(userImageView);
                        }
                    });

        }

        public void setUserOnline(String userOnline){


            if (userOnline.equals("true")) {
                ivOnlineIcon.setVisibility(View.VISIBLE);
            } else {
                ivOnlineIcon.setVisibility(View.INVISIBLE);
            }
        }


    }
}
