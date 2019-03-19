package com.example.user.testapp;



public class User {
    String mob;
    String picuri;
    int visitCount;

    public User() {
    }

    public User(String mob, String picuri) {
        this.mob = mob;
        this.picuri = picuri;
        this.visitCount = 1;
    }

    public String getmob() {
        return mob;
    }

    public String getpicuri() {
        return picuri;
    }

    public int getVisitCount() {
        return visitCount;
    }
}
