package dominando.android.ex39_sensores;

import java.util.concurrent.TimeUnit;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;

public class ThreadJogo extends Thread {
    private boolean mExecutando;
    private Bolinha mBolinha;
    private SurfaceHolder mHolder;
    private Bitmap mImgBackground;

    public ThreadJogo(Context context, Bolinha bolinha, SurfaceHolder holder){
        mBolinha = bolinha;
        mHolder = holder;
        mImgBackground = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.grass);
    }

    public void run() {
        mExecutando = true;
        while (mExecutando) {
            try {
                mBolinha.calcularFisica();
                pintar();
                TimeUnit.MILLISECONDS.sleep(16);
            } catch (InterruptedException ie) {
                mExecutando = false;
            }
        }
    }

    public void parar() {
        mExecutando = false;
        interrupt();
    }

    private void pintar() {
        Canvas c = null;
        try {
            c = mHolder.lockCanvas();

            if (c != null) {
                Rect rect = new Rect(0, 0, c.getWidth(), c.getHeight());
                c.drawBitmap(mImgBackground, null, rect, null);
                c.drawBitmap(mBolinha.getImagemBolinha(),
                        mBolinha.getX(), mBolinha.getY(), null);
            }

        } finally {
            if (c != null) {
                mHolder.unlockCanvasAndPost(c);
            }
        }
    }
}
