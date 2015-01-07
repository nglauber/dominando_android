package dominando.android.ex38_toques;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity {

    ScaleGestureDetector mScaleDetector;
    ImageView mImageView;
    float mEscala;
    int mLarguraOrig;
    int mAlturaOrig;

    ScaleGestureDetector.OnScaleGestureListener mScaleListener =
            new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    mEscala *= detector.getScaleFactor();
                    mEscala = Math.max(1.0f, Math.min(mEscala, 7.0f));

                    ViewGroup.LayoutParams lp = mImageView.getLayoutParams();
                    lp.width = (int) (mLarguraOrig * mEscala);
                    lp.height = (int) (mAlturaOrig * mEscala);
                    mImageView.setLayoutParams(lp);
                    return true;
                }
            };

    ViewTreeObserver.OnGlobalLayoutListener mImageViewGlobalLayoutListener =
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mLarguraOrig = mImageView.getWidth();
                    mAlturaOrig = mImageView.getHeight();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            };

    View.OnTouchListener mImageViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return mScaleDetector.onTouchEvent(motionEvent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScaleDetector = new ScaleGestureDetector(this, mScaleListener);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setOnTouchListener(mImageViewTouchListener);
        mImageView.getViewTreeObserver().addOnGlobalLayoutListener(
                mImageViewGlobalLayoutListener);
    }
}



