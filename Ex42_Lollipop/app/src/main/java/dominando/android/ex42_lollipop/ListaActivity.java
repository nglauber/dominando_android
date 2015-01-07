package dominando.android.ex42_lollipop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.ImageButton;

import java.util.List;

public class ListaActivity extends ActionBarActivity
        implements LivrosAdapter.AoClicarNoLivroListener{

    private RecyclerView mRecyclerView;
    private LivrosAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(5000);
            getWindow().setExitTransition(changeBounds);
            getWindow().setEnterTransition(changeBounds);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mLayoutManager = new LinearLayoutManager(this);
        } else {
            mLayoutManager = new GridLayoutManager(this, 2);
        }
        mRecyclerView.setLayoutManager(mLayoutManager);

        new LivrosTask().execute();

        ImageButton imageButton = (ImageButton) findViewById(R.id.btnAnim);
        float elevation = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        ViewCompat.setElevation(imageButton, elevation);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exibirOcultar(view);
            }
        });
    }

    @Override
    public void aoClicarNoLivro(final View v, final int position, final Livro livro) {
        Intent intent = new Intent(this, DetalheActivity.class);
        intent.putExtra(DetalheActivity.EXTRA_LIVRO, livro);

        View btnAnim = findViewById(R.id.btnAnim);
        ViewCompat.setTransitionName(btnAnim, "btn");
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                Pair.create(v.findViewById(R.id.imgCapa), "capa"),
                Pair.create(v.findViewById(R.id.txtTitulo), "titulo"),
                Pair.create(v.findViewById(R.id.txtAno), "ano"),
                Pair.create(v.findViewById(R.id.txtAutores), "autores"),
                Pair.create(v.findViewById(R.id.txtPaginas), "paginas"),
                Pair.create(btnAnim, "btn"));
        ActivityCompat.startActivity(this, intent, options.toBundle());

    }

    public void exibirOcultar(View view) {
        boolean exibindo = mRecyclerView.getVisibility() == View.VISIBLE;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            int cx = view.getLeft() + (view.getWidth() / 2);
            int cy = view.getTop() + (view.getHeight() / 2);
            int raio = Math.max(mRecyclerView.getWidth(), mRecyclerView.getHeight());

            if (exibindo) {
                Animator anim = ViewAnimationUtils.createCircularReveal(
                        mRecyclerView, cx, cy, raio, 0);

                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mRecyclerView.setVisibility(View.INVISIBLE);
                    }
                });
                anim.start();

            } else {
                Animator anim = ViewAnimationUtils.createCircularReveal(
                        mRecyclerView, cx, cy, 0, raio);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                });
                anim.start();
            }

        } else {
            mRecyclerView.setVisibility(exibindo ? View.INVISIBLE : View.VISIBLE);
        }
    }


    class LivrosTask extends AsyncTask<Void, Void, List<Livro>> {

        @Override
        protected List<Livro> doInBackground(Void... strings) {
            return LivroHttp.carregarLivrosJson();
        }

        @Override
        protected void onPostExecute(List<Livro> livros) {
            super.onPostExecute(livros);
            if (livros != null) {
                mAdapter = new LivrosAdapter(ListaActivity.this, livros);
                mAdapter.setAoClicarNoLivroListener(ListaActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }
}

