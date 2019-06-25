package licenta.books.androidmobile.activities.others;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.Random;

import licenta.books.androidmobile.activities.ReaderBookActivity;

public class ShakeService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float accel; // acceleration apart from gravity
    private float accelCurrent; // current acceleration including gravity
    private float accelLast; // last acceleration including gravity


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_UI, new Handler());
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        accelLast = accelCurrent;
        accelCurrent = (float)Math.sqrt((double)(x*x + y*y + z*z));
        float delta = accelCurrent - accelLast;
        accel = accel*0.9f + delta;

        if(accel > 11){
            Random random = new Random();
//            int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
//            Toast.makeText(getApplicationContext(),"S-a miscat",Toast.LENGTH_LONG).show();
//            ReaderBookActivity
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
