package dominando.android.ex33_sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent it) {
        SmsMessage sms = getMessagesFromIntent(it)[0];

        String telefone = sms.getOriginatingAddress();
        String mensagem = sms.getMessageBody();

        Toast.makeText(context,
                "Mensagem recebida de " + telefone + " - " + mensagem,
                Toast.LENGTH_LONG).show();
    }

    public static SmsMessage[] getMessagesFromIntent(Intent intent) {

        Object[] pdusExtras = (Object[])intent.getSerializableExtra("pdus");

        SmsMessage[] messages = new SmsMessage[pdusExtras.length];

        for (int i = 0; i < pdusExtras.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[])pdusExtras[i]);
        }
        return messages;
    }
}
