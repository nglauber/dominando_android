package dominando.android.ex26_notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    private static final int NOTIFICACAO_SIMPLES = 1;
    private static final int NOTIFICACAO_COMPLETA = 2;
    private static final int NOTIFICACAO_BIG = 3;
    private static final int NOTIFICACAO_RESPOSTA = 4;
    private static final int NOTIFICACAO_PAGINAS = 5;
    private static final int NOTIFICACAO_AGRUPADA = 6;

    EditText mEdtTexto;
    MeuReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEdtTexto = (EditText) findViewById(R.id.editText);

        mReceiver = new MeuReceiver();
        registerReceiver(mReceiver,
                new IntentFilter(NotificationUtils.ACAO_DELETE));
        registerReceiver(mReceiver,
                new IntentFilter(NotificationUtils.ACAO_NOTIFICACAO));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public void criarNotificacaoSimples(View v) {
        NotificationUtils.criarNotificacaoSimples(
                this,
                mEdtTexto.getText().toString(),
                NOTIFICACAO_SIMPLES);
    }

    public void criarNotificacaoCompleta(View v) {
        NotificationUtils.criarNotificacaoCompleta(
                this,
                mEdtTexto.getText().toString(),
                NOTIFICACAO_COMPLETA);
    }

    public void criarNotificacaoBig(View v) {
        NotificationUtils.criarNotificationBig(
                this,
                NOTIFICACAO_BIG);
    }

    public void criarNotificacaoComResposta(View v) {
        NotificationUtils.criarNotificacaoComResposta(this, NOTIFICACAO_RESPOSTA);
    }

    public void criarNotificacaoComPaginas(View v) {
        NotificationUtils.criarNotificacaoComPaginas(this, NOTIFICACAO_PAGINAS);
    }

    class MeuReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this, intent.getAction(), Toast.LENGTH_SHORT).show();
        }
    }

    public void criarNotificacaoAgrupadas(View v) {
        NotificationUtils.criarNotificacaoAgrupada(
                this,
                mEdtTexto.getText().toString(),
                NOTIFICACAO_AGRUPADA);
    }

}

