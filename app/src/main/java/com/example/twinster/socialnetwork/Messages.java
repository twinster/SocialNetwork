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



    public Messages(String message, Boolean seen, Long time, String type, String from){
        this.message = message;
        this.seen = seen;
        this.time = time;
        this.type = type;
        this.from = from;

    }

    public Messages(){}


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
}
