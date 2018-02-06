package me.andrewosborn.pressta.controller;

import android.support.v4.app.Fragment;

public class MyBrewsActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return MyBrewsFragment.newInstance();
    }
}
