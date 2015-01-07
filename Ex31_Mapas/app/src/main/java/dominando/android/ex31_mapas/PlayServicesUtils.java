package dominando.android.ex31_mapas;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class PlayServicesUtils {

    public final static int REQUEST_CODE_ERRO_PLAY_SERVICES = 9000;

    public static boolean googlePlayServicesDisponivel(FragmentActivity activity) {
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            exibirMensagemDeErro(activity, resultCode);
            return false;
        }
    }

    public static void exibirMensagemDeErro(FragmentActivity activity, int codigoDoErro){
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                codigoDoErro, activity, REQUEST_CODE_ERRO_PLAY_SERVICES);

        if (errorDialog != null) {
            MessageDialogFragment errorFragment = new MessageDialogFragment();
            errorFragment.setDialog(errorDialog);
            errorFragment.show(activity.getSupportFragmentManager(),
                    "DIALOG_ERRO_PLAY_SERVICES");
        }
    }
}

