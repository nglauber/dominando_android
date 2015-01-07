package dominando.android.ex32_contatos;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.widget.ListView;

public class ListaContatosFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private final static String[] COLUNAS = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME};

    private CursorAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mAdapter == null) {
            mAdapter = new ContatoAdapter(getActivity(), null);
            setListAdapter(mAdapter);

            getActivity().getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);

        long idContato = cursor.getLong(
                cursor.getColumnIndex(ContactsContract.Contacts._ID));
        String lookupKey = cursor.getString(
                cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

        Uri uriContato = ContactsContract.Contacts.getLookupUri(idContato, lookupKey);

        Intent it = new Intent(Intent.ACTION_VIEW, uriContato);
        startActivity(it);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                ContactsContract.Contacts.CONTENT_URI,
                COLUNAS,
                ContactsContract.Contacts.HAS_PHONE_NUMBER +" = 1 ",
                null,
                ContactsContract.Contacts.DISPLAY_NAME
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}

