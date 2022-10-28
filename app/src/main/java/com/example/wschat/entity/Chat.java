package com.example.wschat.entity;

import com.google.gson.annotations.SerializedName;

public class Chat {
    @SerializedName("timestamp_message")
    private String data;
    @SerializedName("body")
    private String lastMessage;
    @SerializedName("hasNewMex")
    private int newMessage;
    @SerializedName("id")
    private int chatId;
    @SerializedName("name")
    private String name;
    @SerializedName("url_image")
    private String urlImage;


    public String getData() {
        return data;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getNewMessage() {
        return newMessage;
    }

    public int getChatId() {
        return chatId;
    }

    public String getName() {
        return name;
    }

    public String getUrlImage() {
        return urlImage;
    }
}