package com.example.twinster.socialnetwork;

/**
 * Created by MaXaRa on 5/22/2018.
 */

public class Messages {

    private String message;
    private Boolean seen;
    private Long time;
    private String type;


    public Messages(String message, Boolean seen, Long time, String type){
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;

    }

    public Messages(){}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
