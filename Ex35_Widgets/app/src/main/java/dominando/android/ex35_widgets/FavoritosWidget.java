package dominando.android.ex35_widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class FavoritosWidget extends AppWidgetProvider {
    public static final String EXTRA_ACAO    = "acao";
    public static final String ACAO_ANTERIOR = "anterior";
    public static final String ACAO_PROXIMO  = "proximo";
    public static final String ACAO_EXCLUIR  = "excluir";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        RemoteViews views = new RemoteViews(
                context.getPackageName(), R.layout.widget_favoritos);

        for (int i = 0; i < appWidgetIds.length; i++) {
            configuraClickBotoes(context, appWidgetIds[i], views);
        }
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Intent it = new Intent(context, FavoritosService.class);
        it.putExtra(EXTRA_ACAO, ACAO_EXCLUIR);
        it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.startService(it);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        context.stopService(new Intent(context, FavoritosService.class));
    }

    private void configuraClickBotoes(Context context, int appWidgetId, RemoteViews views) {
        views.setOnClickPendingIntent(R.id.btnProximo,
                servicePendingIntent(context, ACAO_PROXIMO, appWidgetId));

        views.setOnClickPendingIntent(R.id.btnAnterior,
                servicePendingIntent(context, ACAO_ANTERIOR, appWidgetId));
    }

    private PendingIntent servicePendingIntent(
            Context ctx, String acao, int appWidgetId) {

        Intent serviceIntent = new Intent(ctx, FavoritosService.class);
        serviceIntent.putExtra(EXTRA_ACAO, acao);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent pit = PendingIntent.getService(
                ctx, appWidgetId, serviceIntent, 0);
        return pit;
    }
}
