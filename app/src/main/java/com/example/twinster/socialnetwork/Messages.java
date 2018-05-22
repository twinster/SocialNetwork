package com.example.twinster.socialnetwork;

/**
 * Created by MaXaRa on 5/22/2018.
 */

public class Messages {

    private String message;
    private String type;
    private String from;
    private Boolean seen;
    private Long time;



    public Messages(String message, Boolean seen, Long time, String type){
        this.setMessage(message);
        this.setSeen(seen);
        this.setTime(time);
        this.setType(type);

    }

    public Messages(){}

    public Messages(String from){ this.setFrom(from); }


    public String getMessage() {
        return message;
    }

    public Boolean getSeen() {
        return seen;
    }

    public Long getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getFrom() {
        return from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
