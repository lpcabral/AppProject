package com.example.cabra.aceldata;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.snatik.storage.Storage;


import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    private SensorManager sensorManager;
    private Sensor acel;
    TextView xAcelValue, yAcelValue, zAcelValue;
    ToggleButton tglData;

    Storage storage;
    String filename1 =  "/dadosAcel";
    String path;
    String ts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPermissionGranted();

        //define as Strings que vão ser usadas como caminho para guardar os dados do acelerometro
        storage = new Storage(getApplicationContext());
        path = storage.getExternalStorageDirectory(Environment.DIRECTORY_DOWNLOADS);

        xAcelValue = (TextView) findViewById(R.id.xAcelValue);
        yAcelValue = (TextView) findViewById(R.id.yAcelValue);
        zAcelValue = (TextView) findViewById(R.id.zAcelValue);



        Log.d(TAG, "Inicia o serviço do Sensor");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        acel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        /*  if(acel != null ){
            sensorManager.registerListener(MainActivity.this, acel, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "Regista o listener do acelerometro");}
        else{
            xAcelValue.setText("Acelerómetro não suportado");
            yAcelValue.setText("Acelerómetro não suportado");
            zAcelValue.setText("Acelerómetro não suportado");
        }*/

        tglData = (ToggleButton) findViewById(R.id.tglData);

        tglData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) { // The toggle is enabled

                    //Define o tempo e a string final que vai usada para o caminho onde são guardados os dados
                    Long tsLong = System.currentTimeMillis()/1000;
                    ts = tsLong.toString()+".txt";

                    //Cria o ficheiro onde guarda os dados
                    storage.createFile(path+filename1+ts, "");


                    if (acel != null) {


                    /*   ///////////////////////////////////////////////////////////////////////// timer para medir durante X segundos
                        new CountDownTimer(2000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                sensorManager.registerListener(MainActivity.this, acel, sensorManager.SENSOR_DELAY_FASTEST);
                            }

                            public void onFinish() {
                                sensorManager.unregisterListener(MainActivity.this);
                            }
                        }.start();

                        *///////////////////////////////////////////////////////////////////////////
                        sensorManager.registerListener(MainActivity.this, acel, sensorManager.SENSOR_DELAY_FASTEST);
                        Log.d(TAG, "Regista o listener do acelerometro");
                        Toast.makeText(getApplicationContext(),"A gravar...",Toast.LENGTH_SHORT).show();

                    } else {
                        xAcelValue.setText("Acelerómetro não suportado");
                        yAcelValue.setText("Acelerómetro não suportado");
                        zAcelValue.setText("Acelerómetro não suportado");
                    }


                } else {  // The toggle is disabled

                    sensorManager.unregisterListener(MainActivity.this);
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == sensor.TYPE_ACCELEROMETER) {
        getAccelerometer(sensorEvent);
        }
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // movimento
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z) //aceleração em G's
                        / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;

        xAcelValue.setText("Força G: " + new StringBuilder().append(accelationSquareRoot).toString());

        //armazena os dados do sensor no ficheiro criado
        storage.appendFile(path + filename1 + ts,new StringBuilder().append(accelationSquareRoot).toString());


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        }

    private boolean isPermissionGranted() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED ) {
            Log.v("mylog", "Permission is granted");
            return true;
        } else {
            Log.v("mylog", "Permission not granted");

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);

            return false;
        }
    }
    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }

  /*  @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
*/
}

