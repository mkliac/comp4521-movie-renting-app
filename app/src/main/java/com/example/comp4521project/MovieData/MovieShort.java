package com.example.comp4521project.MovieData;

public class MovieShort {
    private String id = "dummydata", name = "dummydata", year = "dummydata";
    private String category = "00000000";
    private Integer popularity = 0;
    private Float price = 0F;

    private boolean action = false;
    private boolean adventure = false;
    private boolean cartoon = false;
    private boolean comedy = false;
    private boolean documentary = false;
    private boolean horror = false;
    private boolean mystery = false;
    private boolean scifi = false;


    public MovieShort(String id, Float price){this.id = id; this.price = price;}
    public MovieShort(String id){this.id = id;}
    public MovieShort(String id, String name, String year, Integer popularity, Float price, String category){
        setId(id);
        setName(name);
        setYear(year);
        setPopularity(popularity);
        setPrice(price);
        setCategory(category);
    }

    public void setId(String id){
        this.id = id;
    }
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
    public void setCategory(String category){
        action = (category.charAt(0)=='1')?true:false;
        adventure = (category.charAt(1)=='1')?true:false;
        cartoon = (category.charAt(2)=='1')?true:false;
        comedy = (category.charAt(3)=='1')?true:false;
        documentary = (category.charAt(4)=='1')?true:false;
        horror = (category.charAt(5)=='1')?true:false;
        mystery = (category.charAt(6)=='1')?true:false;
        scifi = (category.charAt(7)=='1')?true:false;
    }

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getYear(){
        return year;
    }
    public Integer getPopularity(){
        return popularity;
    }
    public Float getPrice(){return price;}

}
