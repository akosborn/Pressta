package me.andrewosborn.pressta.controller;

import android.support.v4.app.Fragment;

public class CalculationActivity extends SingleFragmentActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new CalculationFragment();
    }
}
