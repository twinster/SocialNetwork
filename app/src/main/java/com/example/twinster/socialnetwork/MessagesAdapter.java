package com.example.twinster.socialnetwork;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by MaXaRa on 5/22/2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>{
    private List<Messages> messagesList;
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
        Messages c = messagesList.get(position);
        holder.messageText.setText(c.getMessage());
    }
}
