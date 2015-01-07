package dominando.android.ex36_multimidia;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DominandoCameraActivity extends Activity
        implements View.OnClickListener {

    private Camera mCamera;
    private VisualizacaoCamera mPreview;
    private MediaRecorder mMediaRecorder;
    private boolean mGravando;
    private boolean mTirouFoto;
    private Button mBtnCapturar;
    private File mCaminhoArquivo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dominando_camera);

        if (cameraDisponivel()) {
            abrirCamera();

            mPreview = new VisualizacaoCamera(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.previewCamera);
            preview.addView(mPreview);
        }

        Uri uri = getIntent().getParcelableExtra(MediaStore.EXTRA_OUTPUT);
        if (uri != null){
            mCaminhoArquivo = new File(uri.getPath());
        }

        mBtnCapturar = (Button) findViewById(R.id.btnCapturar);
        mBtnCapturar.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        liberarMediaRecorder();
        liberarCamera();
        if (mGravando){
            if (mCaminhoArquivo.exists()){
                mCaminhoArquivo.delete();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraDisponivel()) {
            abrirCamera();
        }
    }

    @Override
    public void onClick(View view) {
        String action = getIntent().getAction();
        if (action.equals(MediaStore.ACTION_IMAGE_CAPTURE)){
            tirarFoto();
        } else if (action.equals(MediaStore.ACTION_VIDEO_CAPTURE)){
            gravarVideo();
        }
    }
    // Próximos métodos virão aqui
    private boolean cameraDisponivel() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void abrirCamera() {
        try {
            mCamera = Camera.open();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void liberarMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    private void liberarCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
            mPreview.getHolder().removeCallback(mPreview);
        }
    }

    private void tirarFoto(){
        if (mTirouFoto){
            setResult(RESULT_OK);
            finish();
        } else {
            mCamera.takePicture(null, null, mPicture);
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            if (mCaminhoArquivo != null){
                try {
                    FileOutputStream fos = new FileOutputStream(mCaminhoArquivo);
                    fos.write(data);
                    fos.close();
                    mTirouFoto = true;
                    mBtnCapturar.setText("OK");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void gravarVideo(){
        if (mGravando) {
            concluirGravacao();

        } else {
            if (prepararGravacao()) {
                mMediaRecorder.start();
                mBtnCapturar.setText("Stop");
                mGravando = true;
            } else {
                liberarMediaRecorder();
            }
        }
    }

    private boolean prepararGravacao(){

        abrirCamera();
        mCamera.unlock();

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        mMediaRecorder.setOutputFile(mCaminhoArquivo.toString());
        mMediaRecorder.setMaxDuration(60000); // 1 minuto
        mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    concluirGravacao();
                }
            }
        });

        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            liberarMediaRecorder();
            return false;
        }
        return true;
    }

    private void concluirGravacao(){
        mMediaRecorder.stop();
        liberarMediaRecorder();
        mCamera.lock();

        mGravando = false;

        Intent it = new Intent();
        it.setData(Uri.fromFile(mCaminhoArquivo));
        setResult(RESULT_OK, it);
        finish();
    }

}


