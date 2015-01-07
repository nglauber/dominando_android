package dominando.android.ex36_multimidia;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;

public class VisualizacaoCamera extends SurfaceView implements SurfaceHolder.Callback {
    private Camera mCamera;

    public VisualizacaoCamera(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        getHolder().addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (holder.getSurface() == null){
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            e.printStackTrace();
        }
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

