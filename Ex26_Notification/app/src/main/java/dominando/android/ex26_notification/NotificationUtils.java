package dominando.android.ex26_notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;

import java.util.ArrayList;
import java.util.List;

public class NotificationUtils {

    public static final String ACAO_DELETE =
            "dominando.android.ex25_notification.DELETE_NOTIFICACAO";
    public static final String ACAO_NOTIFICACAO =
            "dominando.android.ex25_notification.ACAO_NOTIFICACAO";

    private static PendingIntent criarPendingIntent(
            Context ctx, String texto, int id) {

        Intent resultIntent = new Intent(ctx, DetalheActivity.class);
        resultIntent.putExtra(DetalheActivity.EXTRA_TEXTO, texto);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
        stackBuilder.addParentStack(DetalheActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        return stackBuilder.getPendingIntent(
                id, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void criarNotificacaoSimples(Context ctx, String texto, int id) {
        PendingIntent resultPendingIntent = criarPendingIntent(ctx, texto, id);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_notificacao)
                        .setContentTitle("Simples "+ id)
                        .setContentText(texto)
                        .setContentIntent(resultPendingIntent);

        NotificationManagerCompat nm = NotificationManagerCompat.from(ctx);

        nm.notify(id, mBuilder.build());
    }

    public static void criarNotificacaoCompleta(Context ctx, String texto, int id) {
        Uri uriSom = Uri.parse("android.resource://"+
                ctx.getPackageName() +"/raw/som_notificacao");

        PendingIntent pitAcao = PendingIntent.getBroadcast(
                ctx, 0, new Intent(ACAO_NOTIFICACAO), 0);
        PendingIntent pitDelete = PendingIntent.getBroadcast(
                ctx, 0, new Intent(ACAO_DELETE), 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(
                ctx.getResources(), R.drawable.ic_launcher);

        PendingIntent pitNotificacao= criarPendingIntent(ctx, texto, id);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.drawable.ic_notificacao)
                        .setContentTitle("Completa")
                        .setContentText(texto)
                        .setTicker("Chegou uma notificação")
                        .setWhen(System.currentTimeMillis())
                        .setLargeIcon(largeIcon)
                        .setAutoCancel(true)
                        .setContentIntent(pitNotificacao)
                        .setDeleteIntent(pitDelete)
                        .setLights(Color.BLUE, 1000, 5000)
                        .setSound(uriSom)
                        .setVibrate(new long[]{100, 500, 200, 800})
                        .addAction(R.drawable.ic_acao_notificacao, "Ação Customizada", pitAcao)
                        .setNumber(id)
                        .setSubText("Subtexto");

        NotificationManagerCompat nm = NotificationManagerCompat.from(ctx);
        nm.notify(id, mBuilder.build());
    }

    public static void criarNotificationBig(Context ctx, int idNotificacao) {
        int numero = 5;
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        inboxStyle.setBigContentTitle("Mensagens não lidas:");
        for (int i = 1; i <= numero; i++) {
            inboxStyle.addLine("Mensagem " + i);
        }
        inboxStyle.setSummaryText("Clique para exibir");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.drawable.ic_notificacao)
                .setContentTitle("Notificação")
                .setContentText("Vários itens pendentes")
                .setContentIntent(criarPendingIntent(ctx, "Mensagens não lidas", -1))
                .setNumber(numero)
                .setStyle(inboxStyle);

        NotificationManagerCompat nm = NotificationManagerCompat.from(ctx);
        nm.notify(idNotificacao, mBuilder.build());
    }

    public static void criarNotificacaoComResposta(Context ctx, int idNotificacao) {
        RemoteInput remoteInput = new RemoteInput.Builder(DetalheActivity.EXTRA_RESPOSTA_VOZ)
                .setLabel("Diga a resposta")
                .build();

        PendingIntent pit = criarPendingIntent(ctx, "Notificação com resposta", idNotificacao);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(
                R.drawable.ic_acao_notificacao, "Responder", pit)
                .addRemoteInput(remoteInput)
                .build();

        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender();

        Notification notificacao = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.drawable.ic_notificacao)
                .setContentTitle("Com resposta")
                .setContentText("Passe a página para responder")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .extend(wearableExtender.addAction(action))
                .build();

        NotificationManagerCompat nm = NotificationManagerCompat.from(ctx);
        nm.notify(idNotificacao, notificacao);
    }

    public static void criarNotificacaoComPaginas(Context ctx, int idNotificacao) {
        PendingIntent pit = criarPendingIntent(ctx, "Notificação com páginas", idNotificacao);

        NotificationCompat.Builder notificacaoPrincipal = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.drawable.ic_notificacao)
                .setContentTitle("Com páginas")
                .setContentText("Essa é a primeira página")
                .setAutoCancel(true)
                .setContentIntent(pit)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationCompat.BigTextStyle estiloDePagina = new NotificationCompat.BigTextStyle()
                .setBigContentTitle("Segunda página")
                .bigText("Um texto qualquer que você queira colocar na segunda página");

        Notification notificacaoPag2 = new NotificationCompat.Builder(ctx)
                .setStyle(estiloDePagina)
                .build();

        Notification notificacaoCom2Paginas = new NotificationCompat.WearableExtender()
                .addPage(notificacaoPag2)
                .extend(notificacaoPrincipal)
                .build();

        NotificationManagerCompat nm = NotificationManagerCompat.from(ctx);
        nm.notify(idNotificacao, notificacaoCom2Paginas);
    }

    private static final List<String> sMensagens = new ArrayList<String>();

    public static void criarNotificacaoAgrupada(Context ctx,  String texto, int idNotificacao) {
        int contador = sMensagens.size() + 1;
        sMensagens.add(texto);

        final String GRUPO_MENSAGENS = "mensages";

        NotificationCompat.Builder notificacaoSimples =
                new NotificationCompat.Builder(ctx)
                        .setContentTitle(ctx.getString(R.string.titulo_notificacao, contador))
                        .setSmallIcon(R.drawable.ic_notificacao)
                        .setGroup(GRUPO_MENSAGENS)
                        .setContentText(texto);

        String qtdeDeMensagens = ctx.getResources().getQuantityString(
                R.plurals.qtde_mensagens, contador, contador);

        PendingIntent intentLimparLista = PendingIntent.getBroadcast(
                ctx, 0, new Intent(ctx, ExcluirMensagensReceiver.class), 0);

        Bitmap imgBackground = BitmapFactory.decodeResource(
                ctx.getResources(), R.drawable.ic_launcher);

        NotificationCompat.WearableExtender extensaoWear =
                new NotificationCompat.WearableExtender().setBackground(imgBackground);

        NotificationCompat.InboxStyle estilo = new NotificationCompat.InboxStyle()
                .setBigContentTitle(qtdeDeMensagens)
                .setSummaryText("seuemail@gmail.com");

        for (String msgTitulo : sMensagens){
            estilo.addLine(msgTitulo);
        }

        NotificationCompat.Builder notificacaoResumo = new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.drawable.ic_notificacao)
                .setDeleteIntent(intentLimparLista)
                .extend(extensaoWear)
                .setStyle(estilo)
                .setDefaults(Notification.DEFAULT_ALL)
                .setGroup(GRUPO_MENSAGENS)
                .setGroupSummary(true);

        NotificationManagerCompat nm = NotificationManagerCompat.from(ctx);
        nm.notify(idNotificacao + contador, notificacaoSimples.build());
        nm.notify(idNotificacao, notificacaoResumo.build());
    }

    public static class ExcluirMensagensReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            sMensagens.clear();
        }
    }
}

