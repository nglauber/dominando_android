package dominando.android.hotel;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class HotelActivity extends ActionBarActivity
        implements HotelListFragment.AoClicarNoHotel,
        SearchView.OnQueryTextListener,
        MenuItemCompat.OnActionExpandListener,
        GenericDialogFragment.AoClicarNoDialog,
        HotelDialogFragment.AoSalvarHotel,
        HotelDetalheFragment.AoEditarHotel,
        HotelListFragment.AoExcluirHoteis {

    private FragmentManager mFragmentManager;
    private HotelListFragment mListFragment;
    private long mIdSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hotel);

        mFragmentManager = getSupportFragmentManager();
        mListFragment = (HotelListFragment)
                mFragmentManager.findFragmentById(R.id.fragmentLista);

        if (GcmHelper.googlePlayServicesEstaDisponivel(this)) {
            GcmHelper.registrar(this, new GcmHelper.AoRegistrarDispositivo() {
                @Override
                public void aoRegistrar(String regId, boolean emSegundoPlano) {
                    if (regId != null){
                        Toast.makeText(HotelActivity.this,
                                "Registrado com sucesso! \n"+ regId,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HotelActivity.this,
                                R.string.erro_gplay_falha_registrar,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.erro_gplay_indisponivel, Toast.LENGTH_SHORT).show();
        }
        if (GcmHelper.googlePlayServicesEstaDisponivel(this)) {
            GcmHelper.registrar(this, new GcmHelper.AoRegistrarDispositivo() {
                @Override
                public void aoRegistrar(String regId, boolean emSegundoPlano) {
                    if (regId != null){
                        Log.d("NGVL", regId);
                        Toast.makeText(HotelActivity.this,
                                "Registrado com sucesso! \n"+ regId,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HotelActivity.this,
                                R.string.erro_gplay_falha_registrar,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, R.string.erro_gplay_indisponivel, Toast.LENGTH_SHORT).show();
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mListFragment.limparBusca();
        }
    }

    @Override
    public void clicouNoHotel(Hotel hotel) {
        mIdSelecionado = hotel.id;
        if (isTablet()) {
            HotelDetalheFragment fragment = HotelDetalheFragment.novaInstancia(hotel);

            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.detalhe, fragment, HotelDetalheFragment.TAG_DETALHE);
            ft.commit();
        } else {
            Intent it = new Intent(this, HotelDetalheActivity.class);
            it.putExtra(HotelDetalheActivity.EXTRA_HOTEL, hotel);
            startActivityForResult(it, 0);
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
        HotelRepositorio repo = new HotelRepositorio(this);
        repo.salvar(hotel);
        mListFragment.limparBusca();
        if (isTablet()) {
            clicouNoHotel(hotel);
        }
    }

    @Override
    public void aoEditarhotel(Hotel hotel) {
        HotelDialogFragment editNameDialog = HotelDialogFragment.newInstance(hotel);
        editNameDialog.abrir(getSupportFragmentManager());
    }

    @Override
    public void exclusaoCompleta(List<Hotel> excluidos) {
        HotelDetalheFragment f = (HotelDetalheFragment)
                mFragmentManager.findFragmentByTag(HotelDetalheFragment.TAG_DETALHE);

        if (f != null) {
            boolean encontrou = false;
            for (Hotel h : excluidos) {
                if (h.id == mIdSelecionado) {
                    encontrou = true;
                    break;
                }
            }
            if (encontrou) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.remove(f);
                ft.commit();
            }
        }
    }

    private boolean isTablet() {
        return getResources().getBoolean(R.bool.tablet);
    }

    private boolean isSmartphone() {
        return getResources().getBoolean(R.bool.smartphone);
    }

}
