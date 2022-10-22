package com.example.wschat.entity;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("body")
    private String body;
    @SerializedName("fromMe")
    private int fromMe;

    public String getBody() {
        return body;
    }

    public int getFromMe() {
        return fromMe;
    }


    public Message(String body, int fromMe) {
        this.body = body;
        this.fromMe = fromMe;
    }
}
