package dominando.android.hotel;

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
    HotelRepositorio mRepositorio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = getListView();
        mRepositorio = new HotelRepositorio(getActivity());
        limparBusca();
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

        List<Hotel> hoteisEncontrados =
                mRepositorio.buscarHotel("%" + s + "%");


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
        mHoteis = mRepositorio.buscarHotel(null);
        mListView.setOnItemLongClickListener(this);

        mAdapter = new MultiSelectAdapter<Hotel>(getActivity(),
                android.R.layout.simple_list_item_1,
                mHoteis);

        setListAdapter(mAdapter);
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

            List<Hotel> excluidos = new ArrayList<Hotel>();
            for (int i = checked.size()-1; i >= 0; i--) {
                if (checked.valueAt(i)) {
                    Hotel hotel = mHoteis.get(checked.keyAt(i));
                    mRepositorio.excluir(hotel);
                    excluidos.add(hotel);
                }
            }
            limparBusca();
            actionMode.finish();

            Activity activity = getActivity();
            if (activity instanceof AoExcluirHoteis) {
                AoExcluirHoteis aoExcluirHoteis =
                        (AoExcluirHoteis)activity;
                aoExcluirHoteis.exclusaoCompleta(excluidos);
            }
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

    public interface  AoExcluirHoteis {
        void exclusaoCompleta(List<Hotel> hoteis);
    }

}

