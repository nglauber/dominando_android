package dominando.android.ex16_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

public class HotelDetalheFragment extends Fragment {

    public static final String TAG_DETALHE = "tagDetalhe";
    private static final String EXTRA_HOTEL = "hotel";

    TextView mTextNome;
    TextView mTextEndereco;
    RatingBar mRatingEstrelas;
    ShareActionProvider mShareActionProvider;

    Hotel mHotel;

    public static HotelDetalheFragment novaInstancia(Hotel hotel) {
        Bundle parametros = new Bundle();
        parametros.putSerializable(EXTRA_HOTEL, hotel);

        HotelDetalheFragment fragment = new HotelDetalheFragment();
        fragment.setArguments(parametros);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHotel = (Hotel)
                getArguments().getSerializable(EXTRA_HOTEL);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(
                R.layout.fragment_detalhe_hotel, container, false);

        mTextNome = (TextView)layout.findViewById(R.id.txtNome);
        mTextEndereco = (TextView)
                layout.findViewById(R.id.txtEndereco);
        mRatingEstrelas = (RatingBar)
                layout.findViewById(R.id.rtbEstrelas);

        if (mHotel != null) {
            mTextNome.setText(mHotel.nome);
            mTextEndereco.setText(mHotel.endereco);
            mRatingEstrelas.setRating(mHotel.estrelas);
        }
        return layout;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_hotel_detalhe, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);

        String texto = getString(R.string.texto_compartilhar,
                mHotel.nome, mHotel.estrelas);

        Intent it = new Intent(Intent.ACTION_SEND);
        it.setType("text/plain");
        it.putExtra(Intent.EXTRA_TEXT, texto);
        mShareActionProvider.setShareIntent(it);
    }
}
