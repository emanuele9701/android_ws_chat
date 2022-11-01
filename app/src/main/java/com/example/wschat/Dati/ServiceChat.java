package com.example.wschat.Dati;



import com.example.wschat.entity.Chat;
import com.example.wschat.entity.ChatInfo;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ServiceChat {
    @GET("chats/ping")
    Call<String> ping();

    @GET("chats/listChats")
    Call<List<Chat>> listChats();

    @GET("chats/allCountChat")
    Call<HashMap<String,Integer>> countChats();

    @GET("chats/getChatsInfo/{chatId}/{limit}")
    Call<ChatInfo> infoChat(@Path("chatId") int chatId,@Path("limit") int limit);

    @GET("chats/getChatsInfo/{chatId}/{limit}/{onlyInfoChat}")
    Call<ChatInfo> onlyInfoChat(@Path("chatId") int chatId,@Path("limit") int limit,@Path("onlyInfoChat") boolean onlyInfoChat);

    @GET("chats/search/{what}")
    Call<List<Chat>> searchChat(@Path("what") String what);

    @FormUrlEncoded
    @POST("chats/messages/responseMessage")
    Call<HashMap<String,Object>> rispondi(@Field("chat_id") int chatId, @Field("message") String messaggio);
}
