package me.andrewosborn.pressta.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import me.andrewosborn.pressta.R;

public class MyBrewsActivity extends FragmentActivity
{
    private MyBrewsPagerAdapter mMyBrewsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my_brews_view_pager);

        mMyBrewsPagerAdapter = new MyBrewsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.view_pager_my_brews);
        mViewPager.setAdapter(mMyBrewsPagerAdapter);
    }
}
