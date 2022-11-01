package com.example.wschat.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Message {

    @SerializedName("body")
    private String body;
    @SerializedName("fromMe")
    private int fromMe;

    @SerializedName("media")
    private MediaMessage mediaMessage;

    @SerializedName("timestamp_message")
    private String dateMessage;

    @SerializedName("mediaFile")
    private int mediaId;

    @SerializedName("mittente")
    private String mittente;

    public String getBody() {
        return body;
    }

    public int getFromMe() {
        return fromMe;
    }

    public MediaMessage getMediaMessage() {
        return mediaMessage;
    }

    public String getDateMessage() {
        return dateMessage;
    }

    public int getMediaId() {
        return mediaId;
    }

    public String getMittente() {
        return mittente;
    }

    public Message(String body, int fromMe) {
        this.body = body;
        this.fromMe = fromMe;
    }
}
