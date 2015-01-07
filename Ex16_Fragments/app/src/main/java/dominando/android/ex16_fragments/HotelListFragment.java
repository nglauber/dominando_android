package dominando.android.ex16_fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class HotelListFragment extends ListFragment
        implements ActionMode.Callback, AdapterView.OnItemLongClickListener {
    ListView mListView;
    List<Hotel> mHoteis;
    ArrayAdapter<Hotel> mAdapter;
    ActionMode mActionMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mHoteis = carregarHoteis();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
        limparBusca();
    }

    private List<Hotel> carregarHoteis() {
        List<Hotel> hotels = new ArrayList<Hotel>();
        hotels.add(new Hotel("New Beach Hotel", "Av. Boa Viagem", 4.5f));
        hotels.add(new Hotel("Recife Hotel", "Av. Boa Viagem", 4.0f));
        hotels.add(new Hotel("Canario Hotel", "Rua dos Navegantes", 3.0f));
        hotels.add(new Hotel("Byanca Beach Hotel", "Rua Mamanguape", 4.0f));
        hotels.add(new Hotel("Grand Hotel Dor", "Av. Bernardo", 3.5f));
        hotels.add(new Hotel("Hotel Cool", "Av. Conselheiro Aguiar", 4.0f));
        hotels.add(new Hotel("Hotel Infinito", "Rua Ribeiro de Brito", 5.0f));
        hotels.add(new Hotel("Hotel Tulipa", "Av. Boa Viagem", 5.0f));
        return hotels;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (mActionMode == null) {
            Activity activity = getActivity();
            if (activity instanceof AoClicarNoHotel) {
                Hotel hotel = (Hotel) l.getItemAtPosition(position);

                AoClicarNoHotel listener = (AoClicarNoHotel) activity;
                listener.clicouNoHotel(hotel);
            }
        } else {
            int checkedCount = atualizarItensMarcados(mListView, position);
            if (checkedCount == 0) {
                mActionMode.finish();
            }
        }
    }

    public void buscar(String s) {
        if (s == null || s.trim().equals("")) {
            limparBusca();
            return;
        }

        List<Hotel> hoteisEncontrados = new ArrayList<Hotel>(mHoteis);

        for (int i = hoteisEncontrados.size()-1; i >= 0; i--) {
            Hotel hotel = hoteisEncontrados.get(i);
            if (! hotel.nome.toUpperCase().contains(s.toUpperCase())) {
                hoteisEncontrados.remove(hotel);
            }
        }

        mListView.setOnItemLongClickListener(null);
        mAdapter = new ArrayAdapter<Hotel>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                hoteisEncontrados);
        setListAdapter(mAdapter);
    }

    public void limparBusca() {
        mListView.setOnItemLongClickListener(this);

        mAdapter = new MultiSelectAdapter<Hotel>(getActivity(),
                android.R.layout.simple_list_item_1,
                mHoteis);

        setListAdapter(mAdapter);
    }

    public void adicionar(Hotel hotel) {
        mHoteis.add(hotel);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_delete_list, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.acao_delete) {
            SparseBooleanArray checked = mListView.getCheckedItemPositions();

            for (int i = checked.size()-1; i >= 0; i--) {
                if (checked.valueAt(i)) {
                    mHoteis.remove(checked.keyAt(i));
                }
            }
            actionMode.finish();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mActionMode = null;
        mListView.clearChoices();
        mAdapter.notifyDataSetChanged();
        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        boolean consumed = (mActionMode == null);

        if (consumed) {
            ActionBarActivity activity = (ActionBarActivity)getActivity();

            mActionMode = activity.startSupportActionMode(this);
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            mListView.setItemChecked(i, true);
            atualizarItensMarcados(mListView, i);
        }
        return consumed;
    }

    private int atualizarItensMarcados(ListView l, int position) {
        SparseBooleanArray checked = l.getCheckedItemPositions();

        l.setItemChecked(position, l.isItemChecked(position));

        int checkedCount = 0;
        for (int i = 0; i < checked.size(); i++) {
            if (checked.valueAt(i)) {
                checkedCount++;
            }
        }

        String selecionados = getResources().getQuantityString(
                R.plurals.numero_selecionados,
                checkedCount, checkedCount);
        mActionMode.setTitle(selecionados);

        return checkedCount;
    }

    public interface AoClicarNoHotel {
        void clicouNoHotel(Hotel hotel);
    }

}

