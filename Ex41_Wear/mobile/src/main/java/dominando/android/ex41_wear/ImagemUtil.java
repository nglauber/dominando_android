package dominando.android.ex41_wear;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImagemUtil {
    public static Bitmap imagem(Context ctx, String assetPath,  int largura, int altura){
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(ctx.getAssets().open(assetPath), null, bmOptions);

            int larguraFoto = bmOptions.outWidth;
            int alturaFoto = bmOptions.outHeight;
            int escala = Math.min(larguraFoto / largura, alturaFoto / altura);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = escala;
            bmOptions.inPurgeable = true;

            bitmap = BitmapFactory.decodeStream(
                    ctx.getAssets().open(assetPath), null, bmOptions);

        } catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
}

