package dominando.android.ex41_wear;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

public class MainMobileActivity extends ActionBarActivity
        implements WearUtil.ComunicacaoComRelogio, ViewPager.OnPageChangeListener {

    WearUtil mWearUtil;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mobile);

        mWearUtil = new WearUtil(this, this);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(new ImagemPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mWearUtil.conectar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWearUtil.desconectar();
    }

    private void proximaPagina() {
        int novaPagina = Math.min(
                mViewPager.getCurrentItem() + 1,
                mViewPager.getAdapter().getCount() - 1);
        moverPagina(novaPagina);
    }

    private void paginaAnterior() {
        int novaPagina = Math.max(mViewPager.getCurrentItem() - 1, 0);
        moverPagina(novaPagina);
    }

    private void moverPagina(final int position) {
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(position, true);
            }
        });
    }

    @Override
    public void onPageScrolled(int position,
                               float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mWearUtil.atualizarDados(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void aoConectar() {
        mWearUtil.atualizarDados(mViewPager.getCurrentItem());
    }

    @Override
    public void aoMudarDePagina(int pagina) {
        if (pagina == Constantes.NAVEGACAO_PROXIMO) {
            proximaPagina();
        } else if (pagina == Constantes.NAVEGACAO_ANTERIOR) {
            paginaAnterior();
        } else {
            moverPagina(pagina);
        }
    }
}

