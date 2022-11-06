package com.example.wschat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wschat.classes.DownloadImage;
import com.example.wschat.entity.ChatInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityChatInfo extends AppCompatActivity {
    int chatId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_info);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent i = getIntent();
        chatId = (Integer) i.getExtras().get("IdListChat");
        recuperoInfoChat();
    }


    private void recuperoInfoChat() {
        Call<ChatInfo> cInfoCall = MainActivity.sc.onlyInfoChat(chatId,0,true);

        cInfoCall.enqueue(new Callback<ChatInfo>() {
            @Override
            public void onResponse(Call<ChatInfo> call, Response<ChatInfo> response) {
                ChatInfo cInfo = response.body();
                TextView nome = findViewById(R.id.cInfo_nome);
                TextView bloccato = findViewById(R.id.cInfo_bloccata);
                TextView hasWhatsApp = findViewById(R.id.cInfo_hasWhatsApp);
                TextView telefono = findViewById(R.id.cInfo_numero);
                TextView statoWs = findViewById(R.id.cInfo_stato);
                ImageView imageProfilo = (ImageView) findViewById(R.id.cInfo_imageProfilo);


                nome.setText(cInfo.getName());
                telefono.setText(cInfo.getNumeroFormattato());

                statoWs.setText(cInfo.getStato());

                if(cInfo.getIsBlocked() == 1) {
                    bloccato.setText(R.string.yes_block);
                } else {
                    bloccato.setText(R.string.no_block);
                }
                if(cInfo.getHaveWhatsApp() == 1) {
                    hasWhatsApp.setText(R.string.yes_whatsapp);
                } else {
                    hasWhatsApp.setText(R.string.no_whatsapp);
                }

                if(cInfo.getUrlImage() != null) {
                    DownloadImage dImage = new DownloadImage(imageProfilo,cInfo.getUrlImage());
                    dImage.downloadImage();
                } else {
                    imageProfilo.setImageResource(R.drawable.blank_profile_icon);
                }
            }

            @Override
            public void onFailure(Call<ChatInfo> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Recupero info chat fallito",Toast.LENGTH_SHORT).show();
            }
        });
    }

}