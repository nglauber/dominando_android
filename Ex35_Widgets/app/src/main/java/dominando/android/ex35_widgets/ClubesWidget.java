package dominando.android.ex35_widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

public class ClubesWidget extends AppWidgetProvider {

    public static final String ACAO_CLIQUE = "chamar_url";
    public static final String EXTRA_URL = "url";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACAO_CLIQUE.equals(intent.getAction())) {
            String url = intent.getStringExtra(EXTRA_URL);
            Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(it);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int i = 0; i < appWidgetIds.length; ++i) {
            Intent intent = new Intent(context, ClubesService.class);

            RemoteViews rv = new RemoteViews(
                    context.getPackageName(), R.layout.widget_clubes);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                rv.setRemoteAdapter(R.id.lstClubes, intent);
            } else {
                rv.setRemoteAdapter(appWidgetIds[i], R.id.lstClubes, intent);
            }

            Intent it = new Intent(context, ClubesWidget.class);
            it.setAction(ACAO_CLIQUE);
            it.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            PendingIntent pit = PendingIntent.getBroadcast(
                    context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.lstClubes, pit);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}


