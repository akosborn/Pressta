package me.andrewosborn.pressta.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


public class MyBrewsPagerAdapter extends FragmentStatePagerAdapter
{
    private final int TAB_COUNT = 2;
    private final int HOT_TAB = 0;

    MyBrewsPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        if (position == HOT_TAB)
            return MyHotBrewsFragment.newInstance();
        else
            return MyColdBrewsFragment.newInstance();
    }

    @Override
    public int getCount()
    {
        return TAB_COUNT;
    }
}
