package me.andrewosborn.pressta.controller;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import me.andrewosborn.pressta.R;
import me.andrewosborn.pressta.model.Brew;
import me.andrewosborn.pressta.model.Type;

public class BrewFragment extends Fragment
{
    private static final String ARG_BREW_TYPE = "brew_type";

    private EditText mCoffeeWeightField;
    private EditText mWaterWeightField;
    private ProgressBar mBrewProgressBar;
    private CountDownTimer mCountDownTimer;
    private RelativeLayout mProgressBarLayout;
    private TextView mTimeRemainingTextView;
    private AppCompatSeekBar mRatioSeekbar;
    private TextView mRatioTextView;
    private EditText mColdBrewDurationEditText;
    private RelativeLayout mDurationRelLayout;

    private Brew mBrew;
    private Type mBrewType;

    public static BrewFragment newInstance(Type brewType)
    {
        BrewFragment fragment = new BrewFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_BREW_TYPE, brewType);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate and get reference to calculation fragment view
        View view = inflater.inflate(R.layout.fragment_brew, container, false);

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
                    int coffeeWeight = mBrew.getCalculatedCoffee(waterWeight);
                    mBrew.setWaterWeight(waterWeight);
                    mBrew.setCoffeeWeight(coffeeWeight);

                    if (getActivity().getCurrentFocus() == mWaterWeightField)
                        mCoffeeWeightField.setText(String.valueOf(mBrew.getCoffeeWeight()));
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

        mColdBrewDurationEditText = (EditText) view.findViewById(R.id.edit_text_cold_brew_duration);

        mDurationRelLayout = (RelativeLayout) view.findViewById(R.id.rel_layout_duration);

        if (mBrewType.equals(Type.HOT))
            mBrew = new Brew(mBrewType, 23, 16, 4.5f);
        else if (mBrewType.equals(Type.COLD))
        {
            mBrew = new Brew(mBrewType, 23, 8, 720f);
            mBrewProgressBar.setVisibility(View.INVISIBLE);
            mTimeRemainingTextView.setVisibility(View.INVISIBLE);
            mDurationRelLayout.setVisibility(View.VISIBLE);
            mColdBrewDurationEditText.setText(String.valueOf((int) mBrew.getBrewDurationMin() / 60));
        }


        createTimer(mBrew.getBrewDurationMin());
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
                mCountDownTimer.start();
                mBrewProgressBar.startAnimation(animation);
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

        mRatioSeekbar.setProgress(mBrew.getRatio());
        mCoffeeWeightField.setText(String.valueOf(mBrew.getCoffeeWeight()));
        mWaterWeightField.setText(String.valueOf(mBrew.getWaterWeight()));

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

         mBrewType = (Type) getArguments().getSerializable(ARG_BREW_TYPE);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mCountDownTimer.cancel();
    }

    private void calculate()
    {
        mBrew.setWaterWeight(mBrew.getCalculatedWater(mBrew.getCoffeeWeight()));
        mCoffeeWeightField.setText(String.valueOf(mBrew.getCoffeeWeight()));
        mWaterWeightField.setText(String.valueOf(mBrew.getWaterWeight()));
    }

    private void createTimer(float minutes)
    {
        final int durationInSeconds = (int) (minutes * 60);
        long durationInMillis = (long) (60 * minutes * 1000);

        mBrewProgressBar.setMax(durationInSeconds);
        mBrewProgressBar.setProgress(durationInSeconds);

        mTimeRemainingTextView.setText(getString(R.string.timer_countdown,
                durationInSeconds / 60, durationInSeconds % 60));

        mCountDownTimer = new CountDownTimer(durationInMillis, 500)
        {
            int secondsLeft = 0;

            @Override
            public void onTick(long msRemaining)
            {
                Log.i("CountDownTimer", "Tick at " + String.valueOf(msRemaining) + " ms");

                // checks to see if UI update is necessary
                if (Math.round((float) msRemaining / 1000.0f) != secondsLeft)
                {
                    Log.i("CountDownTimer", "msRemaining/1000: " + String.valueOf(Math.round((float)msRemaining/1000.0f) + "; secondsLeft: " + secondsLeft));

                    secondsLeft = Math.round((float) msRemaining / 1000.0f);
                    mTimeRemainingTextView.setText(getString(R.string.timer_countdown,
                            secondsLeft / 60, secondsLeft % 60));
                    mBrewProgressBar.setProgress(secondsLeft);
                }

                Log.i("CountDownTimer", "Progress at " + mBrewProgressBar.getProgress());
            }

            @Override
            public void onFinish()
            {
                mBrewProgressBar.setProgress(0);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
                {
                    ObjectAnimator animator = ObjectAnimator.ofArgb(
                            mTimeRemainingTextView,
                            "textColor",
                            Color.WHITE,
                            Color.RED,
                            Color.WHITE);
                    animator.setDuration(1500);
                    animator.setEvaluator(new ArgbEvaluator());
                    animator.setRepeatMode(ValueAnimator.REVERSE);
                    animator.setRepeatCount(ValueAnimator.INFINITE);
                    animator.start();
                }
            }
        };
    }
}
