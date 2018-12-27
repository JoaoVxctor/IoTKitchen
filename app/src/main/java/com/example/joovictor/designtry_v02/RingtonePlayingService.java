package com.example.joovictor.designtry_v02;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class RingtonePlayingService extends Service {
    MediaPlayer media_song ;
     int startId;
boolean isRunning;


    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public IBinder onBind(Intent intent){
        return null;
    }



    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        // busca o valor dos extra strings
        String estado = intent.getExtras().getString("extra");
        Log.e("Ringtone extra é : ",estado);



        assert estado != null;

        // converte os extra strings da intent para start id , int .
        switch (estado) {
            case "ligado":
                startId = 1;
                break;
            case "desligado":
                startId = 0;

                break;
            default:
                startId = 0;
                break;
        }

        // ifs e elses

        //se a musica nao tiver tocando e o alarme estiver ligado, a musica deve tocar
        if(!this.isRunning && startId==1){
            // cria uma instancia do player de midia
            media_song = MediaPlayer.create(this, R.raw.xo);
            media_song.start();
            this.isRunning = true;
            this.startId=0;
            Log.e("nao tem musica", "voce quer que toque");

            //notificação
            //  setando service de notificação
            NotificationManager notif_manager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);

            // setar o intent para abrir a tela do despertador
            Intent intent_tela_despertador = new Intent(this.getApplicationContext(), DeviceControlActivity.class);


            PendingIntent intent_pendente_tela_despertador =  PendingIntent.getActivity(this, 0 ,
                    intent_tela_despertador,0 );

// parametros da notificação
            NotificationCompat.Builder notification= (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setContentTitle("O despertador está tocando")
                    .setContentIntent(intent_pendente_tela_despertador)
                    .setAutoCancel(true)
                    .setContentText("Clique aqui para ir ao aplicativo")
                    .setSmallIcon(R.drawable.alarm);




            notif_manager.notify(0,notification.build());





        }

//se a musica tiver tocando e o usuario apertar para desligar, a musica deve parar
        else if(this.isRunning && startId==0) {
            Log.e("tem musica", "voce quer que pare");
            media_song.stop();
            media_song.reset();

            // liga a cafeteira quando parar o alarme


            this.isRunning= false;
            this.startId=0;

        }

        // condiçoes caso o usuario aperte botoes de um jeito idiota , para ser a prova de bugs
        // se a musica nao tiver tocando e apertar para desligar, nao faz nada
        else if(!this.isRunning && startId==0) {
            Log.e("nao tem musica", "voce quer que pare");
            this.isRunning= false;
            this.startId=0;

        }
// se a musica tiver tocando e apertar para tocar , nao faz nada
        else if(this.isRunning && startId==1) {
            Log.e("tem musica", "voce quer que toque");
            this.isRunning= true;
            this.startId=0;

        }

        else{
            Log.e("deuruim", "Deu merda");

        }




        return START_NOT_STICKY;
    }


    public void onDestroy() {


        // Tell the user we stopped.
        Log.e("On destroy called", "Fim");
        super.onDestroy();
        this.isRunning=false;
    }






}

