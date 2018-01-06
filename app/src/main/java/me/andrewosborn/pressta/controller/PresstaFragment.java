package me.andrewosborn.pressta.controller;

import android.content.Intent;
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


public class PresstaFragment extends Fragment
{
    private RelativeLayout mQuickBrewRelativeLayout;
    private TextView mQuickColdBrewTextView;
    private TextView mQuickHotBrewTextView;

    public static Fragment newInstance()
    {
        return new PresstaFragment();
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
                Intent intent = new Intent(getActivity(), CalculationActivity.class);
                startActivity(intent);
            }
        });

        mQuickHotBrewTextView = (TextView) view.findViewById(R.id.text_view_quick_hot);
        mQuickHotBrewTextView.setBackgroundColor(getResources().getColor(R.color.opaque_black));
        mQuickHotBrewTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
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
                mQuickHotBrewTextView.setBackgroundColor(Color.TRANSPARENT);
                mQuickColdBrewTextView.setBackgroundColor(getResources().getColor(R.color.opaque_black));
            }
        });

        return view;
    }
}
