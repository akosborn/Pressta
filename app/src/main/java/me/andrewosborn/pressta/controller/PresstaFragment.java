package me.andrewosborn.pressta.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import me.andrewosborn.pressta.R;
import me.andrewosborn.pressta.model.Brew;
import me.andrewosborn.pressta.model.Type;


public class PresstaFragment extends Fragment
{
    private static final String DEFAULT_HOT_BREW_KEY = "default_hot_brew";
    private static final String DEFAULT_COLD_BREW_KEY = "default_cold_brew";
    private RelativeLayout mQuickBrewRelativeLayout;
    private RelativeLayout mMyBrewsRelativeLayout;
    private TextView mQuickColdBrewTextView;
    private TextView mQuickHotBrewTextView;

    private Intent mIntent;
    private SharedPreferences mSharedPreferences;

    private Type mBrewType = Type.HOT;

    public static Fragment newInstance()
    {
        return new PresstaFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (!mSharedPreferences.contains(DEFAULT_HOT_BREW_KEY))
        {
            editor.putInt(DEFAULT_HOT_BREW_KEY, Brew.DEFAULT_HOT_BREW_ID);
            editor.apply();
        }
        if (!mSharedPreferences.contains(DEFAULT_COLD_BREW_KEY))
        {
            editor.putInt(DEFAULT_COLD_BREW_KEY, Brew.DEFAULT_COLD_BREW_ID);
            editor.apply();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_pressta, container, false);

        mQuickBrewRelativeLayout = (RelativeLayout) view.findViewById(R.id.rel_layout_quick_brew);
        mQuickBrewRelativeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                if (mBrewType.equals(Type.HOT))
                    mIntent = HotBrewActivity.newIntent(getActivity(),
                                    mSharedPreferences.getInt(DEFAULT_HOT_BREW_KEY, 1));
                else if (mBrewType.equals(Type.COLD))
                    mIntent = new Intent(getActivity(), ColdBrewActivity.class);

                startActivity(mIntent);
            }
        });

        mQuickHotBrewTextView = (TextView) view.findViewById(R.id.text_view_quick_hot);
        mQuickHotBrewTextView.setBackgroundColor(getResources().getColor(R.color.opaque_black));
        mQuickHotBrewTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mBrewType = Type.HOT;
                mQuickColdBrewTextView.setBackgroundColor(Color.TRANSPARENT);
                mQuickHotBrewTextView.setBackgroundColor(getResources().getColor(R.color.opaque_black));
            }
        });

        mQuickColdBrewTextView = (TextView) view.findViewById(R.id.text_view_quick_cold);
        mQuickColdBrewTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mBrewType = Type.COLD;
                mQuickHotBrewTextView.setBackgroundColor(Color.TRANSPARENT);
                mQuickColdBrewTextView.setBackgroundColor(getResources().getColor(R.color.opaque_black));
            }
        });

        mMyBrewsRelativeLayout = (RelativeLayout) view.findViewById(R.id.rel_layout_my_brews);
        mMyBrewsRelativeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mIntent = new Intent(getActivity(), MyBrewsActivity.class);
                startActivity(mIntent);
            }
        });

        return view;
    }
}
