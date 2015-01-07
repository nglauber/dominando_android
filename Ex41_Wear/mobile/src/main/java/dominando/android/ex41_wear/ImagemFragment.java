package dominando.android.ex41_wear;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImagemFragment extends Fragment {
    private static final String EXTRA_IMAGEM = "imagem";

    public static ImagemFragment newInstance(String imagem) {
        ImagemFragment fragment = new ImagemFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_IMAGEM, imagem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String imagem = getArguments().getString(EXTRA_IMAGEM);
        ImageView imgView = new ImageView(getActivity());
        imgView.setImageBitmap(ImagemUtil.imagem(getActivity(), imagem, 800, 600));
        return imgView;
    }
}
