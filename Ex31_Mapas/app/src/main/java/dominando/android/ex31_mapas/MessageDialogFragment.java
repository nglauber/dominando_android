package dominando.android.ex31_mapas;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class MessageDialogFragment extends DialogFragment {

    private Dialog mDialog;

    public MessageDialogFragment() {
        super();
        mDialog = null;
        setRetainInstance(true);
    }

    public void setDialog(Dialog dialog) {
        mDialog = dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return mDialog;
    }
}

