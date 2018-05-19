package com.example.twinster.socialnetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class UsersActivity extends AppCompatActivity {
    Toolbar myToolBar;
    private RecyclerView rvUsersList;
    private DatabaseReference usersDatabase;

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
    }
}
