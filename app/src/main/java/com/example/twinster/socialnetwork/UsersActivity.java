package com.example.twinster.socialnetwork;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    Toolbar myToolBar;
    private RecyclerView rvUsersList;
    private DatabaseReference usersDatabase;
    public static String user_id_intent_key =  "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        rvUsersList = findViewById(R.id.rvUsersList);
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        myToolBar = findViewById(R.id.users_tool_bar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle("Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvUsersList.setHasFixedSize(true);
        rvUsersList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Users, UserViewHolder> fireBaseAdapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(
                Users.class,
                R.layout.users_row,
                UserViewHolder.class,
                usersDatabase
        ) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, Users user, int position) {
                viewHolder.setName(user.getName());
                viewHolder.setImage(user.getThumb_image(), getApplicationContext());

                final String user_id = getRef(position).getKey();

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent profileIntent = new Intent(UsersActivity.this,ProfileActivity.class);
                        profileIntent.putExtra(user_id_intent_key,user_id);
                        startActivity(profileIntent);

                    }
                });
            }
        };

        rvUsersList.setAdapter(fireBaseAdapter);
    }


    public static class UserViewHolder extends RecyclerView.ViewHolder {
        View view;

        public void setName(String name){
            TextView userName = view.findViewById(R.id.tvUserName);
            userName.setText(name);
        }

        public UserViewHolder(View itemView) {
            super(itemView);
            view = itemView;

        }

        public void setImage(final String thumb_image, final Context context) {

            final CircleImageView userImageView = (CircleImageView) view.findViewById(R.id.profileImgUsers);

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
    }
}
