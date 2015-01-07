package dominando.android.ex38_toques;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DragAndDropActivity extends Activity
        implements View.OnTouchListener, View.OnDragListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop);

        findViewById(R.id.myimage1).setOnTouchListener(this);
        findViewById(R.id.myimage2).setOnTouchListener(this);
        findViewById(R.id.myimage3).setOnTouchListener(this);
        findViewById(R.id.myimage4).setOnTouchListener(this);

        findViewById(R.id.topleft).setOnDragListener(this);
        findViewById(R.id.topright).setOnDragListener(this);
        findViewById(R.id.bottomleft).setOnDragListener(this);
        findViewById(R.id.bottomright).setOnDragListener(this);
    }

    public boolean onTouch(View view, MotionEvent me) {
        int action = me.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        }
        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundResource(R.drawable.bg_hover);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundResource(R.drawable.bg);
                break;
            case DragEvent.ACTION_DROP:
                View view = (View) event.getLocalState();
                ViewGroup owner = (ViewGroup) view.getParent();
                owner.removeView(view);
                LinearLayout container = (LinearLayout) v;
                container.addView(view);
                view.setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackgroundResource(R.drawable.bg);
                View view2 = (View) event.getLocalState();
                view2.setVisibility(View.VISIBLE);
            default:
                break;
        }
        return true;
    }
}
