package me.andrewosborn.pressta.controller;

import android.support.v4.app.Fragment;

public class HotBrewActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return HotBrewFragment.newInstance();
    }
}
