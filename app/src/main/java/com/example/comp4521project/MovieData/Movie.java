package com.example.comp4521project.MovieData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Movie implements Parcelable {
    private String id = "dummydata", name = "dummydata", year = "dummydata", description = "dummydata";
    private Integer popularity = 0;
    private Float price = 0F;
    private Bitmap image;
    private String trailerURL = "dummydata";
    public Movie(String id, Integer popularity){this.id = id; this.popularity = popularity;}
    public Movie(String id, String name, String year, String description, Integer popularity, String trailerURL, Float price){
        setId(id);
        setName(name);
        setYear(year);
        setDescription(description);
        setPopularity(popularity);
        setTrailerURL(trailerURL);
        setPrice(price);
    }

    protected Movie(Parcel in) {
        id = in.readString();
        name = in.readString();
        year = in.readString();
        description = in.readString();
        if (in.readByte() == 0) {
            popularity = null;
        } else {
            popularity = in.readInt();
        }
        trailerURL = in.readString();
        price = in.readFloat();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public void setId(String id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setYear(String year){
        this.year = year;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setPopularity(Integer popularity){
        this.popularity = popularity;
    }
    public void setImage(Bitmap image){
        this.image = image;
    }
    public void setTrailerURL(String trailerURL){
        this.trailerURL = trailerURL;
    }
    public void setPrice(Float price){this.price = price;}

    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getYear(){
        return year;
    }
    public String getDescription(){
        return description;
    }
    public Integer getPopularity(){
        return popularity;
    }

    public final long MB = 1024 * 1024;
    public Bitmap getImage(){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        String path = "movies/"+id+"/"+id+".jpg";
        StorageReference imageRef = storageRef.child(path);

        imageRef.getBytes(MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        });
        return image;
    }
    public String getTrailerURL(){
        return trailerURL;
    }
    public Float getPrice(){return price;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(year);
        parcel.writeString(description);
        if (popularity == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(popularity);
        }
        parcel.writeString(trailerURL);
        parcel.writeFloat(price);
    }
}
