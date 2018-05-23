package com.example.twinster.socialnetwork;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MaXaRa on 5/22/2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>{
    private List<Messages> messagesList;
    private FirebaseAuth myAuth;
    private DatabaseReference userDatabase;
    public MessagesAdapter(List<Messages> messagesList){
        this.messagesList = messagesList;
    }


    @NonNull
    @Override
    public MessagesAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row,parent,false);
        return new MessageViewHolder(v);
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public CircleImageView profileImg;
        public ImageView ivImageMessage;
        public TextView tvMessageTIme;

        public MessageViewHolder(View view){
            super(view);
            messageText = view.findViewById(R.id.tvSMS);
            profileImg = view.findViewById(R.id.smsPersonImg);
            ivImageMessage = view.findViewById(R.id.ivImageMessage);
            tvMessageTIme = view.findViewById(R.id.tvMessageTIme);
        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        myAuth = FirebaseAuth.getInstance();
        String current_user_id = myAuth.getCurrentUser().getUid();
        final Messages c = messagesList.get(position);
        String from_user = c.getFrom();
        String message_type = c.getType();
        Long messageTime = c.getTime();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                Picasso.with(holder.profileImg.getContext()).load(thumb_image).
                        placeholder(R.drawable.defaultpic).
                        into(holder.profileImg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Date date = new Date(messageTime);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        String timeFormatted = formatter.format(date);

        holder.tvMessageTIme.setText(timeFormatted);


        if (message_type.equals("text")){
            holder.messageText.setText(c.getMessage());
            holder.ivImageMessage.setVisibility(View.INVISIBLE);
        }else{
            holder.messageText.setVisibility(View.INVISIBLE);

            holder.ivImageMessage.setVisibility(View.VISIBLE);

            Picasso.with(holder.ivImageMessage.getContext()).load(c.getMessage()).
                    networkPolicy(NetworkPolicy.OFFLINE).
                    placeholder(R.drawable.defaultpic).
                    into(holder.ivImageMessage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(holder.ivImageMessage.getContext()).load(c.getMessage()).
                                    placeholder(R.drawable.defaultpic).
                                    into(holder.ivImageMessage);
                        }
                    });
        }

        if (from_user.equals(current_user_id)) {
            holder.messageText.setBackgroundColor(Color.BLUE);
            holder.messageText.setTextColor(Color.WHITE);
        }
        else{
            holder.messageText.setBackgroundColor(Color.GRAY);
            holder.messageText.setTextColor(Color.BLACK);
        }
        holder.messageText.setText(c.getMessage());


    }
}
