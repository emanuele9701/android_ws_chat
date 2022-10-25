package com.example.wschat.classes;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

public class PusherEvent {
    Pusher pusher;
    public PusherEvent(String cluster,String apiKey) {
        PusherOptions options = new PusherOptions();
        options.setCluster(cluster);

        pusher = new Pusher(apiKey, options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                System.out.println("State changed to " + change.getCurrentState() +
                        " from " + change.getPreviousState());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                System.out.println("There was a problem connecting!");
            }
        }, ConnectionState.ALL);
    }

    public Pusher getPuscher() {
        return pusher;
    }

}
