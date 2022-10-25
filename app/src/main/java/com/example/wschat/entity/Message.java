package com.example.wschat.entity;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("body")
    private String body;
    @SerializedName("fromMe")
    private int fromMe;

    @SerializedName("stream")
    private String stream;

    @SerializedName("nome_immagine")
    private String nomeImmagine;

    public String getBody() {
        return body;
    }

    public int getFromMe() {
        return fromMe;
    }

    public String getStream() {
        return stream;
    }

    public String getNomeImmagine() {
        return nomeImmagine;
    }

    public Message(String body, int fromMe) {
        this.body = body;
        this.fromMe = fromMe;
    }
}
