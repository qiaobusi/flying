package com.wjc.flyinghelper;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mob.mobapi.MobAPI;
import com.wjc.flyinghelper.adapter.ViewPagerAdapter;
import com.wjc.flyinghelper.config.Config;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewComponent();
        initMob();
    }

    private void initMob() {
        MobAPI.initSDK(this, Config.mobKey);
    }

    private void initViewComponent() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < viewPagerAdapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(R.layout.tab);
        }

    }


}
