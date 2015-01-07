package dominando.android.hotel;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HotelListFragment extends ListFragment implements
        ActionMode.Callback,
        AdapterView.OnItemLongClickListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener {
    ListView mListView;
    ActionMode mActionMode;
    CursorAdapter mAdapter;
    String mTextoBusca;
    HotelRepositorio mRepositorio;

    SwipeRefreshLayout mSwipeLayout;

    BroadcastReceiver mServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mSwipeLayout.setRefreshing(false);
            if (!intent.getBooleanExtra(HotelIntentService.EXTRA_SUCESSO, false)) {
                Toast.makeText(getActivity(), R.string.erro_sincronizacao,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        IntentFilter filter = new IntentFilter(HotelIntentService.ACAO_SINCRONIZAR);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mServiceReceiver, filter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRepositorio = new HotelRepositorio(getActivity());

        mAdapter = new HotelCursorAdapter(getActivity(), null);

        mListView = getListView();
        setListAdapter(mAdapter);

        mListView.setOnItemLongClickListener(this);

        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mServiceReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_list_hotel, null);
        mSwipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(
                R.color.azul, R.color.verde, R.color.laranja, R.color.vermelho);
        return layout;
    }

    @Override
    public void onRefresh() {
        Intent it = new Intent(getActivity(), HotelIntentService.class);
        getActivity().startService(it);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (mActionMode == null) {
            Activity activity = getActivity();
            if (activity instanceof AoClicarNoHotel) {
                Cursor cursor = (Cursor) l.getItemAtPosition(position);
                Hotel hotel = mRepositorio.hotelFromCursor(cursor);

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
        mTextoBusca = TextUtils.isEmpty(s) ? null : s;
        getLoaderManager().restartLoader(0, null, this);

    }

    public void limparBusca() {
        buscar(null);
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
                    Cursor cursor = (Cursor) mListView.getItemAtPosition(checked.keyAt(i));
                    Hotel hotel = mRepositorio.hotelFromCursor(cursor);

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
        for (int i = 0; i < mListView.getCount(); i++) {
            mListView.setItemChecked(i, false);
        }
        mListView.clearChoices();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mRepositorio.buscar(getActivity(), mTextoBusca);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    public interface AoClicarNoHotel {
        void clicouNoHotel(Hotel hotel);
    }

    public interface  AoExcluirHoteis {
        void exclusaoCompleta(List<Hotel> hoteis);
    }

}

