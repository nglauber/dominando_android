package dominando.android.ex05_intents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;


public class AcaoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acao);

        TextView txt = (TextView)findViewById(R.id.txtAcao);

        Intent it = getIntent();

        if (it.getAction().equals(Intent.ACTION_VIEW)) {
            Uri uri = it.getData();
            txt.setText("Ação customizada 2. " +
                    "Uri: "+   uri.toString() +
                    " Host: "+ uri.getHost() +
                    " Path: "+ uri.getPath());

        } else if (it.getAction().equals("dominando.android.ACAO_CUSTOMIZADA")) {
            txt.setText("Ação customizada 1");
        }
    }
}

