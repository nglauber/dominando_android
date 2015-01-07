package dominando.android.ex30_googleplus;

import android.widget.AbsListView;

public abstract class ScrollPaginado implements AbsListView.OnScrollListener {
    private int limiteItensVisiveis = 5;
    private int qtdeAnterior = 0;
    private boolean carregando = true;

    public ScrollPaginado(int limiteItensVisiveis) {
        this.limiteItensVisiveis = limiteItensVisiveis;
    }

    @Override
    public void onScroll(AbsListView view,int primeiraPosicaoVisivel,
                         int qtdeItensVisisveis,int qtdeTotal)
    {
        if (qtdeTotal < qtdeAnterior) {
            qtdeAnterior = qtdeTotal;
            if (qtdeTotal == 0) {
                this.carregando = true;
            }
        }

        if (carregando && qtdeTotal > qtdeAnterior) {
            carregando = false;
            qtdeAnterior = qtdeTotal;
        }

        if (!carregando &&
                (qtdeTotal - qtdeItensVisisveis) <= (primeiraPosicaoVisivel + limiteItensVisiveis)) {
            aoPrecisarCarregarMais();
            carregando = true;
        }
    }

    public abstract void aoPrecisarCarregarMais();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}

