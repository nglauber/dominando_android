package dominando.android.ex35_widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

public class ClubesService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ClubesRemoteViewsFactory(
                this.getApplicationContext(), intent);
    }
}

class ClubesRemoteViewsFactory
        implements RemoteViewsService.RemoteViewsFactory {

    private List<Clube> listClubes = new ArrayList<Clube>();
    private Context mContext;

    public ClubesRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
    }

    public void onCreate() {
        listClubes.add(new Clube("América-MG", "america-mg_45x45.png",
                "www.americamineiro.com.br"));
        listClubes.add(new Clube("Atlético-GO", "atl_go_45x45.png",
                "www.atleticogoianiense.com.br"));
        listClubes.add(new Clube("Atlético-MG", "atl_mg_45x45.png",
                "www.atletico.com.br"));
        listClubes.add(new Clube("Atlético-PR", "atl_pr_45x45.png",
                "www.atleticoparanaense.com"));
        listClubes.add(new Clube("Avaí", "avai_45x45.png",
                "www.avai.com.br"));
        listClubes.add(new Clube("Bahia", "bahia_45x45.png",
                "www.esporteclubebahia.com.br"));
        listClubes.add(new Clube("Botafogo", "botafogo_45x45.png",
                "www.botafogo.com.br"));
        listClubes.add(new Clube("Ceará", "ceara_45x45.png",
                "www.cearasc.com"));
        listClubes.add(new Clube("Corinthians", "corinthians_45x45.png",
                "www.corinthians.com.br"));
        listClubes.add(new Clube("Coritiba", "coritiba_45x45.png",
                "www.coritiba.com.br"));
        listClubes.add(new Clube("Cruzeiro", "cruzeiro_45x45.png",
                "www.cruzeiro.com.br"));
        listClubes.add(new Clube("Figueirense", "figueirense_45x45.png",
                "www.figueirense.com.br"));
        listClubes.add(new Clube("Flamengo", "flamengo_45x45.png",
                "www.flamengo.com.br"));
        listClubes.add(new Clube("Fluminense", "fluminense_45x45.png",
                "www.fluminense.com.br"));
        listClubes.add(new Clube("Grêmio", "gremio_45x45.png",
                "www.gremio.net"));
        listClubes.add(new Clube("Internacional",
                "internacional_45x45.png", "www.internacional.com.br"));
        listClubes.add(new Clube("Palmeiras", "palmeiras_45x45.png",
                "www.internacional.com.br"));
        listClubes.add(new Clube("Santos", "santos_45x45.png",
                "www.santosfc.com.br"));
        listClubes.add(new Clube("São Paulo", "sao_paulo_45x45.png",
                "www.saopaulofc.net"));
        listClubes.add(new Clube("Vasco", "vasco_45x45.png",
                "www.crvascodagama.com"));
    }

    public void onDestroy() {
        listClubes.clear();
    }

    public int getCount() {
        return listClubes.size();
    }

    public RemoteViews getViewAt(int position) {
        Clube clube = listClubes.get(position);
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(
                    mContext.getAssets().open(clube.escudo));
        } catch (Exception e) {
            e.printStackTrace();
        }

        RemoteViews rv = new RemoteViews(
                mContext.getPackageName(), R.layout.item_lista_clubes);
        rv.setTextViewText(R.id.txtNome, clube.nome);
        rv.setImageViewBitmap(R.id.imgEscudo, bmp);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(ClubesWidget.EXTRA_URL, "http://"+ clube.url);
        rv.setOnClickFillInIntent(R.id.txtNome, fillInIntent);

        return rv;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return false;
    }

    public void onDataSetChanged() {
    }
}
