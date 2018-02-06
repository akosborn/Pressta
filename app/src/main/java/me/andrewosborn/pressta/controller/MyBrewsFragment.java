package me.andrewosborn.pressta.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.andrewosborn.pressta.R;

public class MyBrewsFragment extends Fragment
{
    public static MyBrewsFragment newInstance()
    {
        return new MyBrewsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_my_brews, container, false);

        return view;
    }
}
