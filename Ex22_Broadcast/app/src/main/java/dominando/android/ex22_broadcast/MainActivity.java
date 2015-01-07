package dominando.android.ex22_broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    private static final String MINHA_ACAO =
            "dominando.android.ex22_broadcast.MINHA_ACAO";

    InternoReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiver = new InternoReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filterLocal = new IntentFilter(MINHA_ACAO);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, filterLocal);

        IntentFilter filter = new IntentFilter(Intent.ACTION_USER_PRESENT);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mReceiver);
    }

    public void enviarBroadcast(View v) {
        Intent it = new Intent(MINHA_ACAO);
        sendBroadcast(it);
    }

    public void enviarLocalBroadcast(View v) {
        Intent it = new Intent(MINHA_ACAO);
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(it);
    }

    class InternoReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView txtMensagem = (TextView)findViewById(R.id.txtMensagem);
            txtMensagem.setText("Ação:\n"+ intent.getAction());
        }
    }

}

