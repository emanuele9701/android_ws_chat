package com.example.wschat.classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class MyJSONParse {

    String streamJson;

    public MyJSONParse(String streamJson){
        this.streamJson = streamJson;
    }


    public HashMap<String,Integer> parseJSON() {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String,Integer>>(){}.getType();
        HashMap<String,Integer> contactList = gson.fromJson(streamJson, type);
        System.out.println("Fatto");
        return contactList;
    }
}
