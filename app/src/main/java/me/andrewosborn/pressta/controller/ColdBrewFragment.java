package me.andrewosborn.pressta.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import me.andrewosborn.pressta.R;
import me.andrewosborn.pressta.model.Brew;
import me.andrewosborn.pressta.model.Type;


public class ColdBrewFragment extends Fragment
{
    private EditText mCoffeeWeightField;
    private EditText mWaterWeightField;
    private EditText mColdBrewDurationEditText;
    private TextView mRatioTextView;
    private AppCompatSeekBar mRatioSeekbar;



    private static final Brew mBrew = new Brew(Type.COLD, 23, 8, 720000);

    public static ColdBrewFragment newInstance()
    {
        return new ColdBrewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_cold_brew, container, false);

        mCoffeeWeightField = (EditText) view.findViewById(R.id.edit_text_coffee_weight);
        mCoffeeWeightField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if (!charSequence.toString().equals(""))
                {
                    int coffeeWeight = Integer.parseInt(charSequence.toString());
                    int waterWeight = mBrew.getCalculatedWater(coffeeWeight);
                    mBrew.setCoffeeWeight(coffeeWeight);
                    mBrew.setWaterWeight(waterWeight);

                    if (getActivity().getCurrentFocus() == mCoffeeWeightField)
                        mWaterWeightField.setText(String.valueOf(mBrew.getWaterWeight()));
                } else
                {
                    if (getActivity().getCurrentFocus() == mCoffeeWeightField)
                        mWaterWeightField.getText().clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        mWaterWeightField = (EditText) view.findViewById(R.id.edit_text_water_weight);
        mWaterWeightField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if (!charSequence.toString().equals(""))
                {
                    int waterWeight = Integer.parseInt(charSequence.toString());
                    int coffeeWeight = mBrew.getCalculatedCoffee(waterWeight);
                    mBrew.setWaterWeight(waterWeight);
                    mBrew.setCoffeeWeight(coffeeWeight);

                    if (getActivity().getCurrentFocus() == mWaterWeightField)
                        mCoffeeWeightField.setText(String.valueOf(mBrew.getCoffeeWeight()));
                } else
                {
                    if (getActivity().getCurrentFocus() == mWaterWeightField)
                        mCoffeeWeightField.getText().clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        mColdBrewDurationEditText = (EditText) view.findViewById(R.id.edit_text_cold_brew_duration);
        mColdBrewDurationEditText.setText(String.valueOf((int) mBrew.getBrewDurationSeconds() / 60));
        mColdBrewDurationEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if (!charSequence.toString().equals(""))
                {
                    int hours = Integer.parseInt(charSequence.toString());
                    mBrew.setBrewDurationSeconds(hours);
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        mRatioTextView = (TextView) view.findViewById(R.id.text_view_seekbar_label);

        mRatioSeekbar = (AppCompatSeekBar) view.findViewById(R.id.seekbar_ratio);
        mRatioSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                if (b)
                {
                    mRatioTextView.setText(String.valueOf(i + 1));
                    mBrew.setRatio(seekBar.getProgress() + 1);
                } else
                    mRatioTextView.setText(String.valueOf(i));

                calculate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
        mRatioSeekbar.setProgress(mBrew.getRatio());

        mCoffeeWeightField.setText(String.valueOf(mBrew.getCoffeeWeight()));
        mWaterWeightField.setText(String.valueOf(mBrew.getWaterWeight()));

        return view;
    }

    private void calculate()
    {
        mBrew.setWaterWeight(mBrew.getCalculatedWater(mBrew.getCoffeeWeight()));
        mCoffeeWeightField.setText(String.valueOf(mBrew.getCoffeeWeight()));
        mWaterWeightField.setText(String.valueOf(mBrew.getWaterWeight()));
    }
}
