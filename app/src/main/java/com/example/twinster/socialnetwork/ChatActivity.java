package com.example.twinster.socialnetwork;

import android.content.Context;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

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

    private final List<Messages> messagesList = new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;

    private MessagesAdapter messagesAdapter;


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

        linearLayoutManager = new LinearLayoutManager(this);

        smsList.setHasFixedSize(true);

        smsList.setLayoutManager(linearLayoutManager);

        smsList.setAdapter(messagesAdapter);



        lostMessage();

        chatToolBarUsername.setText(displayName);

        rootReference.child("Users").child(chatuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String online = dataSnapshot.child("online").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                if (online.equals("true")) {

                    chatToolBarLastseen.setText("Online");
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
                               Log.d("CHAT_LOG",databaseError.getMessage().toString());
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

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                    rootReference.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Log.d("CHAT_LOG",databaseError.getMessage().toString());
                        }
                    });
                }

            }
        });

    }

    private void lostMessage() {
        rootReference.child("messages").child(currentUserId).child(chatuser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messagesAdapter.notifyDataSetChanged();
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
