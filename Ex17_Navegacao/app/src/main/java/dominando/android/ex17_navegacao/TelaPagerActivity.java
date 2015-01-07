package dominando.android.ex17_navegacao;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

public class TelaPagerActivity extends ActionBarActivity {

    AbasPagerAdapter mAbasPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_pager);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        mAbasPagerAdapter = new AbasPagerAdapter(this, getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAbasPagerAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mViewPager.setPageTransformer(true, new ZoomPageTransformer());
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}

