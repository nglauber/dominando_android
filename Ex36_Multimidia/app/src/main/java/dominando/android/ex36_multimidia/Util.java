package dominando.android.ex36_multimidia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateFormat;

import java.io.File;
import java.util.Date;

public abstract class Util {

    public static final int MIDIA_FOTO = 0;
    public static final int MIDIA_VIDEO = 1;
    public static final int MIDIA_AUDIO = 2;

    public static final int REQUESTCODE_FOTO = 1;
    public static final int REQUESTCODE_VIDEO = 2;
    public static final int REQUESTCODE_AUDIO = 3;

    private static final String ULTIMA_FOTO = "ultima_foto";
    private static final String ULTIMO_VIDEO = "ultima_video";
    private static final String ULTIMO_AUDIO = "ultimo_audio";

    private static final String PREFERENCIA_MIDIA = "midia_prefs";
    private static final String PASTA_MIDIA = "Dominando_Android";

    private static final String[] EXTENSOES =
            new String[]{".jpg", ".mp4", ".3gp" };

    private static final String[] CHAVES_PREF = new String[]{
            ULTIMA_FOTO, ULTIMO_VIDEO, ULTIMO_AUDIO };

    public static File novaMidia(int tipo){
        String nomeMidia = DateFormat.format(
                "yyyy-MM-dd_hhmmss", new Date()).toString();

        File dirMidia = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                PASTA_MIDIA);
        if (!dirMidia.exists()) {
            dirMidia.mkdirs();
        }
        return new File(dirMidia, nomeMidia + EXTENSOES[tipo]);
    }

    public static void salvarUltimaMidia(Context ctx, int tipo, String midia) {
        SharedPreferences sharedPreferences =
                ctx.getSharedPreferences(PREFERENCIA_MIDIA,Context.MODE_PRIVATE);

        sharedPreferences.edit()
                .putString(CHAVES_PREF[tipo], midia)
                .commit();

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.parse(midia);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
    }

    public static String carregarUltimaMidia(Context ctx, int tipo) {
        return ctx.getSharedPreferences(PREFERENCIA_MIDIA, Context.MODE_PRIVATE)
                .getString(CHAVES_PREF[tipo], null);
    }

    public static Bitmap carregarImagem(File imagem,  int largura, int altura){
        if (largura == 0 || altura == 0) return null;

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagem.getAbsolutePath(), bmOptions);

        int larguraFoto = bmOptions.outWidth;
        int alturaFoto = bmOptions.outHeight;
        int escala = Math.min(
                larguraFoto / largura,
                alturaFoto / altura);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = escala;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(
                imagem.getAbsolutePath(), bmOptions);
        return bitmap;
    }
}

