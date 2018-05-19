package com.example.twinster.socialnetwork;

public class Users {
    private String name;
    private String image;

    public Users () {}

    public Users(String name, String image) {
        this.name = name;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
