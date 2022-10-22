package com.example.wschat.placeholder;

import android.widget.ImageView;

public class ChatItem {
    public ImageView image;
    public String content;
    public String dateTime;
    public String body;
    public int WsChatId;
    public ChatItem(ImageView image, String content, String body,String dateTime, int WsChatId) {
        this.image = image;
        this.content = content;
        this.body = body;
        this.dateTime = dateTime;
        this.WsChatId = WsChatId;
    }

    @Override
    public String toString() {
        return content;
    }
}
