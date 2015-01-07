package dominando.android.ex41_wear;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

public class MudancaPaginaService extends WearableListenerService {

    WearUtil mWearUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        mWearUtil = new WearUtil(this);
        mWearUtil.conectar();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWearUtil.desconectar();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Constantes.ACAO_ANTERIOR.equals(intent.getAction())) {
            mWearUtil.enviarMensagem(Constantes.MSG_NAVEGACAO,
                    new byte[]{Constantes.NAVEGACAO_ANTERIOR});

        } else if (Constantes.ACAO_PROXIMO.equals(intent.getAction())) {
            mWearUtil.enviarMensagem(Constantes.MSG_NAVEGACAO,
                    new byte[]{Constantes.NAVEGACAO_PROXIMO});
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();

            if (Constantes.CAMINHO_DADOS.equals(uri.getPath())) {
                if (event.getType() == DataEvent.TYPE_CHANGED) {
                    lerNovosDados(event);

                } else if (event.getType() == DataEvent.TYPE_DELETED) {
                    NotificationManagerCompat.from(this).cancel(Constantes.ID_NOTIFICACAO);
                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        if (Constantes.MSG_SAIR.equals(messageEvent.getPath())) {
            enviarMsgSair();
        }
    }

    private void enviarMsgSair(){
        NotificationManagerCompat.from(this).cancel(Constantes.ID_NOTIFICACAO);

        Intent it = new Intent(Constantes.ACAO_SAIR);
        LocalBroadcastManager.getInstance(this).sendBroadcast(it);
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        mWearUtil.conectar();
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        super.onPeerDisconnected(peer);
        enviarMsgSair();
        mWearUtil.desconectar();
    }

    private void lerNovosDados(DataEvent event) {
        DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
        int posicaoAtual = dataMap.getInt(Constantes.EXTRA_POSICAO_ATUAL);
        int total = dataMap.getInt(Constantes.EXTRA_TOTAL_IMAGENS);
        Asset imagem = dataMap.getAsset(Constantes.EXTRA_IMAGEM);
        dispararNotificacao(posicaoAtual, total);

        Intent it = new Intent(Constantes.ACAO_DADOS_ALTERADOS);
        it.putExtra(Constantes.EXTRA_POSICAO_ATUAL, posicaoAtual);
        it.putExtra(Constantes.EXTRA_TOTAL_IMAGENS, total);
        it.putExtra(Constantes.EXTRA_IMAGEM, imagem);
        LocalBroadcastManager.getInstance(this).sendBroadcast(it);
    }

    private void dispararNotificacao(int posicaoAtual, int total) {
        Intent itProximo = new Intent(Constantes.ACAO_PROXIMO);
        Intent itAnterior = new Intent(Constantes.ACAO_ANTERIOR);
        Intent itActivity = new Intent(this, MainWearActivity.class);

        PendingIntent pitProximo = PendingIntent.getService(
                this, 0, itProximo, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pitAnterior = PendingIntent.getService(
                this, 0, itAnterior, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pitActivity = PendingIntent.getActivity(
                this, 0, itActivity, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManagerCompat nm = NotificationManagerCompat.from(this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("Passar imagens")
                .setContentText((posicaoAtual + 1) + " / " + total)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher);

        NotificationCompat.WearableExtender extender =
                new NotificationCompat.WearableExtender();
        extender.addAction(
                new NotificationCompat.Action(
                        android.R.drawable.ic_media_previous,
                        "Anterior",
                        pitAnterior));
        extender.addAction(
                new NotificationCompat.Action(
                        android.R.drawable.ic_media_next,
                        "Próximo",
                        pitProximo));
        extender.addAction(
                new NotificationCompat.Action(
                        android.R.drawable.ic_media_play,
                        "Tela cheia",
                        pitActivity));
        extender.setHintHideIcon(true);
        builder.extend(extender);
        nm.notify(Constantes.ID_NOTIFICACAO, builder.build());
    }
}


