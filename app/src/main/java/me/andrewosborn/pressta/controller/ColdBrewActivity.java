package me.andrewosborn.pressta.controller;

import android.support.v4.app.Fragment;

import me.andrewosborn.pressta.model.Type;

public class ColdBrewActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return ColdBrewFragment.newInstance();
    }
}
