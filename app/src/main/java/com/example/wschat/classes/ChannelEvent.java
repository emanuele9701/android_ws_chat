package com.example.wschat.classes;

import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;

public class ChannelEvent {

    private String channel;
    private Channel canale;
    private Pusher pusher;

    public ChannelEvent(String channel, Pusher pusher) {
        this.channel = channel;
        this.pusher = pusher;
    }

    public Channel subscribeChannel() {
        canale = pusher.subscribe(this.channel);
        return canale;
    }

    public Channel getCanale() {
        return canale;
    }

    public String getChannel() {
        return channel;
    }
}
