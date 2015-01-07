package dominando.android.ex41_wear;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.wearable.view.CardFragment;

public class ImagemPagerAdapter extends FragmentPagerAdapter {

    private int mCount;

    public ImagemPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        CardFragment cardFragment = CardFragment.create("Passando Imagens",
                String.format("%d/%d", i + 1, mCount));
        return cardFragment;
    }

    public void setCount(int count) {
        mCount = count;
    }

    @Override
    public int getCount() {
        return mCount;
    }
}

