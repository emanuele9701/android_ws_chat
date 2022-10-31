package com.example.wschat.entity;

import com.google.gson.annotations.SerializedName;

public class MediaMessage {

    private String localPathSaved;

    @SerializedName("id")
    private int id;

    @SerializedName("nome_immagine")
    private String nome;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("stream")
    private String stream;


    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public String getStream() {
        return stream;
    }

    public int getId() {
        return id;
    }

    public String getLocalPathSaved() {
        return localPathSaved;
    }

    public void setLocalPathSaved(String localPathSaved) {
        this.localPathSaved = localPathSaved;
    }
}
