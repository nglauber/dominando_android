package dominando.android.ex40_gradle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import dominando.android.minhalib.LibActivity;

public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void chamarLibActivity(View v) {
        startActivity(new Intent(this, LibActivity.class));
    }
}

