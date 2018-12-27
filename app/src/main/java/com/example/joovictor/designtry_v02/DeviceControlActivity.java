package com.example.joovictor.designtry_v02;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.TimePicker;
import android.widget.ToggleButton;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;



/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    public final static String TAG = DeviceControlActivity.class.getSimpleName();

    public  Switch Cafeteira;
    Switch Fogao ;
    Switch Geladeira ;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView Despensa_Status;
    private TextView mDataField;
    public String mDeviceName;
    public String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    public BluetoothLeService mBluetoothLeService;
    public ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    public boolean mConnected = false;
    public BluetoothGattCharacteristic mNotifyCharacteristic;
    AlarmManager alarm_manager;
    String statusdesperta = "" ;
    TextView update_text;
    TimePicker alarm_timepicker;
    Context context;
     long endTime = System.currentTimeMillis();
      long startTime = System.currentTimeMillis();
    PendingIntent intent_pendente;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    public BluetoothGattCharacteristic bluetoothGattCharacteristicHM_10;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {


        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();


            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

               Despensa_Status = (TextView)findViewById(R.id.Despensa_Status);

                //Echo back received data, with something inserted
                byte[] rxBytes = bluetoothGattCharacteristicHM_10.getValue();
                byte[] insertSomething = {(byte) '\n'};
                byte[] txBytes = new byte[insertSomething.length + rxBytes.length];
                System.arraycopy(insertSomething, 0, txBytes, 0, insertSomething.length);
                System.arraycopy(rxBytes, 0, txBytes, insertSomething.length, rxBytes.length);
                bluetoothGattCharacteristicHM_10.setValue(txBytes);
                mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicHM_10);
                mBluetoothLeService.setCharacteristicNotification(bluetoothGattCharacteristicHM_10, true);

                byte[] vaisefoder = bluetoothGattCharacteristicHM_10.getValue();

                int manos = vaisefoder[1];


                System.out.println(manos);

                if(manos==54){
                    String update_despensa = new String("Objeto não detectado");
                    Despensa_Status.setText(update_despensa);
                }
                else if(manos==55) {

                    String update_despensa = new String("Objeto Detectado");
                    Despensa_Status.setText(update_despensa);
                }









            }
        }
    };



    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
            };

    private void clearUI() {
        // mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
//        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final DBHandler db = new DBHandler(this);
        db.addTomada(new Tomada( 0 ,2,1, 550));
        db.addHorario(new Horario( 1 ,1,1,"18/08/1990",0));



        setContentView(R.layout.gatt_services_characteristics);
       // someTudo();


        // inicio despertador things
        this.context = this;
        // inicializar alarm manager



        alarm_manager = (AlarmManager)getSystemService(ALARM_SERVICE);

        // inicializar o time picker
        alarm_timepicker  = (TimePicker)findViewById(R.id.timePicker);

        // inicializar o update text
        update_text = (TextView)findViewById(R.id.update_text);

        // criar a instancia de um calendario
        final Calendar calendar = Calendar.getInstance();

        //Cria um intent para o AlarmReceiver
        final Intent meuintent = new Intent(this.context,AlarmReceiver.class);




        Button alarm_on = (Button)findViewById(R.id.alarm_on);
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View xd) {



                // setando a instancia do calendario que diz para ele pegar a hora e os minutos do time picker
                calendar.set(Calendar.HOUR_OF_DAY,alarm_timepicker.getHour());
                calendar.set(Calendar.MINUTE,alarm_timepicker.getMinute());

                // pegar a string do valor das horas em minutos
                int hora = alarm_timepicker.getHour();
                int minuto = alarm_timepicker.getMinute();


                //converter o valor inteiro para string
                String hora_string = String.valueOf(hora);
                String minuto_string = String.valueOf(minuto);
                if(minuto < 10) {
                    minuto_string = "0" + String.valueOf(minuto);
                }




// metodo que  muda o txto da caixa update text
                set_alarm_text("Alarme definido para  " + hora_string+":"+minuto_string);


                // colocar   extra string no meuintent
                meuintent.putExtra("extra","ligado");

                // criar um intent pendente que atrasa o intent até o horario setado no calendario
                intent_pendente = PendingIntent.getBroadcast(DeviceControlActivity.this, 0,
                        meuintent, PendingIntent.FLAG_UPDATE_CURRENT);

                // definir o alarm manager
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),intent_pendente);
            }
        });




        Button alarm_off = (Button)findViewById(R.id.alarm_off);

        //criar onclick pra desligar o alarme
        alarm_off.setOnClickListener(new View.OnClickListener(){
@Override
public void onClick(View vv){
   int hora =  calendar.get(Calendar.HOUR_OF_DAY);
    String horabd = String.valueOf(hora);
    int minuto =  calendar.get(Calendar.MINUTE);
    String minutobd = String.valueOf(minuto);
    db.addHorario(new Horario(3,3,3,horabd+":"+minutobd,0 ));
        set_alarm_text("Alarme desligado!");
        System.out.println(horabd);
    System.out.println(minutobd);

    String onCafeteiraDesperta = new String("1");
    bluetoothGattCharacteristicHM_10.setValue(onCafeteiraDesperta);
    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicHM_10);
    mBluetoothLeService.setCharacteristicNotification(bluetoothGattCharacteristicHM_10,true);


        //Cancela o alarme
        alarm_manager.cancel(intent_pendente);

        // colocar   extra string no meuintent
        meuintent.putExtra("extra","desligado");

        //parar o despertador
        sendBroadcast(meuintent);


        }
        });








        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /// switch cafeteira
       Switch Cafeteira = (Switch) findViewById(R.id.Cafeteira);


        Cafeteira.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    // The toggle is enabled
                    String onCafeteira = new String("1");
                    bluetoothGattCharacteristicHM_10.setValue(onCafeteira);
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicHM_10);
                    mBluetoothLeService.setCharacteristicNotification(bluetoothGattCharacteristicHM_10, true);
                    startTime = System.currentTimeMillis();

                } else {
                    // The toggle is disabled

                    String offCafeteira = new String("0");
                    bluetoothGattCharacteristicHM_10.setValue(offCafeteira);
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicHM_10);
                    mBluetoothLeService.setCharacteristicNotification(bluetoothGattCharacteristicHM_10, true);
                    endTime = System.currentTimeMillis(); //calculo de kwh
                    double segundos = (endTime - startTime) / 1000;
                    double totalsegundos=  db.getSegundos(1);

                    db.updateHorario(new Horario(1,0,0,"" , totalsegundos+segundos));


                    int potencia =  db.getPotencia(1) ;
                    double consumo = (potencia*totalsegundos)/3600000;


                    System.out.println("seu consumo total foi de " +consumo+ "KW/H" );

                }


            }

        }

        );



        Switch Fogao = (Switch) findViewById(R.id.Fogao);
        Fogao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                        String onFogao = new String("3");
                        bluetoothGattCharacteristicHM_10.setValue(onFogao);
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicHM_10);
                        mBluetoothLeService.setCharacteristicNotification(bluetoothGattCharacteristicHM_10, true);


                } else {
                    String offFogao = new String("2");
                    bluetoothGattCharacteristicHM_10.setValue(offFogao);
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicHM_10);
                    mBluetoothLeService.setCharacteristicNotification(bluetoothGattCharacteristicHM_10, true);



                }




            }
                });

        Switch Geladeira= (Switch) findViewById(R.id.Geladeira);
        Geladeira.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String onGeladeira = new String("5");
                    bluetoothGattCharacteristicHM_10.setValue(onGeladeira);
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicHM_10);
                    mBluetoothLeService.setCharacteristicNotification(bluetoothGattCharacteristicHM_10, true);
                } else {
                    String offGeladeira = new String("4");
                    bluetoothGattCharacteristicHM_10.setValue(offGeladeira);
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicHM_10);
                    mBluetoothLeService.setCharacteristicNotification(bluetoothGattCharacteristicHM_10, true);


                }




            }
        });










        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);


        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
      /*   mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
       */ mConnectionState = (TextView) findViewById(R.id.connection_state);
        // mDataField = (TextView) findViewById(R.id.data_value);

        //   getActionBar().setTitle(mDeviceName);
        //   getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        String bob = mDeviceAddress;
        db.addPessoa(new Pessoa( 0 ,2, "Gustavo", bob ));

         String teste =  db.getBTADS(1);
         System.out.println(teste);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }
    private void set_alarm_text(String output) {
        update_text.setText(output);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    public void displayData(String data) {
        if (data != null) {
//            mDataField.setText(data);
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    public void displayGattServices(List<BluetoothGattService> gattServices) {

        UUID UUID_HM_10 =
                UUID.fromString(SampleGattAttributes.HM_10);

        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);

                //Check if it is "HM_10"
                if(uuid.equals(SampleGattAttributes.HM_10)){
                    bluetoothGattCharacteristicHM_10 = gattService.getCharacteristic(UUID_HM_10);

                }
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        //  mGattServicesList.setAdapter(gattServiceAdapter);
    }

    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void someTudo(){
        View mLogo = findViewById(R.id.Logo);
        View mBluetooth = findViewById(R.id.Bluetooth);
        View mDespensa = findViewById(R.id.Despensa);
        View mDespertador = findViewById(R.id.Despertador);

        mLogo.setVisibility(mLogo.INVISIBLE);
        mBluetooth.setVisibility(mBluetooth.INVISIBLE);
        mDespensa.setVisibility(mDespensa.INVISIBLE);
        mDespertador.setVisibility(mDespertador.INVISIBLE);


    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_alarm:
                    someTudo();
                    View b = findViewById(R.id.Despertador);
                    b.setVisibility(b.VISIBLE);
                    return true;
                case R.id.navigation_bluetooth:
                    someTudo();
                    View c = findViewById(R.id.Bluetooth);
                    c.setVisibility(c.VISIBLE);
                    return true;


                case R.id.navigation_basket:
                    someTudo();
                    View d = findViewById(R.id.Despensa);
                    d.setVisibility(d.VISIBLE);
                    return true;

            }
            return false;
        }

    };

}
