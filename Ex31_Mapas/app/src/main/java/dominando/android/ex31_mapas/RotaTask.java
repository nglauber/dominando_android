package dominando.android.ex31_mapas;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;

public class RotaTask extends AsyncTaskLoader<List<LatLng>> {

    List<LatLng> mRota;
    LatLng mOrigem;
    LatLng mDestino;

    public RotaTask(Context context, LatLng orig, LatLng dest) {
        super(context);
        mOrigem = orig;
        mDestino = dest;
    }

    @Override
    protected void onStartLoading() {
        if (mRota == null) {
            forceLoad();
        } else {
            deliverResult(mRota);
        }
    }

    @Override
    public List<LatLng> loadInBackground() {
        mRota = RotaHttp.carregarRota(mOrigem, mDestino);
        return mRota;
    }
}
