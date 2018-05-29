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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfileImage;
    private TextView tvProfileName, tvTotalFriend;
    private Button btSendRequest, btDecline;

    private DatabaseReference profileDatabase;

    private ProgressDialog progressDialog;

    private DatabaseReference friendRequestDatabase;
    private DatabaseReference friendsDatabase;
    private DatabaseReference notificationDatabase;

    private DatabaseReference rootReference;

    private FirebaseUser currentUser;

    private String currentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra(UsersActivity.user_id_intent_key);

        rootReference = FirebaseDatabase.getInstance().getReference();

        profileDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        friendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        notificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvTotalFriend = findViewById(R.id.tvTotalFriends);
        btSendRequest = findViewById(R.id.btSendFreindRequest);
        btDecline = findViewById(R.id.btDeclineFreindRequest);


        currentState = "not_friends";


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loading_profile_data));
        progressDialog.setMessage(getString(R.string.wait_while_loading_user_data));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        profileDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String displayName = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                tvProfileName.setText(displayName);

                if (!image.equals("Default")){
                    ivProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }

                Picasso.with(ProfileActivity.this).load(image).
                        networkPolicy(NetworkPolicy.OFFLINE).
                        placeholder(R.drawable.defaultpic).into(ivProfileImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.defaultpic).into(ivProfileImage);
                    }
                });


                friendRequestDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("received")){

                                currentState = "req_received";
                                btSendRequest.setText(R.string.accept_friend_request);

                                btDecline.setVisibility(View.VISIBLE);
                                btDecline.setEnabled(true);

                            } else if(req_type.equals("sent")) {

                                    currentState = "req_sent";
                                    btSendRequest.setText(R.string.cancel_friend_request);

                            }

                            progressDialog.dismiss();


                        } else {

                            friendsDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){

                                        currentState = "friends";
                                        btSendRequest.setText(R.string.unfriend_this_person);

                                        btDecline.setVisibility(View.INVISIBLE);
                                        btDecline.setEnabled(false);

                                    }

                                    progressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    progressDialog.dismiss();

                                }
                            });

                        }

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

                    DatabaseReference newnotificationReference = rootReference.child("notifications").child(user_id).push();
                    String newNotificationId = newnotificationReference.getKey();

                    HashMap<String,String> notificationData = new HashMap<>();
                    notificationData.put("from",currentUser.getUid());
                    notificationData.put("type","request");

                    Map map = new HashMap();
                    map.put("Friend_req/" + currentUser.getUid() + "/" + user_id + "/request_type","sent");
                    map.put("Friend_req/" + user_id + "/" + currentUser.getUid() + "/request_type","received");
                    map.put("notifications/" + user_id + "/" + newNotificationId,notificationData);
                    rootReference.updateChildren(map, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){

                                Toast.makeText(ProfileActivity.this, R.string.error_while_sending_request,Toast.LENGTH_SHORT).show();

                            }
                            btSendRequest.setEnabled(true);
                            currentState = "req_sent";
                            btSendRequest.setText(R.string.cancel_friend_request);

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
                                    btSendRequest.setText(R.string.send_friend_request);

                                }
                            });

                        }
                    });

                }

                if(currentState.equals("req_received")){


                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + currentUser.getUid() + "/" + user_id + "/date",currentDate);
                    friendsMap.put("Friends/" + user_id + "/" + currentUser.getUid() + "/date",currentDate);

                    friendsMap.put("Friend_req/" + currentUser.getUid() + "/" + user_id,null);
                    friendsMap.put("Friend_req/" + user_id + "/" + currentUser.getUid(),null);
                    rootReference.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null){

                                btSendRequest.setEnabled(true);
                                currentState = "friends";
                                btSendRequest.setText(R.string.unfriend_this_person);

                                btDecline.setVisibility(View.INVISIBLE);
                                btDecline.setEnabled(false);

                            } else {

                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                            }


                        }
                    });

                }

                if (currentState.equals("friends")){
                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + currentUser.getUid() + "/" + user_id,null);
                    unfriendMap.put("Friends/" + user_id + "/" + currentUser.getUid(),null);
                    rootReference.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError == null){

                                currentState = "not_friends";
                                btSendRequest.setText(R.string.send_friend_request);

                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();
                            }
                            btSendRequest.setEnabled(true);
                        }
                    });
                }
            }
        });

        btDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map friendsMap = new HashMap();

                friendsMap.put("Friend_req/" + currentUser.getUid() + "/" + user_id,null);
                friendsMap.put("Friend_req/" + user_id + "/" + currentUser.getUid(),null);

                rootReference.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        if(databaseError == null){

                            btSendRequest.setEnabled(true);
                            currentState = "not_friends";
                            btSendRequest.setText(R.string.send_friend_request);

                            btDecline.setVisibility(View.INVISIBLE);
                            btDecline.setEnabled(false);

                        } else {

                            String error = databaseError.getMessage();
                            Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                        }


                    }
                });

            }
        });
    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        profileDatabase.child("online").setValue(true);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        profileDatabase.child("online").setValue(false);
//    }
}
