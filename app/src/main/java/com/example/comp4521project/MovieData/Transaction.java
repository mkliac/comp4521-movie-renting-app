package com.example.comp4521project.MovieData;

public class Transaction {
    private String dueDate;
    private String id;
    private String user;
    private Float price;
    public Transaction(String dueDate,String id,String user,Float price){
        this.dueDate=dueDate;
        this.id=id;
        this.price=price;
    }
    public String getDueDate(){
        return dueDate;
    }
    public String getMovieID(){
        return id;
    }
    public Float getPurchasePrice(){
        return price;
    }
    public String getBuyer(){
        return user;
    }
}
