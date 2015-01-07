package dominando.android.ex22_broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MeuReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String acao = intent.getAction();
        Toast.makeText(context, "Ação: "+ acao, Toast.LENGTH_LONG).show();
        if (Intent.ACTION_BOOT_COMPLETED.equals(acao)) {
            // Não faça isso na sua aplicação!
            // Isso vai irritar o usuário :)
            Intent it = new Intent(context, MainActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(it);
        }

    }
}
