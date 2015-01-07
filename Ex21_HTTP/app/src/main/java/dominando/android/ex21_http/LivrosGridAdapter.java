package dominando.android.ex21_http;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import java.util.List;

public class LivrosGridAdapter extends ArrayAdapter<Livro> {

    private ImageLoader mLoader;

    public LivrosGridAdapter(Context context, List<Livro> objects) {
        super(context, 0, objects);
        mLoader = VolleySingleton.getInstance(context).getImageLoader();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Context ctx = parent.getContext();
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx)
                    .inflate(R.layout.item_livro_grid, null);
        }
        NetworkImageView img = (NetworkImageView)
                convertView.findViewById(R.id.imgCapa);
        TextView txt = (TextView)
                convertView.findViewById(R.id.txtTitulo);

        Livro livro = getItem(position);
        txt.setText(livro.titulo);
        img.setImageUrl(livro.capa, mLoader);

        return convertView;
    }
}

