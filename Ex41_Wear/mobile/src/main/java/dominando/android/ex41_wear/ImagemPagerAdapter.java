package dominando.android.ex41_wear;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ImagemPagerAdapter extends FragmentPagerAdapter {

    public ImagemPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return ImagemFragment.newInstance("foto" + (position + 1) + ".jpg");
    }

    @Override
    public int getCount() {
        return Constantes.TOTAL_IMAGENS;
    }
}

