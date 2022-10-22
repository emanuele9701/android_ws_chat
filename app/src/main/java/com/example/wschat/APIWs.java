package com.example.wschat;

import com.example.wschat.Dati.ServiceChat;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class APIWs {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.3/bot_whatsapp/api/whatsapp_chats_api_v3/public/index.php/api/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofit.create(ServiceChat.class);

        return retrofit;
    }


}
