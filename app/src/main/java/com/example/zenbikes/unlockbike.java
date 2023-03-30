package com.example.zenbikes;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class unlockbike extends AppCompatActivity {

    Button unlock;
    public  static  String macaddress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlockbike);
        getSupportActionBar().hide();
        unlock=findViewById(R.id.unlockbtn);
        int PERMISSION_ALL=1;
        String[] permission={"android.permission.CAMERA","android.permission.BLUETOOTH"

        };

        if (!haspermission(this,permission)){
            ActivityCompat.requestPermissions(this,permission,PERMISSION_ALL);
        }

        unlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               scanmode();

               // Toast.makeText(unlockbike.this, "getting executed", Toast.LENGTH_SHORT).show();



                //mac_scanner_activity();


            }
        });


    }


    public static  boolean haspermission(Context context,String... permission){
        if (context !=null && permission !=null){
            for(String permissions:permission){
                if (ActivityCompat.checkSelfPermission(context,permissions)!= PackageManager.PERMISSION_GRANTED){
                    return false;

                }
            }
        }

        return true;

    }



    public void scanmode(){
        ScanOptions options=new ScanOptions();
        options.setPrompt("Press Volume for Flash Light");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barlauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barlauncher=registerForActivityResult(new ScanContract(), result ->

    {

        Toast.makeText(this, "Unlocking", Toast.LENGTH_SHORT).show();
        macaddress=result.getContents();
        if(macaddress!=null) {

            Intent macintent = new Intent(unlockbike.this, MainActivity.class);
            startActivity(macintent);
            finish();
        }

    });

     /*public  void mac_scanner_activity(){
         Intent macintent =new Intent(this,MainActivity.class);
         startActivity(macintent);
         finish();


     }*/


}