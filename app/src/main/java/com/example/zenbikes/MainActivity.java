package com.example.zenbikes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button start_ride, pause_ride, stop_ride;
    BluetoothAdapter bluetoothAdapter;
    Intent bluetoothintent;
    public static final int requqestcoeforenable = 1;
    BluetoothDevice zenbikes = null;
    public  static String mac_address = unlockbike.macaddress;
    private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    connectstartthread connectstartthread;
    senddatathread senddatathread;


    //////handler switch case
    private final static int CONNECTED = 0;
    private final static int NOT_CONNECTED = 1;
    private static final int DATA_SENT=2;
    private static final int DATA_NOT_SENT=3;
    private static final int STOP_RIDE=4;
    private static final  int PAUSE_TO_RESUME=5;
    private static final int  RESUME_TO_PAUSE=6;

    public static  int smsg=112;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        start_ride = findViewById(R.id.start_ride);
        pause_ride = findViewById(R.id.pause_ride);
        stop_ride = findViewById(R.id.stop_ride);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

        pause_ride.setEnabled(false);
        stop_ride.setEnabled(false);

        if(!bluetoothAdapter.isEnabled()){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    turnon_bluetooth();


                }
            }, 100);
        }

        start_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startride();;


            }
        });

        pause_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pauseride();

            }
        });

        stop_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopride();
            }
        });


    }

    /////creating Handler

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what) {

                case CONNECTED:
                    ;
                    break;
                case NOT_CONNECTED:
                    Toast.makeText(MainActivity.this, "not connected", Toast.LENGTH_SHORT).show();
                    break;
                case DATA_SENT:
                    pause_ride.setEnabled(true);
                    stop_ride.setEnabled(true);
                    start_ride.setEnabled(false);
                    break;
                case DATA_NOT_SENT:
                    ////
                   break;
                case PAUSE_TO_RESUME:
                    pause_ride.setText("Resume");
                    smsg=114;
                    break;
                case RESUME_TO_PAUSE:
                    smsg=112;
                    pause_ride.setText("Pause");
                    break;
                case STOP_RIDE:

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent uintent=new Intent(MainActivity.this,unlockbike.class);
                            startActivity(uintent);
                            finish();
                        }
                    },1500);




            }


            return false;
        }
    });

    ////Getting Result of Bluetooth connection enabling


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == requqestcoeforenable) {

            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "BlueTooth Enabled", Toast.LENGTH_SHORT).show();
                connectstart();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    ///Turning on bluetooth
    public void turnon_bluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "BlueTooth not Found", Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {

                startActivityForResult(bluetoothintent, requqestcoeforenable);

            }
        }


    }

    //starting ride after connectig to bluetooth
    public void connectstart() {
        connectstartthread = new connectstartthread(bluetoothAdapter, zenbikes, handler, mac_address, uuid);
        connectstartthread.start();
    }

    public void startride(){
        int startmsg=115;
        senddatathread=new senddatathread(handler,startmsg,connectstartthread.getsocket());
        senddatathread.start();
    }

    public void pauseride(){


        senddatathread=new senddatathread(handler,smsg,connectstartthread.getsocket());
        senddatathread.start();
    }


    public void stopride(){

        int stopmsg=101;

        senddatathread=new senddatathread(handler,stopmsg,connectstartthread.getsocket());
        senddatathread.start();


    }
}

