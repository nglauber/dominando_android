package dominando.android.ex37_animacoes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Animacoes3Activity extends ActionBarActivity {

    private FrameLayout mFrame;
    private ImageView mImg;
    private boolean mReverterEscala;
    private boolean mReverterAlpha;
    private Animacoes mAnimacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animacoes3);
        mFrame = (FrameLayout) findViewById(R.id.frame);
        mImg = (ImageView) findViewById(R.id.imageView);
        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executarAnimacao();
            }
        });

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.HONEYCOMB_MR1){
            mAnimacoes = new Animacao3();
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
            mAnimacoes = new Animacao3Plus();
        } else {
            Toast.makeText(this, "Animações não vão funcionar", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void executarAnimacao() {
        int numeroAnimacoes = 6;
        int animacao = (int) (Math.random() * numeroAnimacoes);
        switch (animacao) {
            case 0: mAnimacoes.girar();      break;
            case 1: mAnimacoes.girarEmX();   break;
            case 2: mAnimacoes.girarEmY();   break;
            case 3: mAnimacoes.escala();     break;
            case 4: mAnimacoes.opacidade();  break;
            case 5: mAnimacoes.movimentar(); break;
        }
    }

    private AnimatorListenerAdapter mListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            mImg.setEnabled(false);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mImg.setEnabled(true);
        }
    };
    // O restante do código virá aqui...

    interface Animacoes {
        void girar();
        void girarEmX();
        void girarEmY();
        void escala();
        void opacidade();
        void movimentar();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class Animacao3 implements Animacoes {

        @Override
        public void girar() {
            ObjectAnimator animator =
                    ObjectAnimator.ofFloat(mImg, "rotation", 0, 360);
            animator.addListener(mListener);
            animator.start();
        }

        @Override
        public void girarEmX() {
            ObjectAnimator animator =
                    ObjectAnimator.ofFloat(mImg, "rotationX", 0, 360);
            animator.addListener(mListener);
            animator.start();
        }

        @Override
        public void girarEmY() {
            ObjectAnimator animator =
                    ObjectAnimator.ofFloat(mImg, "rotationY", 0, 360);
            animator.addListener(mListener);
            animator.start();
        }

        @Override
        public void escala() {
            float novoX = mReverterEscala ? 1.0f : 1.5f;
            float novoY = mReverterEscala ? 1.0f : 1.5f;

            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(mImg, "scaleX", novoX),
                    ObjectAnimator.ofFloat(mImg, "scaleY", novoY));
            set.addListener(mListener);
            mReverterEscala = !mReverterEscala;
        }

        @Override
        public void opacidade() {
            ObjectAnimator animator =
                    ObjectAnimator.ofFloat(mImg, "alpha", mReverterAlpha ? 1.0f : 0.5f);
            animator.addListener(mListener);
            animator.start();
            mReverterAlpha = !mReverterAlpha;
        }

        @Override
        public void movimentar() {
            float novoX = (float) (Math.random() * (mFrame.getWidth() - mImg.getWidth()));
            float novoY = (float) (Math.random() * (mFrame.getHeight() - mImg.getHeight()));

            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(mImg, "x", novoX),
                    ObjectAnimator.ofFloat(mImg, "y", novoY));
            set.addListener(mListener);
            set.start();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    class Animacao3Plus implements Animacoes {

        private ViewPropertyAnimator getAnimator() {
            return mImg.animate().setListener(mListener);
        }

        @Override
        public void girar() {
            getAnimator().rotationBy(360);
        }

        @Override
        public void girarEmX() {
            getAnimator().rotationXBy(360);
        }

        @Override
        public void girarEmY() {
            getAnimator().rotationYBy(360);
        }

        @Override
        public void escala() {
            getAnimator().alpha(mReverterAlpha ? 1.0f : 0.5f);
            mReverterAlpha = !mReverterAlpha;
        }

        @Override
        public void opacidade() {
            float novoX = mReverterEscala ? 1.0f : 1.5f;
            float novoY = mReverterEscala ? 1.0f : 1.5f;

            getAnimator().scaleX(novoX).scaleY(novoY);
            mReverterEscala = !mReverterEscala;
        }

        @Override
        public void movimentar() {
            float novoX = (float) (Math.random() * (mFrame.getWidth() - mImg.getWidth()));
            float novoY = (float) (Math.random() * (mFrame.getHeight() - mImg.getHeight()));

            getAnimator().x(novoX).y(novoY);
        }
    }

}

