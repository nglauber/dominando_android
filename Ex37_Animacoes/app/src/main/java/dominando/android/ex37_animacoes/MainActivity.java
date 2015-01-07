package dominando.android.ex37_animacoes;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

public class MainActivity extends ActionBarActivity
        implements View.OnClickListener, Animation.AnimationListener {

    private static final int DURACAO_ANIMACAO = 1000;

    private Spinner mSpnAnimation;
    private Spinner mSpnInterpolator;
    private Button mButtonPlay;
    private ImageView mImg;
    private Animation[] mAnimacoes;
    private Interpolator[] mInterpolators;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpnAnimation = (Spinner) findViewById(R.id.spnAnimacoes);
        mSpnInterpolator = (Spinner) findViewById(R.id.spnInterpolator);
        mImg = (ImageView) findViewById(R.id.imageView);
        mButtonPlay = (Button) findViewById(R.id.btnPlay);
        mButtonPlay.setOnClickListener(this);

        initInterpolators();
        initAnimacoes();
    }

    @Override
    public void onClick(View v) {
        executarAnimacao();
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        mButtonPlay.setEnabled(true);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    private void initAnimacoes() {
        mAnimacoes = new Animation[mSpnAnimation.getCount()];
        mAnimacoes[0] = new AlphaAnimation(1, 0);
        mAnimacoes[1] = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        mAnimacoes[2] = new ScaleAnimation(
                1, 3, 1, 3,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        mAnimacoes[3] = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.ABSOLUTE, 200,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.ABSOLUTE, 300);

        AnimationSet set = new AnimationSet(true);
        for (int i = 0; i < mAnimacoes.length-2; i++) {
            set.addAnimation(mAnimacoes[i]);
        }
        mAnimacoes[4] = set;


        mAnimacoes = new Animation[mSpnAnimation.getCount()];
        mAnimacoes[0] = AnimationUtils.loadAnimation(this, R.anim.transparencia);
        mAnimacoes[1] = AnimationUtils.loadAnimation(this, R.anim.rotacao);
        mAnimacoes[2] = AnimationUtils.loadAnimation(this, R.anim.escala);
        mAnimacoes[3] = AnimationUtils.loadAnimation(this, R.anim.translacao);
        mAnimacoes[4] = AnimationUtils.loadAnimation(this, R.anim.tudo_junto);

    }

    private void initInterpolators() {
        mInterpolators = new Interpolator[mSpnInterpolator.getCount()];
        mInterpolators[0] = new AccelerateDecelerateInterpolator();
        mInterpolators[1] = new AccelerateInterpolator();
        mInterpolators[2] = new AnticipateInterpolator();
        mInterpolators[3] = new AnticipateOvershootInterpolator();
        mInterpolators[4] = new BounceInterpolator();
        mInterpolators[5] = new CycleInterpolator(2);
        mInterpolators[6] = new DecelerateInterpolator();
        mInterpolators[7] = new LinearInterpolator();
        mInterpolators[8] = new OvershootInterpolator();
    }

    private void executarAnimacao() {
        Interpolator interpolator = mInterpolators[mSpnInterpolator.getSelectedItemPosition()];
        Animation animation = mAnimacoes[mSpnAnimation.getSelectedItemPosition()];
        animation.setInterpolator(interpolator);
        animation.setAnimationListener(this);
        animation.setDuration(DURACAO_ANIMACAO);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(1);
        mImg.requestLayout();
        mImg.setAnimation(animation);
        animation.start();
        mButtonPlay.setEnabled(false);
    }
}

