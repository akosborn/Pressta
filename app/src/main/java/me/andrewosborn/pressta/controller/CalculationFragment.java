package me.andrewosborn.pressta.controller;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import me.andrewosborn.pressta.R;
import me.andrewosborn.pressta.model.Calculation;

public class CalculationFragment extends Fragment
{
    private long durationInMillis;

    private EditText mCoffeeWeightField;
    private EditText mWaterWeightField;
    private ProgressBar mBrewProgressBar;
    private CountDownTimer mCountDownTimer;
    private RelativeLayout mProgressBarLayout;
    private TextView mTimeRemainingTextView;
    private AppCompatSeekBar mRatioSeekbar;
    private TextView mRatioTextView;

    private Calculation mCalculation = new Calculation();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate and get reference to calculation fragment view
        View view = inflater.inflate(R.layout.fragment_calculation, container, false);

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
                    int waterWeight = mCalculation.getCalculatedWater(coffeeWeight);
                    mCalculation.setCoffeeWeight(coffeeWeight);
                    mCalculation.setWaterWeight(waterWeight);

                    if (getActivity().getCurrentFocus() == mCoffeeWeightField)
                        mWaterWeightField.setText(String.valueOf(mCalculation.getWaterWeight()));
                }
                else
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
                    int coffeeWeight = mCalculation.getCalculatedCoffee(waterWeight);
                    mCalculation.setWaterWeight(waterWeight);
                    mCalculation.setCoffeeWeight(coffeeWeight);

                    if (getActivity().getCurrentFocus() == mWaterWeightField)
                        mCoffeeWeightField.setText(String.valueOf(mCalculation.getCoffeeWeight()));
                }
                else
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

        mProgressBarLayout = (RelativeLayout) view.findViewById(R.id.layout_progress_bar);

        mBrewProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_brew_countdown);

        mTimeRemainingTextView = (TextView) view.findViewById(R.id.text_view_time_remaining);
        mTimeRemainingTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                float progressBarLayoutWidth = mProgressBarLayout.getWidth();
                float progressBarLayoutHeight = mProgressBarLayout.getHeight();
                float absoluteCenterPivotX = progressBarLayoutWidth/2;
                float absoluteCenterPivotY = progressBarLayoutHeight/2;
                Animation animation = new RotateAnimation(0.0f, 90.0f,
                        absoluteCenterPivotX, absoluteCenterPivotY);
                animation.setFillAfter(true);
                mBrewProgressBar.startAnimation(animation);
                startTimer();
            }
        });

        setupTimer(4.5f);

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
                    mCalculation.setRatio(seekBar.getProgress() + 1);
                }
                else
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
        mCalculation.setRatio(16);
        mRatioSeekbar.setProgress(mCalculation.getRatio());

        mCalculation.setCoffeeWeight(20);
        mCalculation.setWaterWeight(mCalculation.getCalculatedWater(mCalculation.getCoffeeWeight()));
        mCoffeeWeightField.setText(String.valueOf(mCalculation.getCoffeeWeight()));
        mWaterWeightField.setText(String.valueOf(mCalculation.getWaterWeight()));

        return view;
    }

    private void calculate()
    {
        mCalculation.setWaterWeight(mCalculation.getCalculatedWater(mCalculation.getCoffeeWeight()));
        mCoffeeWeightField.setText(String.valueOf(mCalculation.getCoffeeWeight()));
        mWaterWeightField.setText(String.valueOf(mCalculation.getWaterWeight()));
    }

    private void setupTimer(final float minutes)
    {
        final int durationInSeconds = (int) (minutes * 60);
        durationInMillis = (long) (60 * minutes * 1000);

        mBrewProgressBar.setMax((int) durationInMillis);
        mBrewProgressBar.setProgress((int) durationInMillis);

        mTimeRemainingTextView.setText(getString(R.string.timer_countdown,
                durationInSeconds / 60, durationInSeconds % 60));
    }

    private void startTimer()
    {
        if (mCountDownTimer == null)
        {
            mCountDownTimer = new CountDownTimer(durationInMillis, 100)
            {
                @Override
                public void onTick(long remainingTimeInMilliseconds)
                {
                    int secondsRemaining = (int) remainingTimeInMilliseconds / 1000;
                    mBrewProgressBar.setProgress((int) remainingTimeInMilliseconds);
                    mTimeRemainingTextView.setText(getString(R.string.timer_countdown,
                            secondsRemaining / 60, secondsRemaining % 60));
                }

                @Override
                public void onFinish()
                {
                    mBrewProgressBar.setProgress(0);
                    mTimeRemainingTextView.setText(R.string.reset_timer);
                }
            }.start();
        }
        else
        {
            mCountDownTimer.start();
        }
    }
}
