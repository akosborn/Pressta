package me.andrewosborn.pressta.controller;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class HotBrewActivity extends SingleFragmentActivity
{
    private static final String EXTRA_BREW_ID = "me.andrewosborn.pressta.BREW_ID";

    public static Intent newIntent(Context context, int brewId)
    {
        Intent intent = new Intent(context, HotBrewActivity.class);
        intent.putExtra(EXTRA_BREW_ID, brewId);

        return intent;
    }

    @Override
    protected Fragment createFragment()
    {
        int crimeId = getIntent().getIntExtra(EXTRA_BREW_ID, 1);

        return HotBrewFragment.newInstance(crimeId);
    }
}
