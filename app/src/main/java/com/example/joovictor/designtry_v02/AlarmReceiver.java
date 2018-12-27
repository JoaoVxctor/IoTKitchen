package com.example.joovictor.designtry_v02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Gustavo on 08/09/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("receiver", "Estou no receiver");

        //busca extra string do intent
        String pegar_string = intent.getExtras().getString("extra");
        Log.e("qual a key?", pegar_string);


// cria a intent do service ringtone
        Intent service_intent = new Intent(context, RingtonePlayingService.class);

        // passar o extra string do cafe.class para o ringtone service
service_intent.putExtra("extra", pegar_string);

// inicia o service do ringtone
        context.startService(service_intent);
    }


}
