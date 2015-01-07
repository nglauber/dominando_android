package dominando.android.ex12_autocomplete;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

public class MeuAutoCompleteAdapter extends ArrayAdapter<String>
        implements Filterable {

    private List<String> listaCompleta;
    private List<String> resultados;
    private Filter meuFiltro;

    public MeuAutoCompleteAdapter(
            Context ctx, int layout, List<String> textos) {

        super(ctx, layout, textos);
        this.listaCompleta = textos;
        this.resultados = listaCompleta;
        this.meuFiltro = new MeuFiltro();
    }

    @Override
    public int getCount() {
        return resultados.size();
    }

    @Override
    public String getItem(int position) {
        if (resultados != null
                && resultados.size() > 0
                && position < resultados.size()) {
            return resultados.get(position);
        } else {
            return null;
        }
    }

    @Override
    public Filter getFilter() {
        return meuFiltro;
    }

    private class MeuFiltro extends Filter {

        @Override
        protected FilterResults performFiltering(
                CharSequence constraint) {

            FilterResults filterResults =
                    new FilterResults();

            ArrayList<String> temp =
                    new ArrayList<String>();

            if (constraint != null) {
                String term = Util.removeAcentos(
                        constraint.toString().trim().toLowerCase());

                String placeStr;
                for (String p : listaCompleta) {
                    placeStr = Util.removeAcentos(p.toLowerCase());

                    if (placeStr.indexOf(term) > -1) {
                        temp.add(p);
                    }
                }
            }
            filterResults.values = temp;
            filterResults.count = temp.size();
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(
                CharSequence constraint,
                FilterResults filterResults) {

            resultados = (ArrayList<String>)
                    filterResults.values;

            notifyDataSetChanged();
        }
    }
}
