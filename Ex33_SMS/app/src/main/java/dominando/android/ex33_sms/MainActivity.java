package dominando.android.ex33_sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    private static final String ACAO_ENVIADO = "sms_enviado";
    private static final String ACAO_ENTREGUE = "sms_entregue";

    EditText mEdtNumero;
    EditText mEdtMensagem;
    EnvioSmsReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEdtNumero = (EditText) findViewById(R.id.edtNumeroTelefone);
        mEdtMensagem = (EditText) findViewById(R.id.edtMensagem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new EnvioSmsReceiver();

        registerReceiver(mReceiver, new IntentFilter(ACAO_ENVIADO));
        registerReceiver(mReceiver, new IntentFilter(ACAO_ENTREGUE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void enviarSmsClick(View v) {

        PendingIntent pitEnviado = PendingIntent.getBroadcast(
                this, 0, new Intent(ACAO_ENVIADO), 0);

        PendingIntent pitEntregue = PendingIntent.getBroadcast(
                this, 0, new Intent(ACAO_ENTREGUE), 0);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(
                mEdtNumero.getText().toString(),
                null,
                mEdtMensagem.getText().toString(),
                pitEnviado,
                pitEntregue);
    }

    public static class EnvioSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String mensagem = null;
            String acao = intent.getAction();
            int resultado = getResultCode();

            if (resultado == Activity.RESULT_OK) {
                if (ACAO_ENVIADO.equals(acao)) {
                    mensagem = "Enviado com sucesso.";
                } else if (ACAO_ENTREGUE.equals(acao)) {
                    mensagem = "Entregue com sucesso.";
                }
            } else {
                if (ACAO_ENVIADO.equals(acao)) {
                    mensagem = "Falha ao enviar: " + resultado;
                } else if (ACAO_ENTREGUE.equals(acao)) {
                    mensagem = "Falhar ao entregar: " + resultado;
                }
            }
            Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
        }
    }
}

