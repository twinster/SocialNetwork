package com.example.twinster.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private String chatuser;
    private Toolbar chatToolBar;

    private DatabaseReference rootReference;

    private TextView chatToolBarUsername;
    private TextView chatToolBarLastseen;
    private CircleImageView chatToolBarImage;
    private FirebaseAuth mAuth;

    private String currentUserId;

    private ImageButton ibPlusIcon, ibSendIcon;
    private EditText etMessage;

    private RecyclerView smsList;
    private SwipeRefreshLayout refreshLayout;

    private final List<Messages> messagesList = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;

    private MessagesAdapter messagesAdapter;

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int currentPage = 1;

    private static final int GALLERY_PICK = 1;

    private StorageReference imageStorage;

    private int itemPosition = 0;

    private String lastKey = "";
    private String previousKey="";


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
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();


        chatuser = getIntent().getStringExtra("user_id");
        String displayName = getIntent().getStringExtra("user_name");
        //getSupportActionBar().setTitle(displayName);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_bar,null);

        actionBar.setCustomView(action_bar_view);


        chatToolBarUsername = findViewById(R.id.chat_toolbar_username);
        chatToolBarLastseen = findViewById(R.id.chat_toolbar_userslasteen);
        chatToolBarImage = findViewById(R.id.chat_toolbar_iamge);

        ibPlusIcon = findViewById(R.id.ibPlusIcon);
        ibSendIcon = findViewById(R.id.ibSendIcon);
        etMessage = findViewById(R.id.etMessage);

        messagesAdapter = new MessagesAdapter(messagesList);

        smsList = findViewById(R.id.chatMessagesList);
        refreshLayout = findViewById(R.id.chatSwipeRefreshLayout);

        linearLayoutManager = new LinearLayoutManager(this);

        smsList.setHasFixedSize(true);

        smsList.setLayoutManager(linearLayoutManager);

        smsList.setAdapter(messagesAdapter);

        imageStorage = FirebaseStorage.getInstance().getReference();



        lostMessage();

        chatToolBarUsername.setText(displayName);

        rootReference.child("Users").child(chatuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();

                Picasso.with(chatToolBarImage.getContext()).load(image).
                        networkPolicy(NetworkPolicy.OFFLINE).
                        placeholder(R.drawable.defaultpic).
                        into(chatToolBarImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(chatToolBarImage.getContext()).load(image).
                                        placeholder(R.drawable.defaultpic).
                                        into(chatToolBarImage);
                            }
                        });

                if (online.equals("true")) {

                    chatToolBarLastseen.setText(R.string.online);
                } else {

                    getTimeAgo getTimeAgo = new getTimeAgo();
                    long lastTime = Long.parseLong(online);

                    String lastSeenTime = getTimeAgo.getTimeAgo(lastTime,getApplicationContext());

                    chatToolBarLastseen.setText(lastSeenTime);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rootReference.child("Chat").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(chatuser)){

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen",false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + currentUserId + "/" + chatuser ,chatAddMap);
                    chatUserMap.put("Chat/" + chatuser + "/" + currentUserId ,chatAddMap);

                    rootReference.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if(databaseError != null){
                                Log.d(getString(R.string.chat_log),databaseError.getMessage().toString());
                            }

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ibSendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendMessage();

            }

            private void sendMessage() {

                String message = etMessage.getText().toString();

                if (!TextUtils.isEmpty(message)) {

                    String current_user_ref = "messages/" + currentUserId + "/" + chatuser;
                    String chat_user_ref = "messages/" + chatuser + "/" + currentUserId;

                    DatabaseReference user_message_push = rootReference.child("messages")
                            .child(currentUserId).child(chatuser).push();

                    String push_id = user_message_push.getKey();

                    Map messageMap = new HashMap();
                    messageMap.put("message",message);
                    messageMap.put("seen",false);
                    messageMap.put("type","text");
                    messageMap.put("time",ServerValue.TIMESTAMP);
                    messageMap.put("from", currentUserId);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                    etMessage.setText("");

                    rootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            //Log.d("CHAT_LOG",databaseError.getMessage().toString());
                        }
                    });
                }

            }
        });

        ibPlusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent,getString(R.string.select_image)),GALLERY_PICK);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage++;

                itemPosition = 0;

                loadMoreMessages();
            }
        });

        chatToolBarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(ChatActivity.this, ProfileActivity.class);
                profileIntent.putExtra("user_id",chatuser);
                startActivity(profileIntent);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            final String current_user_ref = "messages/" + currentUserId + "/" + chatuser;
            final String chat_user_ref = "messages/" + chatuser + "/" + currentUserId;

            DatabaseReference user_message_push = rootReference.child("messages")
                    .child(currentUserId).child(chatuser).push();

            final String push_id = user_message_push.getKey();

            StorageReference filepath = imageStorage.child("message_images").child(push_id + ".jpg");

            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()){

                        String download_uri = task.getResult().getDownloadUrl().toString();


                        Map messageMap = new HashMap();
                        messageMap.put("message",download_uri);
                        messageMap.put("seen",false);
                        messageMap.put("type","image");
                        messageMap.put("time",ServerValue.TIMESTAMP);
                        messageMap.put("from", currentUserId);

                        Map messageUserMap = new HashMap();
                        messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                        messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                        etMessage.setText("");

                        rootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null){
                                    Log.d(getString(R.string.chat_log),databaseError.getMessage().toString());
                                }
                            }
                        });
                    }

                }
            });
        }
    }

    private void loadMoreMessages() {
        DatabaseReference messageRef = rootReference.child("messages").child(currentUserId).child(chatuser);

        Query messageQuery = messageRef.orderByKey().endAt(lastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);
                String messageKey = dataSnapshot.getKey();

                messagesList.add(itemPosition++,messages);

                if (!previousKey.equals(messageKey)){
                    messagesList.add(itemPosition++,messages);
                } else {
                    previousKey = messageKey;
                }

                if(itemPosition == 1){


                    lastKey = messageKey;
                }



                Log.d(getString(R.string.totalkeys),getString(R.string.last_key) +lastKey + getString(R.string.prev_key) + previousKey + getString(R.string.message_key) + messageKey);

                messagesAdapter.notifyDataSetChanged();

                refreshLayout.setRefreshing(false);

                linearLayoutManager.scrollToPositionWithOffset(10,0);

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


    }

    private void lostMessage() {

        DatabaseReference messageRef = rootReference.child("messages").child(currentUserId).child(chatuser);

        Query messageQuery = messageRef.limitToLast(currentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);

                itemPosition++;

                if(itemPosition == 1){
                    String messageKey = dataSnapshot.getKey();

                    lastKey = messageKey;
                    previousKey = messageKey;

                }

                messagesList.add(messages);
                messagesAdapter.notifyDataSetChanged();

                smsList.scrollToPosition(messagesList.size() - 1);

                refreshLayout.setRefreshing(false);
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
    }
}
