package dominando.android.ex17_navegacao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SegundoNivelFragment extends Fragment {

    private static final String EXTRA_TEXTO = "texto";
    private static final String EXTRA_COR_BG = "corBg";
    private static final String EXTRA_COR_TEXTO = "corTexto";

    public static SegundoNivelFragment novaInstancia(
            String texto, int background, int textColor) {

        Bundle params = new Bundle();
        params.putString(EXTRA_TEXTO, texto);
        params.putInt(EXTRA_COR_BG, background);
        params.putInt(EXTRA_COR_TEXTO, textColor);

        SegundoNivelFragment snf = new SegundoNivelFragment();
        snf.setArguments(params);
        return snf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle params = getArguments();
        String texto = params.getString(EXTRA_TEXTO);
        int bgColor = params.getInt(EXTRA_COR_BG);
        int textColor = params.getInt(EXTRA_COR_TEXTO);

        View layout = inflater.inflate(
                R.layout.fragment_segundo_nivel, container, false);
        layout.setBackgroundColor(bgColor);

        TextView txt = (TextView) layout.findViewById(R.id.textView);
        txt.setText(texto);
        txt.setTextColor(textColor);

        return layout;
    }
}

