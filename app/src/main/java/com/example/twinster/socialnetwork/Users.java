package com.example.twinster.socialnetwork;

public class Users {
    private String name;
    private String image;
    private String thumb_image;

    public Users () {}

    public Users(String name, String image,String thumb_image) {
        this.name = name;
        this.image = image;
        this.thumb_image = thumb_image;
    }


    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }
}
