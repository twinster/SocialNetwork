package com.example.twinster.socialnetwork;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MaXaRa on 5/22/2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>{
    private List<Messages> messagesList;
    private FirebaseAuth myAuth;
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
        public MessageViewHolder(View view){
            super(view);
            messageText = view.findViewById(R.id.tvSMS);
            profileImg = view.findViewById(R.id.smsPersonImg);
        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        String current_user_id = myAuth.getCurrentUser().getUid();
        Messages c = messagesList.get(position);
        String from_user = c.getFrom();

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
