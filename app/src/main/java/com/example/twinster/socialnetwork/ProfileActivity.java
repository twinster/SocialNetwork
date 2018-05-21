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

        final String user_id = getIntent().getStringExtra("user_id");

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
        progressDialog.setTitle("Loading Profile Data");
        progressDialog.setMessage("Please wait while loading the user data");
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
                            if(req_type.equals("recieved")){

                                currentState = "req_recieved";
                                btSendRequest.setText("Accept Friend Request");

                                btDecline.setVisibility(View.VISIBLE);
                                btDecline.setEnabled(true);

                            } else if(req_type.equals("sent")) {

                                    currentState = "req_sent";
                                    btSendRequest.setText("Cancel Friend Request");

                            }

                            progressDialog.dismiss();


                        } else {

                            friendsDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){

                                        currentState = "friends";
                                        btSendRequest.setText("Unfriend this person");

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

                                Toast.makeText(ProfileActivity.this,"there were some error while sending request",Toast.LENGTH_SHORT).show();

                            }
                            btSendRequest.setEnabled(true);
                            currentState = "req_sent";
                            btSendRequest.setText("Cancel Friend Request");

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

                if(currentState.equals("req_recieved")){

                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/" + currentUser.getUid() + "/" + user_id + "/date",currentDate);
                    friendsMap.put("Friends/" + user_id + "/" + currentUser.getUid() + "/date",currentDate);

                    friendsMap.put("Friend_req/" + currentUser.getUid() + "/" + user_id,null);
                    friendsMap.put("Friends_req/" + user_id + "/" + currentUser.getUid(),null);
                    rootReference.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){

                                btSendRequest.setEnabled(true);
                                currentState = "friends";
                                btSendRequest.setText("Unfriend this person");

                            } else {

                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                            }


                        }
                    });

                }

                if (currentUser.equals("friends")){
                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friend/" + currentUser.getUid() + "/" + user_id,null);
                    unfriendMap.put("Friends/" + user_id + "/" + currentUser.getUid(),null);
                    rootReference.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){

                                currentState = "not_friends";
                                btSendRequest.setText("Send Friend Request");

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

    }
}
