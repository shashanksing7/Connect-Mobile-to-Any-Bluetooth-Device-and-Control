package com.example.zenbikes;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class connectstartthread extends Thread{


    private static  BluetoothSocket mmSocket;
    private static final String TAG = "connectthread";
    public boolean AASURANCE_VARIABLE=false;
    public static Handler handler;
    private final  static int CONNECTED=0;
    private final static int NOT_CONNECTED=1;


    @SuppressLint("MissingPermission")
    public connectstartthread(BluetoothAdapter adapter, BluetoothDevice device, Handler handler, String address, UUID uuid){


        this.handler=handler; //passing handler

        device=adapter.getRemoteDevice(address);  //creating  device

        BluetoothSocket temp_socket=null; //creating a temporary bluetooth socket

        ////connecting the socket
        try  {

            temp_socket = device.createRfcommSocketToServiceRecord(uuid);  ///if connection sucessful
            Log.d(TAG, "connectThread: socket created");
            AASURANCE_VARIABLE=true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "connectThread: creating socket failed"); ///if connection fails
        }

        mmSocket=temp_socket; ///socket tarnsferred

    }

    @SuppressLint("MissingPermission")
    @Override
    public void run() {
        if (mmSocket!=null&&AASURANCE_VARIABLE==true){

            int counter=0;

            do {


                try {
                    mmSocket.connect();
                    Log.d(TAG, "run: connection sucessful");

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: connection failed");
                }

            }while (!mmSocket.isConnected()&&counter==5);

            if (mmSocket.isConnected()){
                Message msg=Message.obtain();
                msg.what=CONNECTED;
                handler.sendMessage(msg);
            }
            else{
                Log.d(TAG, "run:unable to connect ");
                Message msg=Message.obtain();
                msg.what=NOT_CONNECTED;
                handler.sendMessage(msg);
            }


        }
    }






    public BluetoothSocket getsocket(){
        return mmSocket;
    }
}
