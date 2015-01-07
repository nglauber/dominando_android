package dominando.android.ex39_sensores;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class JogoActivity extends Activity
        implements SensorEventListener, Callback {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private SensorManager mSensorManager;
    private ThreadJogo mThreadJogo;
    private Bolinha mBolinha;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mSurfaceView = new SurfaceView(this);
        mSurfaceView.setKeepScreenOn(true);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        setContentView(mSurfaceView);

        mBolinha = new Bolinha(this);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    }

    protected void onResume() {
        super.onResume();
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mBolinha.setTamanhoTela(width, height);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mThreadJogo = new ThreadJogo(this, mBolinha, holder);
        mThreadJogo.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            mBolinha.setTamanhoTela(0, 0);
            mThreadJogo.parar();
        } finally {
            mThreadJogo = null;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mBolinha.setAceleracao(event.values[0], event.values[1]);
    }
}
