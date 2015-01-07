package dominando.android.hotel;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class HotelIntentService extends IntentService {

    public static final String ACAO_SINCRONIZAR = "sincronizar";
    public static final String EXTRA_SUCESSO   = "sucesso";

    public HotelIntentService() {
        super("HotelIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            HotelHttp hotelHttp = new HotelHttp(this);

            Intent it = new Intent(ACAO_SINCRONIZAR);
            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(this);
            try {
                hotelHttp.sincronizar();
                it.putExtra(EXTRA_SUCESSO, true);
            } catch (Exception e) {
                it.putExtra(EXTRA_SUCESSO, false);
                e.printStackTrace();
            } finally {
                lbm.sendBroadcast(it);
            }
        }
    }
}

