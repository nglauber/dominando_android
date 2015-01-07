package dominando.android.ex17_navegacao;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.Locale;

public class AbasPagerAdapter extends FragmentPagerAdapter {

    String[] titulosAbas;
    TypedArray bgColors;
    TypedArray textColors;

    public AbasPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        titulosAbas = ctx.getResources().getStringArray(R.array.secoes);
        bgColors = ctx.getResources().obtainTypedArray(R.array.cores_bg);
        textColors = ctx.getResources().obtainTypedArray(R.array.cores_texto);
    }

    @Override
    public Fragment getItem(int position) {
        SegundoNivelFragment fragment =
                SegundoNivelFragment.novaInstancia(
                        titulosAbas[position],
                        bgColors.getColor(position, 0),
                        textColors.getColor(position, 0));
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        return titulosAbas[position].toUpperCase(l);
    }
}

