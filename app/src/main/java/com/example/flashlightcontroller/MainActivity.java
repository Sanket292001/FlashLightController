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
    private ImageView flashImageView;
    TextView delayTextView,flashOnOffStatus;
    CameraManager cameraManager;
    String CameraID;
    Runnable runnable;
    Handler handler;
    int delay = 1000;
    boolean stableFlashLightOn = false, blinkingFlashLightOn = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        Button turnOn, turnOff, incDelay, decDelay;
        ToggleButton blinkButton;

        // Check that flashlight is supported or not
        supportFlashLight = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        // CameraManager
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        // Initiating Buttons
        turnOn = findViewById(R.id.turn_on_btn);
        turnOff = findViewById(R.id.turn_off_btn);
        blinkButton = findViewById(R.id.blink_btn);
        incDelay = findViewById(R.id.increase_delay_btn);
        decDelay = findViewById(R.id.decrease_delay_btn);

        flashImageView = findViewById(R.id.flash_image_view);
        delayTextView = findViewById(R.id.delay_text_view);

        delayTextView.setText("Delay: " + delay);
        try {
            CameraID = cameraManager.getCameraIdList()[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        runnable = new Runnable() {
            @Override
            public void run() {
//                System.out.println("Current Delay : " + delay);
                try {
                    if (!blinkingFlashLightOn) {
                        cameraManager.setTorchMode(CameraID, true);
                        flashImageView.setImageResource(R.drawable.flash_on);
                        blinkingFlashLightOn = true;
                    } else {
                        cameraManager.setTorchMode(CameraID, false);
                        flashImageView.setImageResource(R.drawable.flash_off);
                        blinkingFlashLightOn = false;
                    }
                    handler.postDelayed(this, delay);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        handler = new Handler();


        // Handling Button Events
        turnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean status = checkPermission();
                try {
                    if (status) {
                        if (blinkButton.isChecked()) {
                            displayToast("First turn off blinking flashlight");
                        } else {
                            if (stableFlashLightOn) {
                                displayToast("Stable flashlight is already on");
                            } else {
                                cameraManager.setTorchMode(CameraID, true);
                                flashImageView.setImageResource(R.drawable.flash_on);
                                stableFlashLightOn = true;
                            }
                        }
                    } else {
                        displayToast("Permission not granted");
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
                        if (!stableFlashLightOn) {
                            displayToast("Stable flashlight is already off");
                        } else {
                            cameraManager.setTorchMode(CameraID, false);
                            flashImageView.setImageResource(R.drawable.flash_off);
                            stableFlashLightOn = false;
                        }

                    } else {
                        displayToast("Permission not granted");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        blinkButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                boolean status = checkPermission();
                if (status) {
                    if (checked) {
                        if (stableFlashLightOn) {
                            displayToast("Please first turn off stable flashlight");
                            blinkButton.setChecked(false);
                        } else {
                            blinkingFlashLightOn = true;
                            handler.postDelayed(runnable, delay);
                        }
                    } else {
                        try {
                            handler.removeCallbacks(runnable);
                            cameraManager.setTorchMode(CameraID, false);
                            flashImageView.setImageResource(R.drawable.flash_off);
                            blinkingFlashLightOn = false;
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    displayToast("Permission not granted");
                }

            }
        });

        // Control Delay
        incDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delay += 50;
                delayTextView.setText("Delay: " + delay);
            }
        });

        decDelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delay -= 50;
                delayTextView.setText("Delay: " + delay);
                if (delay <= 250) {
                    displayToast("Warning: Do not decrease delay below current value, it might damage your phone.");
                }
            }
        });
    }

    public void displayToast(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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