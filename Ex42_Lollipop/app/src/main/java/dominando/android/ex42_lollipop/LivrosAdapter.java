package dominando.android.ex42_lollipop;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class LivrosAdapter extends RecyclerView.Adapter<LivrosAdapter.LivrosViewHolder> {
    private Context mContext;
    private List<Livro> mLivros;
    private AoClicarNoLivroListener mListener;

    public LivrosAdapter(Context ctx, List<Livro> livros) {
        mContext = ctx;
        mLivros = livros;
    }

    public void setAoClicarNoLivroListener(AoClicarNoLivroListener l){
        mListener = l;
    }

    @Override
    public LivrosViewHolder onCreateViewHolder(ViewGroup parent,  int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_livro, parent, false);
        LivrosViewHolder vh = new LivrosViewHolder(v);
        v.setTag(vh);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    LivrosViewHolder vh = (LivrosViewHolder)view.getTag();
                    int position = vh.getPosition();
                    mListener.aoClicarNoLivro(view, position, mLivros.get(position));
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(LivrosViewHolder holder, int position) {
        Livro livro = mLivros.get(position);

        Picasso.with(mContext).load(livro.capa).into(holder.imgCapa);
        holder.txtTitulo.setText(livro.titulo);
        holder.txtAutores.setText(livro.autor);
        holder.txtAno.setText(String.valueOf(livro.ano));
        holder.txtPaginas.setText(String.valueOf(livro.paginas));
    }

    @Override
    public int getItemCount() {
        return mLivros != null ? mLivros.size() : 0;
    }

    public interface AoClicarNoLivroListener {
        void aoClicarNoLivro(View v, int position, Livro livro);
    }

    public static class LivrosViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgCapa;
        public TextView txtTitulo;
        public TextView txtAutores;
        public TextView txtPaginas;
        public TextView txtAno;

        public LivrosViewHolder(View parent) {
            super(parent);
            imgCapa = (ImageView)parent.findViewById(R.id.imgCapa);
            txtTitulo = (TextView)parent.findViewById(R.id.txtTitulo);
            txtAutores = (TextView)parent.findViewById(R.id.txtAutores);
            txtPaginas = (TextView)parent.findViewById(R.id.txtPaginas);
            txtAno = (TextView)parent.findViewById(R.id.txtAno);

            ViewCompat.setTransitionName(imgCapa, "capa");
            ViewCompat.setTransitionName(txtTitulo, "titulo");
            ViewCompat.setTransitionName(txtAutores, "autores");
            ViewCompat.setTransitionName(txtPaginas, "paginas");
            ViewCompat.setTransitionName(txtAno, "ano");
        }
    }
}

