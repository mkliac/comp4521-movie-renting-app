package com.example.comp4521project.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Users implements Parcelable {
    private String username;
    private String password;
    private String nickname;
    private Float credits;

    public Users(String username, String password, String nickname, Float credits){
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.credits = credits;
    }
    public Users(){
        username = "dummyAccount";
        password = "";
        nickname = "Dummy01";
        credits = 0F;
    }

    protected Users(Parcel in) {
        username = in.readString();
        password = in.readString();
        nickname = in.readString();
        if (in.readByte() == 0) {
            credits = null;
        } else {
            credits = in.readFloat();
        }
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    public String getNickname(){
        return  nickname;
    }
    public Float getCredits(){
        return credits;
    }

    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public void setCredits(Float credits){
        this.credits = credits;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(nickname);
        if (credits == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeFloat(credits);
        }
    }
}
