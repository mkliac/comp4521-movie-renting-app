package com.example.comp4521project.model;

public class MovieBrief {
    private String name = "";
    private String id = "";
    private String year = "";
    private Float price = 0F;
    private Integer popularity = 0;

    public MovieBrief(String id, String name, String year, Integer popularity, Float price){
        setName(name);
        setId(id);
        setYear(year);
        setPrice(price);
        setPopularity(popularity);
    }

    public MovieBrief(String id, Float price){this.id = id; this.price = price;}


    public void setId(String id) { this.id = id; }
    public void setName(String name){
        this.name = name;
    }
    public void setYear(String year){
        this.year = year;
    }
    public void setPopularity(Integer popularity){
        this.popularity = popularity;
    }
    public void setPrice(Float price){this.price = price;}
    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public Integer getPopularity(){
        return popularity;
    }
    public Float getPrice(){return price;}

}
