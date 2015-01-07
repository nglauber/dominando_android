package dominando.android.hotel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

public class HotelDetalheActivity extends ActionBarActivity
        implements HotelDetalheFragment.AoEditarHotel,
            HotelDialogFragment.AoSalvarHotel {

    public static final String EXTRA_HOTEL = "hotel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detalhe);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Hotel hotel = (Hotel)
                intent.getSerializableExtra(EXTRA_HOTEL);
        exibirHotelFragment(hotel);
    }

    private void exibirHotelFragment(Hotel hotel) {
        HotelDetalheFragment fragment = HotelDetalheFragment.novaInstancia(hotel);

        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.detalhe, fragment, HotelDetalheFragment.TAG_DETALHE);
        ft.commit();
    }

    @Override
    public void aoEditarhotel(Hotel hotel) {
        HotelDialogFragment editNameDialog = HotelDialogFragment.newInstance(hotel);
        editNameDialog.abrir(getSupportFragmentManager());
    }

    @Override
    public void salvouHotel(Hotel hotel) {
        HotelRepositorio repo = new HotelRepositorio(this);
        repo.salvar(hotel);
        exibirHotelFragment(hotel);
        setResult(RESULT_OK);
    }
}
