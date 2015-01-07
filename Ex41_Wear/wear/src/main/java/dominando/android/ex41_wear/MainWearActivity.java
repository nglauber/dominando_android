package dominando.android.ex41_wear;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import java.io.InputStream;

public class MainWearActivity extends Activity
        implements ViewPager.OnPageChangeListener {

    WearUtil mWearUtil;
    LocalBroadcastManager mLocalBroadcastManager;
    MensagensMobileReceiver mReceiver;

    ViewPager mViewPager;
    DismissOverlayView mDismissOverlayView;
    GestureDetector mGestureDetector;

    ImagemPagerAdapter mPageAdapter;
    int mPosicaoAtual = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exibirAnimacaoSucesso();

        setContentView(R.layout.activity_main_wear);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                iniciarUI(stub);
            }
        });

        mWearUtil = new WearUtil(this) {
            @Override
            public void onConnected(Bundle bundle) {
                super.onConnected(bundle);
                carregarDadosAtuais();
            }
        };
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        mReceiver = new MensagensMobileReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter1 = new IntentFilter(Constantes.ACAO_DADOS_ALTERADOS);
        IntentFilter filter2 = new IntentFilter(Constantes.ACAO_SAIR);
        mLocalBroadcastManager.registerReceiver(mReceiver, filter1);
        mLocalBroadcastManager.registerReceiver(mReceiver, filter2);
        mWearUtil.conectar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
        mWearUtil.desconectar();
    }

    public void exibirAnimacaoSucesso() {
        Intent intent = new Intent(this, ConfirmationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);

        startActivity(intent);
    }

    private void atualizarTela(final int novaPosicao, final int total, Asset asset) {

        PendingResult<DataApi.GetFdForAssetResult> fdForAsset =
                Wearable.DataApi.getFdForAsset(mWearUtil.getGoogleApiClient(), asset);
        fdForAsset.setResultCallback(new ResultCallback<DataApi.GetFdForAssetResult>() {
            @Override
            public void onResult(DataApi.GetFdForAssetResult getFdForAssetResult) {
                InputStream assetInputStream = getFdForAssetResult.getInputStream();
                final Bitmap bmp = BitmapFactory.decodeStream(assetInputStream);

                mViewPager.post(new Runnable() {
                    @Override
                    public void run() {

                        if (mPageAdapter.getCount() != total) {
                            mPageAdapter.setCount(total);
                            mPageAdapter.notifyDataSetChanged();
                        }

                        findViewById(R.id.viewRaiz).setBackground(
                                new BitmapDrawable(getResources(), bmp));

                        if (novaPosicao != mPosicaoAtual) {
                            mViewPager.setOnPageChangeListener(null);
                            mViewPager.setCurrentItem(novaPosicao, true);
                            mViewPager.setOnPageChangeListener(MainWearActivity.this);
                        }
                    }
                });
            }
        });
    }

    private void iniciarUI(WatchViewStub stub) {
        mPageAdapter = new ImagemPagerAdapter(getFragmentManager());

        mViewPager = (ViewPager) stub.findViewById(R.id.viewPager);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setAdapter(mPageAdapter);
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });

        mDismissOverlayView = (DismissOverlayView) stub.findViewById(R.id.dismiss);

        mGestureDetector = new GestureDetector(this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public void onLongPress(MotionEvent e) {
                        super.onLongPress(e);
                        mDismissOverlayView.show();
                    }
                });
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        mPosicaoAtual = i;
        atualizarMobile(i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private void atualizarMobile(int posicao) {
        byte[] bytes = new byte[]{(byte) posicao};
        mWearUtil.enviarMensagem(Constantes.MSG_POSICAO, bytes);
    }

    private void carregarDadosAtuais() {
        PendingResult<DataItemBuffer> dataItems =
                Wearable.DataApi.getDataItems(mWearUtil.getGoogleApiClient());
        dataItems.setResultCallback(new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {

                for (int i = 0; i < dataItems.getCount(); i++) {
                    DataItem dataItem = dataItems.get(i);

                    Uri uri = dataItem.getUri();

                    if (Constantes.CAMINHO_DADOS.equals(uri.getPath())) {
                        DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
                        int posicaoAtual = dataMap.getInt(Constantes.EXTRA_POSICAO_ATUAL);
                        int total = dataMap.getInt(Constantes.EXTRA_TOTAL_IMAGENS);
                        Asset asset = dataMap.getAsset(Constantes.EXTRA_IMAGEM);
                        atualizarTela(posicaoAtual, total, asset);
                    }
                }
            }
        });
    }

    public static void exibirAnimacaoSucesso(Context context) {
        Intent intent = new Intent(context, ConfirmationActivity.class);
        intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.SUCCESS_ANIMATION);

        context.startActivity(intent);
    }

    class MensagensMobileReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constantes.ACAO_SAIR.equals(intent.getAction())) {
                finish();

            } else if (Constantes.ACAO_DADOS_ALTERADOS.equals(intent.getAction())) {
                final int novaPosicao = intent.getIntExtra(Constantes.EXTRA_POSICAO_ATUAL, 0);
                final int total = intent.getIntExtra(Constantes.EXTRA_TOTAL_IMAGENS, 0);
                final Asset asset = intent.getParcelableExtra(Constantes.EXTRA_IMAGEM);
                atualizarTela(novaPosicao, total, asset);
            }
        }
    }
}

