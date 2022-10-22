package com.example.wschat.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatInfo {
    @SerializedName("name")
    private String name;

    @SerializedName("lastUpdate")
    private String lastUpdate;

    @SerializedName("numero_formattato")
    private String numeroFormattato;

    @SerializedName("url_image")
    private String urlImage;

    @SerializedName("idChat")
    private int idChat;

    @SerializedName("listMex")
    private List<Message> messages;


    public String getName() {
        return name;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getNumeroFormattato() {
        return numeroFormattato;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public int getIdChat() {
        return idChat;
    }
}
