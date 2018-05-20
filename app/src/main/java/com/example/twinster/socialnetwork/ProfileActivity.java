package com.example.twinster.socialnetwork;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfileImage;
    private TextView tvProfileName, tvTotalFriend;
    private Button btSendRequest;

    private DatabaseReference profileDatabase;

    private ProgressDialog progressDialog;

    private DatabaseReference friendRequestDatabase;

    private FirebaseUser currentUser;

    private String currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        profileDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        tvTotalFriend = (TextView) findViewById(R.id.tvTotalFriends);
        btSendRequest = (Button) findViewById(R.id.btSendFreindRequest);


        currentState = "not_friends";


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Profile Data");
        progressDialog.setMessage("Please wait while loading the user data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        profileDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String displayName = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                tvProfileName.setText(displayName);

                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.defaultpic).into(ivProfileImage);


                friendRequestDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("recieved")){

                                currentState = "req_recieved";
                                btSendRequest.setText("Accept Friend Request");

                            } else if(req_type.equals("sent")) {

                                    currentState = "req_sent";
                                    btSendRequest.setText("Cancel Friend Request");
                            }

                        }

                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btSendRequest.setEnabled(false);

                if (currentState.equals("not_friends")) {

                    friendRequestDatabase.child(currentUser.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                friendRequestDatabase.child(user_id).child(currentUser.getUid()).child("request_type")
                                        .setValue("recieved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        btSendRequest.setEnabled(true);
                                        currentState = "req_sent";
                                        btSendRequest.setText("Cancel Friend Request");

                                       // Toast.makeText(ProfileActivity.this, "Request sent successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(ProfileActivity.this, "Failed sending request", Toast.LENGTH_SHORT).show();

                            }
                        }

                    });
                }

                if(currentState.equals("req_sent")){

                    friendRequestDatabase.child(currentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friendRequestDatabase.child(user_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    btSendRequest.setEnabled(true);
                                    currentState = "not_friends";
                                    btSendRequest.setText("Send Friend Request");

                                }
                            });

                        }
                    });

                }
            }
        });

    }
}
