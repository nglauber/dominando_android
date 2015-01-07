package dominando.android.ex21_http;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends ActionBarActivity {

    LivroPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPagerAdapter = new LivroPagerAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(mPagerAdapter);
    }

    class LivroPagerAdapter extends FragmentPagerAdapter {
        LivrosListFragment mList;
        LivrosGridFragment mGrid;

        public LivroPagerAdapter(FragmentManager fm) {
            super(fm);
            mList = new LivrosListFragment();
            mGrid = new LivrosGridFragment();
        }

        @Override
        public Fragment getItem(int position) {
            return (position == 0) ? mList : mGrid;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return (position == 0) ? "Lista" : "Grid";
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
