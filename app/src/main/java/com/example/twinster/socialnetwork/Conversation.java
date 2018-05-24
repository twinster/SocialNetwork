package com.example.twinster.socialnetwork;

/**
 * Created by user on 5/24/2018.
 */

public class Conversation {

    private boolean seen;
    private long timestamp;

    public Conversation(){

    }

    public boolean isSeen() {
        return seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Conversation(boolean seen, long timestamp){
        this.seen = seen;
        this.timestamp = timestamp;
    }
}
