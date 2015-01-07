package dominando.android.ex17_navegacao;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public class TelaAbasActivity extends ActionBarActivity {

    ViewPager mViewPager;
    SlidingTabLayout mSlidingTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_abas);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        AbasPagerAdapter pagerAdapter = new AbasPagerAdapter(this, getSupportFragmentManager());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(pagerAdapter);

        mSlidingTabLayout = (SlidingTabLayout)findViewById(R.id.tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}

