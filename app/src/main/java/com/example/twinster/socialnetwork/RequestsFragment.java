package com.example.twinster.socialnetwork;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
public class RequestsFragment extends Fragment {

    View view;

    private RecyclerView rvRequestsList;

    private DatabaseReference dbRequestsDatabase, dbUsers;
    private FirebaseAuth myAuth;

    private String current_user_id;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_requests, container, false);

        rvRequestsList = view.findViewById(R.id.rvRequestsList);
        myAuth = FirebaseAuth.getInstance();
        current_user_id = myAuth.getCurrentUser().getUid();

        dbRequestsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(current_user_id);
        dbRequestsDatabase.keepSynced(true );
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        dbUsers.keepSynced(true );


        rvRequestsList.setHasFixedSize(true);
        rvRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


        final FirebaseRecyclerAdapter<Friends, RequestsFragment.RequestsViewHolder> friendsAdapter =
                new FirebaseRecyclerAdapter<Friends, RequestsFragment.RequestsViewHolder>(
                Friends.class,
                R.layout.users_row,
                RequestsFragment.RequestsViewHolder.class,
                dbRequestsDatabase
        ) {
            @Override
            protected void populateViewHolder(final RequestsFragment.RequestsViewHolder viewHolder, Friends friend, int position) {
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

                                CharSequence options[] = new CharSequence[]{"Open Profile", "send message"};

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Options");
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

        rvRequestsList.setAdapter(friendsAdapter);
    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView tvUserDate, tvUserName;
        ImageView ivOnlineIcon;

        public RequestsViewHolder(View itemView) {
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
