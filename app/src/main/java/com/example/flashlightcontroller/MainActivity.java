package com.example.flashlightcontroller;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private boolean supportFlashLight;
    private Button turnOn, turnOff,incDelay,decDelay;
    private ToggleButton blinkButton;
    CameraManager cameraManager;
    String CameraID;
    Runnable runnable;
    Handler handler;
    int delay = 1000;
    boolean flashOn;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        // Check that flashlight is supported or not
        supportFlashLight = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        // CameraManager
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            CameraID = cameraManager.getCameraIdList()[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Current Delay : " + delay);
                try {
                    if (!flashOn){
                        cameraManager.setTorchMode(CameraID,true);
                        flashOn = true;
                    }else{
                        cameraManager.setTorchMode(CameraID,false);
                        flashOn = false;
                    }
                    handler.postDelayed(this,delay);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        handler = new Handler();

        // Initiating Buttons
        turnOn = findViewById(R.id.turn_on_btn);
        turnOff = findViewById(R.id.turn_off_btn);
        blinkButton = findViewById(R.id.blink_btn);
        incDelay = findViewById(R.id.increase_delay_btn);
        decDelay = findViewById(R.id.decrease_delay_btn);

        // Handling Button Events
        turnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean status = checkPermission();
                try {
                    if (status) {
                        cameraManager.setTorchMode(CameraID, true);
                    }else{
                        Toast.makeText(getApplicationContext(),"Permission not granted",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        turnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean status = checkPermission();
                try {
                    if (status) {
                        cameraManager.setTorchMode(CameraID, false);
                    }else{
                        Toast.makeText(getApplicationContext(),"Permission not granted",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        blinkButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {

                if(checked){
                    flashOn = false;
                    handler.postDelayed(runnable,delay);
                }else{
                    try {
                        handler.removeCallbacks(runnable);
                        cameraManager.setTorchMode(CameraID,false);
                        flashOn = false;
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        // Control Delay
        incDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delay+=50;
            }
        });

        decDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delay-=50;
            }
        });
    }

    public boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    supportFlashLight = getPackageManager().
                            hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                    Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}