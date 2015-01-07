package dominando.android.hotel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GcmHelper {

    public static final int REQUEST_CODE_GOOGLEPLAY = 100;
    public static final String PROP_REG_ID = "registration_id";
    private static final String PROP_APP_VERSION = "appVersion";
    private static final String SENDER_ID = "SEU_SENDER_ID_AQUI";
    private static final String IP = "ENDERECO_DA_SUA_MAQUINA_NA_REDE_LOCAL_AQUI";

    public static boolean googlePlayServicesEstaDisponivel(Activity activity) {

        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {

                GooglePlayServicesUtil.getErrorDialog(
                        resultCode, activity, REQUEST_CODE_GOOGLEPLAY).show();
                return true;
            }
            return false;
        }
        return true;
    }

    public static void registrar(Context ctx, AoRegistrarDispositivo listener){
        String regId = lerRegistrationId(ctx);

        if ("".equals(regId)) {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(ctx);
            registrarEmBackground(ctx, gcm, listener);

        } else {
            listener.aoRegistrar(regId, false);
        }
    }

    private static String lerRegistrationId(Context context) {

        final SharedPreferences prefs =  getGCMPreferences(context);
        String registrationId = prefs.getString(PROP_REG_ID, "");
        int registeredVersion = prefs.getInt(PROP_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);

        if ("".equals(registrationId) || registeredVersion != currentVersion) {
            return "";
        }
        return registrationId;
    }

    private static void salvarRegistrationId(Context context, String regId) {

        final SharedPreferences prefs = getGCMPreferences(context);

        int appVersion = getAppVersion(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROP_REG_ID, regId);
        editor.putInt(PROP_APP_VERSION, appVersion);
        editor.commit();
    }

    private static void registrarEmBackground(final Context ctx,
                                              final GoogleCloudMessaging gcm,
                                              final AoRegistrarDispositivo listener) {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                String regid = null;
                try {
                    regid = gcm.register(SENDER_ID);

                    enviarRegistrationIdParaServidor(regid);

                    salvarRegistrationId(ctx, regid);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return regid;
            }

            @Override
            protected void onPostExecute(String msg) {
                listener.aoRegistrar(msg, true);
            }
        }.execute();
    }

    private static void enviarRegistrationIdParaServidor(String key) throws IOException {

        URL url = new URL("http://"+ IP +"/hotel_service/gcmserver.php");

        HttpURLConnection conexao = (HttpURLConnection)url.openConnection();
        conexao.setRequestMethod("POST");
        conexao.setDoOutput(true);

        OutputStream os = conexao.getOutputStream();
        os.write(("acao=registrar&regId="+key).getBytes());
        os.flush();
        os.close();
        conexao.connect();
        int responseCode = conexao.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK){
            throw new RuntimeException("Erro ao salvar no servidor");
        }
    }

    private static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences("GCM", Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public interface AoRegistrarDispositivo {
        void aoRegistrar(String regId, boolean emSegundoPlano);
    }
}

