package com.example.wschat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wschat.classes.ChannelEvent;
import com.example.wschat.classes.MyJSONParse;
import com.example.wschat.entity.ChatInfo;
import com.example.wschat.classes.DownloadImage;
import com.example.wschat.entity.Message;
import com.pusher.client.channel.SubscriptionEventListener;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityChat extends AppCompatActivity {

    Button btnSendMex;
    EditText messageCompose;
    ListView list_all_message;
    List<Message> all_message;
    private ChannelEvent event;
    static String EVENT_NEW_MESSAGE;
    int chatId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = getIntent();
        chatId = (Integer) i.getExtras().get("IdListChat");
        recuperoInfoChat(chatId);
        EVENT_NEW_MESSAGE = "App\\Events\\NewMessage_"+String.valueOf(chatId).toString();
        this.btnSendMex = (Button) findViewById(R.id.sendMessageButton);
        this.messageCompose = (EditText) findViewById(R.id.compose_message);
        //this.messageCompose.setText(R.string.writeMessage);
        list_all_message = (ListView) findViewById(R.id.allMessages);

        messageCompose.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(messageCompose.getText().toString().equals("Scrivi messaggio")) {
                   messageCompose.setText("");
                }
                return false;
            }
        });

        btnSendMex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messaggio = messageCompose.getText().toString();
                if(messageCompose.getText().toString().equals("Scrivi messaggio") || !(messaggio != null && messaggio.length() > 0)) {
                    Toast.makeText(getApplicationContext(),R.string.errorWriteMessage,Toast.LENGTH_SHORT).show();
                } else {
                    inviaMessaggio(messaggio);
                }
            }
        });
        ascoltoEvtNewMessage();
    }


    private void ascoltoEvtNewMessage() {
        if(MainActivity.pusher.getPuscher() != null) {
            if(!MainActivity.event.getChannel().equals("messages")) {
               event = new ChannelEvent("messages",MainActivity.pusher.getPuscher());
               event.subscribeChannel();
            } else {
                event = MainActivity.event;
            }
            event.getCanale().bind(EVENT_NEW_MESSAGE, new SubscriptionEventListener() {
                @Override
                public void onEvent(com.pusher.client.channel.PusherEvent event) {
                    // Reload chat
                    String a = event.getData();
                    Log.d(ActivityChat.class.toString(),"Stringa passata nell'evento "+a);

                    MyJSONParse mjson = new MyJSONParse(a);
                    HashMap<String, Integer> result = mjson.parseJSON();
                    if(result.containsKey("chatId") && result.get("chatId").intValue() > 0) {
                        recuperoInfoChat(result.get("chatId").intValue());
                        //list_all_message.setSelection(list_all_message.getAdapter().getCount() - 1);
                    }
                }
            });

        }
    }

    private void inviaMessaggio(String messaggio) {
        Call<HashMap<String,Object>> inviaMessaggio = MainActivity.sc.rispondi(this.chatId,messaggio);
        inviaMessaggio.enqueue(new Callback<HashMap<String, Object>>() {
            @Override
            public void onResponse(Call<HashMap<String, Object>> call, Response<HashMap<String, Object>> response) {
                HashMap<String,Object> jsonResponse = response.body();
                boolean esito = Boolean.valueOf(jsonResponse.get("esito").toString());
                if(esito) {
                    // Insert message in list message
                    boolean esitoUpd = aggiornaListMex(new Message(messageCompose.getText().toString(),1));
                    messageCompose.setText("");
                } else {
                    // Errore
                    Log.e(ActivityChat.class.toString(),"Errore inserimento messaggio nella lista messaggi");
                    Toast.makeText(getApplicationContext(),"Errore aggiornamento lista messaggi",Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, Object>> call, Throwable t) {
                Log.e(ActivityChat.class.toString(),"Errore invio messaggio");
                Toast.makeText(getApplicationContext(),"Errore invio messaggio",Toast.LENGTH_LONG);
            }
        });
    }

    private boolean aggiornaListMex(Message newMessage) {
        all_message.add(newMessage);
        list_all_message.setAdapter(new CustomAdapter(this,R.id.allMessages,all_message));
        list_all_message.setSelection(list_all_message.getAdapter().getCount() - 1);
        return true;
    }

    private void insertTestMex(List<Message> listMex)
    {
        all_message = listMex;
        CustomAdapter cAdp = new CustomAdapter(this,R.id.allMessages,listMex);
        list_all_message.setAdapter(cAdp);
        list_all_message.setSelection(list_all_message.getAdapter().getCount() - 1);

    }
    private void recuperoInfoChat(int chatId) {
        Log.d(ActivityChat.class.toString(),"Recupero info chat");
        Call<ChatInfo> infoChat = MainActivity.sc.infoChat(chatId);
        infoChat.enqueue(new Callback<ChatInfo>() {
            @Override
            public void onResponse(Call<ChatInfo> call, Response<ChatInfo> response) {
                ChatInfo cInfo = response.body();
                //Toast.makeText(getApplicationContext(),"Info recuperate",Toast.LENGTH_LONG).show();
                ImageView imageProfilo = (ImageView) findViewById(R.id.profiloImage);
                TextView infoNameChat = findViewById(R.id.infoNomeChat);
                infoNameChat.setText(String.valueOf(cInfo.getName()));
                if(cInfo.getUrlImage() != null) {
                    DownloadImage dImage = new DownloadImage(imageProfilo,cInfo.getUrlImage());
                    dImage.downloadImage();
                } else {
                    imageProfilo.setImageResource(R.drawable.blank_profile_icon);
                }
                insertTestMex(cInfo.getMessages());
            }

            @Override
            public void onFailure(Call<ChatInfo> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Info non recuperate",Toast.LENGTH_LONG).show();
            }
        });
    }

    private class CustomAdapter extends ArrayAdapter<Message> {
        List<Message> mex;
        public CustomAdapter(Context context, int textViewResourceId,
                             List <Message> objects) {
            super(context, textViewResourceId, objects);
            this.mex = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            TextView titolo;
            Message msg = getItem(position);
            if(msg.getFromMe() ==1 ) {
                convertView = inflater.inflate(R.layout.message_left, null);
                titolo = (TextView)convertView.findViewById(R.id.text_left_message);
            } else {
                convertView = inflater.inflate(R.layout.message_rigth, null);
                titolo = (TextView)convertView.findViewById(R.id.text_rigth_message);
            }

            titolo.setText(msg.getBody());
            return convertView;
        }



    }

}