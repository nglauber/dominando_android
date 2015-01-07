package dominando.android.ex35_widgets;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import java.util.HashMap;
import java.util.Map;

public class FavoritosService extends Service {
    private String[] sites = {
            "nglauber.blogspot.com",
            "developer.android.com",
            "www.unibratec.edu.br",
            "www.especializa.com.br",
            "www.cesar.edu.br",
            "www.cesar.org.br" };

    private Map<Integer, Integer> widgetStates;

    @Override
    public void onCreate() {
        super.onCreate();
        widgetStates = new HashMap<Integer, Integer>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {

            String acao = intent.getStringExtra(FavoritosWidget.EXTRA_ACAO);

            if (acao != null) {

                if (FavoritosWidget.ACAO_PROXIMO.equals(acao) ||
                        FavoritosWidget.ACAO_ANTERIOR.equals(acao)){
                    int appWidgetId = intent.getIntExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID);

                    int posicao = novaPosicao(acao, appWidgetId);

                    RemoteViews views = new RemoteViews(
                            this.getPackageName(), R.layout.widget_favoritos);

                    views.setTextViewText(R.id.txtSite, sites[posicao]);

                    AppWidgetManager appWidgetManager =
                            AppWidgetManager.getInstance(this);

                    appWidgetManager.updateAppWidget(appWidgetId, views);

                } else if (FavoritosWidget.ACAO_EXCLUIR.equals(acao)){
                    int[] widgetsExcluidos = intent.getIntArrayExtra(
                            AppWidgetManager.EXTRA_APPWIDGET_IDS);
                    for (int id : widgetsExcluidos){
                        widgetStates.remove(id);
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // Não usado aqui
        return null;
    }

    private int novaPosicao(String acao, int appWidgetId){
        int posicao = 0;
        if (widgetStates.containsKey(appWidgetId)) {
            posicao = widgetStates.get(appWidgetId);
        } else {
            widgetStates.put(appWidgetId, posicao);
            return posicao;
        }

        if (FavoritosWidget.ACAO_PROXIMO.equals(acao)) {
            posicao++;
            if (posicao >= sites.length) {
                posicao = 0;
            }

        } else if (FavoritosWidget.ACAO_ANTERIOR.equals(acao)) {
            posicao--;
            if (posicao < 0) {
                posicao = sites.length - 1;
            }
        }
        widgetStates.put(appWidgetId, posicao);
        return posicao;
    }
}

