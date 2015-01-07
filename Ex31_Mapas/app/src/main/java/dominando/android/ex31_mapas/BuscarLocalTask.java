package dominando.android.ex31_mapas;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.content.AsyncTaskLoader;
import java.util.List;
import java.util.Locale;

public class BuscarLocalTask extends AsyncTaskLoader<List<Address>> {

    Context mContext;
    String mLocal;
    List<Address> mEnderecosEncontrados;

    public BuscarLocalTask(Context activity, String local) {
        super(activity);
        mContext = activity;
        mLocal = local;
    }

    @Override
    protected void onStartLoading() {
        if (mEnderecosEncontrados == null) {
            forceLoad();
        } else {
            deliverResult(mEnderecosEncontrados);
        }
    }

    @Override
    public List<Address> loadInBackground() {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            mEnderecosEncontrados = geocoder.getFromLocationName(mLocal, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mEnderecosEncontrados;
    }
}
