package dominando.android.ex39_sensores;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
        implements SensorEventListener, AdapterView.OnItemSelectedListener {

    SensorManager mSensorManager;
    List<Sensor> mSensores;
    TextView mTxtValores;
    int mSensorSelecionado;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mTxtValores = (TextView) findViewById(R.id.txtValores);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensores = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        List<String> nomeSensores = new ArrayList<String>();
        for (Sensor sensor : mSensores) {
            nomeSensores.add(sensor.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, nomeSensores);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        Spinner spn = (Spinner) findViewById(R.id.spnSensores);
        spn.setAdapter(adapter);
        spn.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensores.get(mSensorSelecionado),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Toast.makeText(this, "Precisão mudou: "+ accuracy, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String valores = "Valores do Sensor:\n";
        for (int i = 0; i < event.values.length; i++) {
            valores += "values[" + i + "] = " + event.values[i] + "\n";
        }
        mTxtValores.setText(valores);
    }

    @Override
    public void onItemSelected(
            AdapterView<?> adapterView, View view, int position, long id) {
        mSensorSelecionado = position;
        mSensorManager.unregisterListener(this);
        mSensorManager.registerListener(
                this,
                mSensores.get(mSensorSelecionado),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}
