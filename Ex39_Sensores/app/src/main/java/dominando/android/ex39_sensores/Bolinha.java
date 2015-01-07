package dominando.android.ex39_sensores;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Bolinha {

    private static final float REBOTE = 0.6f;
    private static final float VELOCIDADE_MINIMA = 5f;
    private static final float PIXELS_POR_METRO = 25;

    private float mPosicaoX, mPosicaoY;
    private float mVelocidadeX, mVelocidadeY;
    private float mAceleracaoX, mAceleracaoY;
    private float mLarguraTela, mAlturaTela;
    private long mTempoUltAtualizacao = -1;

    private Bitmap mImgBolinha;


    public Bolinha(Context context){
        mImgBolinha = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ball);
    }



    public float getX(){
        return mPosicaoX;
    }

    public float getY(){
        return mPosicaoY;
    }

    public Bitmap getImagemBolinha(){
        return mImgBolinha;
    }

    public void setAceleracao(float x, float y){
        mAceleracaoX = -x;
        mAceleracaoY = y;
        calcularFisica();
    }

    public void setTamanhoTela(int w, int h) {
        mLarguraTela = w;
        mAlturaTela = h;
    }

    public void calcularFisica() {

        if (mLarguraTela <= 0 || mAlturaTela <= 0) return;

        long tempoAtual = System.currentTimeMillis();
        if (mTempoUltAtualizacao < 0) {
            mTempoUltAtualizacao = tempoAtual;
            return;
        }
        long tempoDecorrido = tempoAtual - mTempoUltAtualizacao;
        mTempoUltAtualizacao = tempoAtual;

        mVelocidadeX += ((mAceleracaoX * tempoDecorrido) / 1000) * PIXELS_POR_METRO;
        mVelocidadeY += ((mAceleracaoY * tempoDecorrido) / 1000) * PIXELS_POR_METRO;

        mPosicaoX += ((mVelocidadeX * tempoDecorrido) / 1000) * PIXELS_POR_METRO;
        mPosicaoY += ((mVelocidadeY * tempoDecorrido) / 1000) * PIXELS_POR_METRO;

        boolean rebateuEmX = false;
        boolean rebateuEmY = false;

        if (mPosicaoY < 0) {
            mPosicaoY = 0;
            mVelocidadeY = -mVelocidadeY * REBOTE;
            rebateuEmY = true;

        } else if (mPosicaoY + mImgBolinha.getHeight() > mAlturaTela) {
            mPosicaoY = mAlturaTela - mImgBolinha.getHeight();
            mVelocidadeY = -mVelocidadeY * REBOTE;
            rebateuEmY = true;
        }

        if (rebateuEmY && Math.abs(mVelocidadeY) < VELOCIDADE_MINIMA) {
            mVelocidadeY = 0;
        }

        if (mPosicaoX < 0) {
            mPosicaoX = 0;
            mVelocidadeX = -mVelocidadeX * REBOTE;
            rebateuEmX = true;

        } else if (mPosicaoX + mImgBolinha.getWidth() > mLarguraTela) {
            mPosicaoX = mLarguraTela - mImgBolinha.getWidth();
            mVelocidadeX = -mVelocidadeX * REBOTE;
            rebateuEmX = true;
        }

        if (rebateuEmX && Math.abs(mVelocidadeX) < VELOCIDADE_MINIMA) {
            mVelocidadeX = 0;
        }
    }
}

