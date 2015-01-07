package dominando.android.ex36_multimidia;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class CameraFotoFragment extends Fragment implements
        View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener {

    File mCaminhoFoto;
    ImageView mImageViewFoto;
    CarregarImageTask mTask;
    int mLarguraImagem;
    int mAlturaImagem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        String caminhoFoto = Util.carregarUltimaMidia(getActivity(), Util.MIDIA_FOTO);
        if (caminhoFoto != null) {
            mCaminhoFoto = new File(caminhoFoto);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(
                R.layout.fragment_camera_foto, container, false);
        layout.findViewById(R.id.btnFoto).setOnClickListener(this);
        layout.findViewById(R.id.btnWallpaper).setOnClickListener(this);

        mImageViewFoto = (ImageView) layout.findViewById(R.id.imgFoto);
        layout.getViewTreeObserver().addOnGlobalLayoutListener(this);
        return layout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK
                && requestCode == Util.REQUESTCODE_FOTO) {
            carregarImagem();
        }
    }

    @Override
    public void onGlobalLayout() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
        } else {
            getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        mLarguraImagem = mImageViewFoto.getWidth();
        mAlturaImagem = mImageViewFoto.getHeight();
        carregarImagem();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnFoto) {
            mCaminhoFoto = Util.novaMidia(Util.MIDIA_FOTO);

            Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCaminhoFoto));
            startActivityForResult(it, Util.REQUESTCODE_FOTO);
        } else if (view.getId() == R.id.btnWallpaper) {
            if (mCaminhoFoto != null && mCaminhoFoto.exists()) {
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(Void... voids) {
                        WindowManager windowManager = (WindowManager)
                                getActivity().getSystemService(Context.WINDOW_SERVICE);
                        DisplayMetrics metrics = new DisplayMetrics();
                        windowManager.getDefaultDisplay().getMetrics(metrics);

                        return Util.carregarImagem(mCaminhoFoto,
                                metrics.widthPixels,
                                metrics.heightPixels);
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        if (bitmap != null) {
                            try {
                                WallpaperManager.getInstance(getActivity()).setBitmap(bitmap);
                                Toast.makeText(getActivity(),
                                        "Papel de parede alterado", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.execute();
            }
        }
    }

    private void carregarImagem() {
        if (mCaminhoFoto != null && mCaminhoFoto.exists()) {
            if (mTask == null || mTask.getStatus() != AsyncTask.Status.RUNNING) {
                mTask = new CarregarImageTask();
                mTask.execute();
            }
        }
    }

    class CarregarImageTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(Void... voids) {
            return Util.carregarImagem(
                    mCaminhoFoto,
                    mLarguraImagem,
                    mAlturaImagem);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                mImageViewFoto.setImageBitmap(bitmap);
                Util.salvarUltimaMidia(getActivity(),
                        Util.MIDIA_FOTO, mCaminhoFoto.getAbsolutePath());
            }
        }
    }
}

