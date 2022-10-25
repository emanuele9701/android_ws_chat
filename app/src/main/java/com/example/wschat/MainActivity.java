package com.example.wschat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.wschat.Dati.ServiceChat;
import com.example.wschat.classes.ChannelEvent;
import com.example.wschat.classes.MyJSONParse;
import com.example.wschat.classes.PusherEvent;
import com.example.wschat.gestureCapture.RecyclerItemClickListener;
import com.example.wschat.placeholder.ChatItem;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.wschat.static_functions.MyStaticFunctions;
import com.pusher.client.channel.SubscriptionEventListener;

public class MainActivity extends AppCompatActivity {
    private final String CLUSTER_PUSHER = "eu";
    private final String APIKEY_PUSHER = "456ef6b739ac3e6f465b";
    private final String NEWEVENT = "App\\Events\\NewMessage";
    static ServiceChat sc;
    static TextView stringChatLoaded;
    static String textChatLoaded;
    static TextView infoAppLabel;
    static ChannelEvent event;
    static PusherEvent pusher;
    MyStaticFunctions msf;
    static boolean darkMode;
    AlertDialog.Builder bldSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.sc = APIWs.getClient().create(ServiceChat.class);
        msf = new MyStaticFunctions(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        darkMode = msf.checkDarkTheme();
        ping();
        loadComponents();
        getCountChats();
        setOnClickList();
        loadServices();

    }

    private void loadServices() {
        pusher = new PusherEvent(CLUSTER_PUSHER,APIKEY_PUSHER);
        if(pusher.getPuscher() != null) {
            event = new ChannelEvent("messages",pusher.getPuscher());
            event.subscribeChannel().bind(NEWEVENT, new SubscriptionEventListener() {
                @Override
                public void onEvent(com.pusher.client.channel.PusherEvent event) {
                    infoAppLabel.setText(R.string.nuovoMessaggioRicevuto);
                    infoAppLabel.setTextColor(getColor(R.color.mygreen));
                    HashMap<String,Integer> mjson = new MyJSONParse(event.getData()).parseJSON();
                }
            });
            infoAppLabel.setText(R.string.successPusherConnect);
            infoAppLabel.setTextColor(getColor(R.color.mygreen));
        } else {
            infoAppLabel.setText(R.string.errorPusherConnect);
            infoAppLabel.setTextColor(getColor(R.color.mygreen));
        }
    }

    private void setOnClickList() {
        RecyclerView listaRecycler = findViewById(R.id.list);
        listaRecycler.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), listaRecycler ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // do whatever
                        //Toast.makeText(getApplicationContext(),"Item click "+position,Toast.LENGTH_LONG).show();
                        Intent chatIntent = new Intent(getApplicationContext(),ActivityChat.class);
                        chatIntent.putExtra("IdListChat",getChatPositionList(position).WsChatId);
                        startActivity(chatIntent);
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        //Toast.makeText(getApplicationContext(),"Long Item click "+position,Toast.LENGTH_LONG).show();
                    }
                })
        );
    }

    private ChatItem getChatPositionList(int position) {
        ChatItem rt = new ChatItem(null,null,null,null,0);
        RecyclerView rv = findViewById(R.id.list);
        List<ChatItem> listItem = ((MyChatRecyclerViewAdapter) rv.getAdapter()).getListItem();
        rt = listItem.get(position);
        return rt;
    }

    private void loadComponents() {
        MainActivity.stringChatLoaded = findViewById(R.id.stringChatLoaded);
        this.stringChatLoaded.setText(R.string.chatloaded);
        MainActivity.textChatLoaded = MainActivity.stringChatLoaded.getText().toString();
        infoAppLabel = (TextView) findViewById(R.id.AppInfoLabel);
        ImageButton srcBtn = (ImageButton) findViewById(R.id.srcBtn);
        bldSearch = new AlertDialog.Builder(MainActivity.this);
        //AlertDialog.Builder bld = new AlertDialog.Builder(getApplicationContext());
        bldSearch.setTitle(R.string.search_chat);
        final EditText edt = new EditText(MainActivity.this);
        edt.setInputType(InputType.TYPE_CLASS_TEXT);
        bldSearch.setView(edt);
        bldSearch.setPositiveButton(R.string.search_btn_dialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(MainActivity.class.toString() + " - Dialog ricerca ","Ricerca di "+edt.getText().toString());
                dialog.cancel();
            }
        });
        bldSearch.setNegativeButton(R.string.reset_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        srcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bldSearch.show();
            }
        });
    }

    private void getCountChats() {
        Call<HashMap<String,Integer>> l = this.sc.countChats();
        l.enqueue(new Callback<HashMap<String,Integer>>() {
            @Override
            public void onResponse(Call<HashMap<String,Integer>> call, Response<HashMap<String,Integer>> response) {
                HashMap<String,Integer> result = response.body();
                MainActivity.stringChatLoaded.setText(MainActivity.textChatLoaded + ": "+String.valueOf(result.get("totale").intValue()));
            }

            @Override
            public void onFailure(Call<HashMap<String,Integer>> call, Throwable t) {
                System.out.println("getCountChats Error: "+t.getMessage());
            }
        });
    }

    private boolean ping() {
        this.sc.ping().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    String jsonResult = response.body();
                    ArrayList<String> list = new ArrayList<>();
                    JSONArray js = new  JSONArray(jsonResult);
                    Log.d(MainActivity.class+" --- Ping function ","Ok");
                    Toast.makeText(getApplicationContext(),js.get(0).toString(),Toast.LENGTH_SHORT).show();
                } catch ( JSONException jsEx) {
                    Log.e(MainActivity.class+" --- Ping function ",jsEx.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                System.out.println("Fallito per "+t.getMessage());
                Toast.makeText(getApplicationContext(),"No connection with service chat",Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }
}