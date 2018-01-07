package me.andrewosborn.pressta.controller;

import android.support.v4.app.Fragment;

import me.andrewosborn.pressta.model.Type;

public class BrewActivity extends SingleFragmentActivity
{
    private static final String EXTRA_BREW_TYPE = "me.andrewosborn.extra.brew_type";

    @Override
    protected Fragment createFragment()
    {
        Type brewType = (Type) getIntent()
                .getSerializableExtra(EXTRA_BREW_TYPE);

        return BrewFragment.newInstance(brewType);
    }
}
