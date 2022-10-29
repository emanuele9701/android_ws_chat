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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.wschat.Dati.ServiceChat;
import com.example.wschat.classes.ChannelEvent;
import com.example.wschat.classes.MyJSONParse;
import com.example.wschat.classes.PusherEvent;
import com.example.wschat.entity.Chat;
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
    static ChannelEvent event;
    static PusherEvent pusher;
    MyStaticFunctions msf;
    Call<List<Chat>> clListChat;
    static boolean darkMode;
    static AlertDialog.Builder bldSearch;
    static List<ChatItem> oldChat;
    private Button resetSearch;
    static RecyclerView mainRecyclerView;
    ImageButton refreshChat;
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
        clListChat = sc.listChats();
    }

    private void loadServices() {
        pusher = new PusherEvent(CLUSTER_PUSHER,APIKEY_PUSHER);
        if(pusher.getPuscher() != null) {
            event = new ChannelEvent("messages",pusher.getPuscher());
            event.subscribeChannel().bind(NEWEVENT, new SubscriptionEventListener() {
                @Override
                public void onEvent(com.pusher.client.channel.PusherEvent event) {
                    reloadListChats();
                }
            });
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

    private void reloadListChats() {
        clListChat.enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
                List<Chat> listaChat = response.body();
                List<ChatItem> listaItemChat = new ArrayList<>();

                for (Chat c :
                        listaChat) {
                    listaItemChat.add(new ChatItem(null,c.getName(),c.getLastMessage(),c.getData(),c.getChatId()));
                }
                if(MainActivity.oldChat == null) {
                    MainActivity.oldChat = listaItemChat;
                }
                mainRecyclerView.setAdapter(new MyChatRecyclerViewAdapter(listaItemChat));
                if(refreshChat.getVisibility() == View.INVISIBLE) {
                    refreshChat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) {
                System.out.println("GetListChat Error: "+t.getMessage());
            }
        });
    }

    private ChatItem getChatPositionList(int position) {
        ChatItem rt = new ChatItem(null,null,null,null,0);
        RecyclerView rv = findViewById(R.id.list);
        List<ChatItem> listItem = ((MyChatRecyclerViewAdapter) rv.getAdapter()).getListItem();
        rt = listItem.get(position);
        return rt;
    }

    private List<ChatItem> getChatFromString(String ricerca) {
        List<ChatItem> listItemSearched = new ArrayList<>();
        RecyclerView rv = findViewById(R.id.list);
        List<ChatItem> listItem = ((MyChatRecyclerViewAdapter) rv.getAdapter()).getListItem();

        for (ChatItem c :
                listItem) {
            if(c.content.contains(ricerca)) {
                listItemSearched.add(c);
            }
        }

        // Controllo anche in db
        Call<List<Chat>> callSearchChat = MainActivity.sc.searchChat(ricerca);
        callSearchChat.enqueue(new Callback<List<Chat>>() {
            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
                List<Chat> listaResponse = response.body();
                for (Chat cList :
                    listaResponse) {
                   if(cList.getName().toLowerCase().contains(ricerca.toLowerCase())) {
                        ChatItem c = new ChatItem(null,cList.getName(),cList.getLastMessage(),cList.getData(),cList.getChatId());
                        if(!listItemSearched.contains(c)) {
                            listItemSearched.add(c);
                        }
                   }
                }
                ChatFragment.recyclerView.setAdapter(new MyChatRecyclerViewAdapter(listItemSearched));
                resetSearch.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) {
                Log.e(MainActivity.class.toString(),"Errore recupero chat ricercata "+t.getMessage());
            }
        });

        return listItemSearched;
    }

    private void loadComponents() {
        ImageButton srcBtn = (ImageButton) findViewById(R.id.srcBtn);
        refreshChat = (ImageButton) findViewById(R.id.btnRefreshChat);
        resetSearch = (Button) findViewById(R.id.resetSearch);
        //AlertDialog.Builder bld = new AlertDialog.Builder(getApplicationContext());
         srcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bldSearch == null) {
                    bldSearch = new AlertDialog.Builder(MainActivity.this);
                }
                final EditText edt_ricerca = new EditText(MainActivity.this);
                edt_ricerca.setInputType(InputType.TYPE_CLASS_TEXT);
                bldSearch.setView(edt_ricerca);
                bldSearch.setTitle(R.string.search_chat);

                bldSearch.setPositiveButton(R.string.search_btn_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String valoreRicercato = edt_ricerca.getText().toString();
                        getChatFromString(valoreRicercato);
                        dialog.cancel();
                    }
                });
                bldSearch.setNegativeButton(R.string.reset_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                bldSearch.show();
            }
        });

        resetSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldChat != null) {
                    ChatFragment.recyclerView.setAdapter(new MyChatRecyclerViewAdapter(oldChat));
                    resetSearch.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(),"Verificato un problema nel ripristino della lista",Toast.LENGTH_LONG).show();
                }
            }
        });

        refreshChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.class.toString(),"Refresh button activeted");
                reloadListChats();
                refreshChat.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void getCountChats() {
        Call<HashMap<String,Integer>> l = this.sc.countChats();
        l.enqueue(new Callback<HashMap<String,Integer>>() {
            @Override
            public void onResponse(Call<HashMap<String,Integer>> call, Response<HashMap<String,Integer>> response) {
                HashMap<String,Integer> result = response.body();
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
                    //Toast.makeText(getApplicationContext(),js.get(0).toString(),Toast.LENGTH_SHORT).show();
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