package com.wjc.flyinghelper.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.wjc.flyinghelper.fragment.CarFragment;
import com.wjc.flyinghelper.fragment.LifeFragment;
import com.wjc.flyinghelper.R;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private int[] titles = {R.string.tab_life, R.string.tab_car};

    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0) {
            fragment = new LifeFragment();
        } else if (position == 1) {
            fragment = new CarFragment();
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
