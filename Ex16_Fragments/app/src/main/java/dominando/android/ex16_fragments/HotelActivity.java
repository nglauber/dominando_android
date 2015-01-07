package dominando.android.ex16_fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

public class HotelActivity extends ActionBarActivity
        implements HotelListFragment.AoClicarNoHotel,
        SearchView.OnQueryTextListener,
        MenuItemCompat.OnActionExpandListener,
        GenericDialogFragment.AoClicarNoDialog,
        HotelDialogFragment.AoSalvarHotel {

    private FragmentManager mFragmentManager;
    private HotelListFragment mListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hotel);

        mFragmentManager = getSupportFragmentManager();
        mListFragment = (HotelListFragment)
                mFragmentManager.findFragmentById(R.id.fragmentLista);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hotel, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.hint_busca));
        MenuItemCompat.setOnActionExpandListener(searchItem, this);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                GenericDialogFragment dialog = GenericDialogFragment.novoDialog(
                        // Id do dialog
                        0,
                        // título
                        R.string.sobre_titulo,
                        // mensagem
                        R.string.sobre_mensagem,
                        // texto dos botões
                        new int[]{
                                android.R.string.ok, // String do Android
                                R.string.sobre_botao_site
                        });
                dialog.abrir(mFragmentManager);
                break;
            case R.id.action_new:
                HotelDialogFragment hotelDialogFragment = HotelDialogFragment.newInstance(null);
                hotelDialogFragment.abrir(getSupportFragmentManager());
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void clicouNoHotel(Hotel hotel) {
        if (isTablet()) {
            HotelDetalheFragment fragment = HotelDetalheFragment.novaInstancia(hotel);

            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.detalhe, fragment, HotelDetalheFragment.TAG_DETALHE);
            ft.commit();
        } else {
            Intent it = new Intent(this, HotelDetalheActivity.class);
            it.putExtra(HotelDetalheActivity.EXTRA_HOTEL, hotel);
            startActivity(it);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        mListFragment.buscar(s);
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true; // para expandir a view
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mListFragment.limparBusca();
        return true; // para voltar ao normal
    }

    @Override
    public void aoClicar(int id, int botao) {
        if (botao == DialogInterface.BUTTON_NEGATIVE) {
            Intent it = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://nglauber.blogspot.com"));
            startActivity(it);
        }
    }

    @Override
    public void salvouHotel(Hotel hotel) {
        mListFragment.adicionar(hotel);
    }


    private boolean isTablet() {
        return getResources().getBoolean(R.bool.tablet);
    }

    private boolean isSmartphone() {
        return getResources().getBoolean(R.bool.smartphone);
    }

}
