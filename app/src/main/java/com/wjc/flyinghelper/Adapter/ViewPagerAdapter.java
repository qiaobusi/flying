package com.wjc.flyinghelper.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wjc.flyinghelper.LifeFragment;
import com.wjc.flyinghelper.R;
import com.wjc.flyinghelper.SleepFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private int[] titles = {R.string.tab_sleep, R.string.tab_life};

    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new SleepFragment();
        } else if (position == 1) {
            fragment = new LifeFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(titles[position]);
    }
}
