package com.example.comp4521project.MovieData;

public class Comment {
    String username;
    Boolean isGood;
    String comment;

    public Comment(String username, Boolean isGood, String comment){
        this.comment = comment;
        this.isGood = isGood;
        this.username = username;
    }

    public String getComment(){
        return comment;
    }

    public String getUsername(){
        return username;
    }

    public Boolean getIsGood(){
        return isGood;
    }
}
