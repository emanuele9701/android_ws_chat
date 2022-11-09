package com.example.wschat;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wschat.classes.ChannelEvent;
import com.example.wschat.classes.MyJSONParse;
import com.example.wschat.entity.ChatInfo;
import com.example.wschat.classes.DownloadImage;
import com.example.wschat.entity.Message;
import com.pusher.client.channel.SubscriptionEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    static final int ALTEZZA_IMG_MEX = 300;
    static final int LARGHEZZA_IMG_MEX = 300;
    static final int ALTEZZA_AUD = 150;
    static final int LARGHEZZA_AUD = 150;
    static final int LIMIT_MEX = 35;
    static final int ALTEZZA_RD_IC = 45;
    static final int LARGHEZZA_RD_IC = 45;
    private ChannelEvent event;
    static String EVENT_NEW_MESSAGE;
    MediaPlayer mediaPlayer;
    String audioCurrentPlaying;

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
        TableLayout tbHeaderChat = (TableLayout) findViewById(R.id.tableHeaderChat);
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
        Toast.makeText(getApplicationContext(),"Sono visualizzati soltanto gli ultimi 35 messaggi",Toast.LENGTH_LONG).show();

        tbHeaderChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(getApplicationContext(),ActivityChatInfo.class);
                chatIntent.putExtra("IdListChat",chatId);
                startActivity(chatIntent);
            }
        });
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
        Call<ChatInfo> infoChat = MainActivity.sc.infoChat(chatId,LIMIT_MEX);
        infoChat.enqueue(new Callback<ChatInfo>() {
            @Override
            public void onResponse(Call<ChatInfo> call, Response<ChatInfo> response) {
                Log.d(ActivityChat.class.toString(),"Recupero info chat: OK");
                ChatInfo cInfo = response.body();

                ImageView imageProfilo = (ImageView) findViewById(R.id.profiloImage);
                TextView infoNameChat = findViewById(R.id.infoNomeChat);
                TextView infoNumeroChat = findViewById(R.id.infoNumeroChat);

                infoNumeroChat.setText(cInfo.getNumeroFormattato());
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
                Log.e(this.toString(),"Info non recuperate per "+t.getMessage());
            }
        });
    }



    private class CustomAdapter extends ArrayAdapter<Message> {
        List<Message> mex;
        Context ctx;
        public CustomAdapter(Context context, int textViewResourceId,
                             List <Message> objects) {
            super(context, textViewResourceId, objects);
            this.mex = objects;
            ctx = context;
        }


        private String saveAudio(Message msg){
            String pathToSave = getFilesDir().getPath() + "/" + msg.getMediaMessage().getNome();
            File file = new File(pathToSave);
            try {
                if(!file.exists()) {
                    file.createNewFile();
                } else {
                    file.delete();
                    file.createNewFile();
                }
                FileOutputStream fwrite = new FileOutputStream(pathToSave);
                fwrite.write(java.util.Base64.getDecoder().decode(msg.getMediaMessage().getStream()));
            } catch (IOException ex) {
                Log.e(ActivityChat.class.toString(),"Creazione audio file block for: "+ex.getMessage());
            }

            return pathToSave;
        }

        private void playAudio(String pathAudio) {
            try {
                File f = new File(pathAudio);
                if(!f.exists()) {
                    Toast.makeText(getApplicationContext(),"Problema in riproduzione audio",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mediaPlayer != null) {
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.reset();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(pathAudio);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException ex) {
                Log.e(this.toString(),"Errore in riproduzione audio "+ex.getMessage());
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            Message msg = getItem(position);
            if(msg.getFromMe() ==1 ) {
                // Messaggio inviato
                convertView = inflater.inflate(R.layout.message_left, null);
                printMeMessage(convertView,msg);
            } else {
                convertView = inflater.inflate(R.layout.message_rigth, null);
                TextView labelFrom = (TextView) convertView.findViewById(R.id.labelFrom);

                labelFrom.setText(msg.getMittente());
                printOtherMessage(convertView,msg);
                // Messaggio ricevuto
            }
            return convertView;
        }


        private Bitmap convertStringToImage(String base64) {
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            // Bitmap Image
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return bitmap;
        }

        private File storeImageBitmpa(Bitmap image,String name) {
             String storePath = getFilesDir().getPath() + "/"+name;
             File pictureFile = new File(storePath);
            if (pictureFile == null) {
                Log.d(TAG,
                        "Error creating media file, check storage permissions: ");// e.getMessage());
                return null;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                image.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
            return pictureFile;
        }

        private void printMeMessage(View cw,Message msg) {
            ImageView imgMexLeft, img_spunta_read;

            ImageButton btnLeftAudio;

            TextView titolo = (TextView)cw.findViewById(R.id.text_left_message);
            TextView dateMessage = (TextView) cw.findViewById(R.id.date_left_mex);
            imgMexLeft = (ImageView) cw.findViewById(R.id.imageMsgLeft);
            btnLeftAudio = (ImageButton) cw.findViewById(R.id.btnSoundLeft);
            img_spunta_read = (ImageView) cw.findViewById(R.id.img_spunta_read_left);
            img_spunta_read.getLayoutParams().height = ALTEZZA_RD_IC;
            img_spunta_read.getLayoutParams().width = LARGHEZZA_RD_IC;

            if(btnLeftAudio != null) {
                btnLeftAudio.getLayoutParams().width = 0;
                btnLeftAudio.getLayoutParams().height = 0;
                btnLeftAudio.setVisibility(View.INVISIBLE);
            }
            if(msg.getMediaMessage() == null) {
                imgMexLeft.setVisibility(View.INVISIBLE);
                imgMexLeft.getLayoutParams().height = 0;
                imgMexLeft.getLayoutParams().width = 0;
            } else if (msg.getMediaMessage() != null && msg.getMediaMessage().getTipo().equals("image")) {
                Bitmap img = convertStringToImage(msg.getMediaMessage().getStream());
                imgMexLeft.setVisibility(View.VISIBLE);
                imgMexLeft.setImageBitmap(img);
                imgMexLeft.getLayoutParams().height = ALTEZZA_IMG_MEX;
                imgMexLeft.getLayoutParams().width = LARGHEZZA_IMG_MEX;
                imgMexLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File f = storeImageBitmpa(img,msg.getMediaMessage().getNome());
                        if(f.exists()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW)//
                                    .setDataAndType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                                                    FileProvider.getUriForFile(getApplicationContext(),getPackageName() + ".provider", f) : Uri.fromFile(f),
                                            "image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        }
                    }
                });
            } else if (msg.getMediaMessage() != null && msg.getMediaMessage().getTipo().equals("audio")) {
                imgMexLeft.getLayoutParams().height = 0;
                imgMexLeft.getLayoutParams().width = 0;
                btnLeftAudio.getLayoutParams().width = ALTEZZA_AUD;
                btnLeftAudio.getLayoutParams().height = LARGHEZZA_AUD;
                btnLeftAudio.setVisibility(View.VISIBLE);
                btnLeftAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msg.getMediaMessage().setLocalPathSaved(saveAudio(msg));
                        if(audioCurrentPlaying == null) {
                            audioCurrentPlaying = msg.getMediaMessage().getLocalPathSaved();
                        } else if(!audioCurrentPlaying.equals(msg.getMediaMessage().getLocalPathSaved())) {
                            File fcurrent = new File(audioCurrentPlaying);
                            if(fcurrent.exists()) {
                                fcurrent.delete();
                            }
                        }
                        playAudio(msg.getMediaMessage().getLocalPathSaved());
                    }
                });
            }
            titolo.setText(msg.getBody());
            dateMessage.setText(msg.getDateMessage());
            if(msg.getRead() == 1) {
                img_spunta_read.setImageResource(R.drawable.vicon_green);
            } else {
                img_spunta_read.setImageResource(R.drawable.vicon_white);
            }
        }

        private void printOtherMessage(View cw,Message msg) {
            ImageButton btnRigthAudio;
            ImageView imgMexRigth;
            TextView titolo = (TextView)cw.findViewById(R.id.text_rigth_message);
            TextView dateMessage = (TextView) cw.findViewById(R.id.dateRigthMessage);


            imgMexRigth = (ImageView) cw.findViewById(R.id.imageMsgRigth);
            btnRigthAudio = (ImageButton) cw.findViewById(R.id.btnSoundRigth);

            if(btnRigthAudio != null) {
                btnRigthAudio.getLayoutParams().width = 0;
                btnRigthAudio.getLayoutParams().height = 0;
                btnRigthAudio.setVisibility(View.INVISIBLE);
            }
            if(msg.getMediaMessage() == null) {
                imgMexRigth.setVisibility(View.INVISIBLE);
                imgMexRigth.getLayoutParams().height = 0;
                imgMexRigth.getLayoutParams().width = 0;
            }else if (msg.getMediaMessage() != null && msg.getMediaMessage().getTipo().equals("image")) {
                Bitmap img = convertStringToImage(msg.getMediaMessage().getStream());
                imgMexRigth.setVisibility(View.VISIBLE);
                imgMexRigth.setImageBitmap(img);
                imgMexRigth.getLayoutParams().height = ALTEZZA_IMG_MEX;
                imgMexRigth.getLayoutParams().width = LARGHEZZA_IMG_MEX;
                imgMexRigth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        File f = storeImageBitmpa(img,msg.getMediaMessage().getNome());
                        if(f.exists()) {
                            Intent intent = new Intent(Intent.ACTION_VIEW)//
                                    .setDataAndType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                                                    FileProvider.getUriForFile(getApplicationContext(),getPackageName() + ".provider", f) : Uri.fromFile(f),
                                            "image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        }
                    }
                });
            } else if (msg.getMediaMessage() != null && msg.getMediaMessage().getTipo().equals("audio")) {
                // To do
                imgMexRigth.getLayoutParams().height = 0;
                imgMexRigth.getLayoutParams().width = 0;
                btnRigthAudio.getLayoutParams().width = ALTEZZA_AUD;
                btnRigthAudio.getLayoutParams().height = LARGHEZZA_AUD;
                btnRigthAudio.setVisibility(View.VISIBLE);
                btnRigthAudio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        msg.getMediaMessage().setLocalPathSaved(saveAudio(msg));
                        if(audioCurrentPlaying == null) {
                            audioCurrentPlaying = msg.getMediaMessage().getLocalPathSaved();
                        } else if(!audioCurrentPlaying.equals(msg.getMediaMessage().getLocalPathSaved())) {
                            File fcurrent = new File(audioCurrentPlaying);
                            if(fcurrent.exists()) {
                                fcurrent.delete();
                            }
                        }
                        playAudio(msg.getMediaMessage().getLocalPathSaved());
                    }
                });
            }
            titolo.setText(msg.getBody());
            dateMessage.setText(msg.getDateMessage());
        }

    }

}