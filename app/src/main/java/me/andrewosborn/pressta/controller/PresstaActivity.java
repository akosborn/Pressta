package me.andrewosborn.pressta.controller;

import android.support.v4.app.Fragment;

public class PresstaActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return PresstaFragment.newInstance();
    }
}
