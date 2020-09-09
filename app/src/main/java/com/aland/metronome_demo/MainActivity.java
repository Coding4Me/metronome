package com.aland.metronome_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.aland.metronome.IPFSeqService;
import com.aland.metronome.IServiceCallback;
import com.aland.metronome.PFSeqService;
import com.aland.metronome.PFSeqConfig;

import java.io.File;
import java.io.FileOutputStream;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import static com.aland.metronome.PFSeqConfig.ID;
import static com.aland.metronome.PFSeqConfig.ONGOING_NOTIF_ID;
import static com.aland.metronome.PFSeqConfig.TIME_SIG_LOWER;
import static com.aland.metronome.PFSeqConfig.TIME_SIG_UPPER;
import static com.aland.metronome.PFSeqMessage.ERROR_MSG_PREFIX;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.id_binding).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bindService();

            }
        });

        findViewById(R.id.id_unbinding).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                unbindService();
            }
        });

        findViewById(R.id.init).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ipfSeqService != null) {
                    try {

                        File audFile = File.createTempFile("demo_app_file", "");
                        InputStream ins = getResources().openRawResource(R.raw.dang);

                        OutputStream out = new FileOutputStream(audFile);

                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = ins.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }

                        HashMap<String, Integer> myConfigInts = new HashMap<String, Integer>() {{
                            put(ONGOING_NOTIF_ID, 4346);
                            put(TIME_SIG_UPPER, 1);
                            put(TIME_SIG_LOWER, 4);
                        }};
                        HashMap<String, String> myConfigStrings = new HashMap<String, String>() {{
                            put(ID, "service_id");
                        }};

                        PFSeqConfig config = new PFSeqConfig(myConfigInts, null, null, myConfigStrings);

                        ipfSeqService.init(audFile.getAbsolutePath(), config);
                    } catch (Exception e) {
                        Log.d(ERROR_MSG_PREFIX, "error creating file object \n" + e.getMessage());
                    }
                }
            }
        });

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ipfSeqService != null) {
                    try {
                        ipfSeqService.play();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (ipfSeqService != null) {
                        ipfSeqService.stop();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        final EditText input = findViewById(R.id.input);

        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (ipfSeqService != null) {
                        double bmp = ipfSeqService.getBmp();
                        double step = Double.parseDouble(input.getText().toString());

                        double result = bmp + step;
                        if (result > 300) {
                            result = 300;
                        }
                        Log.d(TAG, "bmp: " + result);

                        ipfSeqService.setBmp(result);
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ipfSeqService != null) {
                        double bmp = ipfSeqService.getBmp();
                        double step = Double.parseDouble(input.getText().toString());

                        double result = bmp - step;
                        if (result < 50) {
                            result = 50;
                        }

                        Log.d(TAG, "bmp: " + result);


                        ipfSeqService.setBmp(result);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private IPFSeqService ipfSeqService;
    private ServiceConnection connection;
    private void bindService() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d(TAG, "service onServiceConnected");
                try {
                    ipfSeqService = IPFSeqService.Stub.asInterface(iBinder);
                    ipfSeqService.registerListener(new IServiceCallback.Stub() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "init success");
                        }

                        @Override
                        public void onWarning(String message) {
                            Log.d(TAG, "onWarning: " + message);

                        }

                        @Override
                        public void onError(int code, String message) {
                            Log.d(TAG, "onError: " + code + " msg--->" + message);

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(TAG, "service onServiceDisconnected");
                ipfSeqService = null;
            }

            @Override
            public void onBindingDied(ComponentName name) {
                Log.d("TAG", "service onBindingDied");
            }
        };

        Intent service = new Intent(this, PFSeqService.class);

        bindService(service, connection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        try {
            if (ipfSeqService != null) {
                ipfSeqService.stop();
            }

            unbindService(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}