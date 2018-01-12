package me.andrewosborn.pressta.controller;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;

import me.andrewosborn.pressta.R;
import me.andrewosborn.pressta.model.Brew;
import me.andrewosborn.pressta.model.Type;

public class HotBrewFragment extends Fragment
{
    private EditText mCoffeeWeightField;
    private EditText mWaterWeightField;
    private ArcProgress mArcProgress;
    private CountDownTimer mCountDownTimer;
    private ConstraintLayout mProgressBarLayout;
    private TextView mTimeRemainingTextView;
    private AppCompatSeekBar mRatioSeekbar;
    private TextView mRatioTextView;

    private static final Brew mBrew = new Brew(Type.HOT, 23, 16, 4.5f);

    public static HotBrewFragment newInstance()
    {
        return new HotBrewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate and get reference to calculation fragment view
        View view = inflater.inflate(R.layout.fragment_hot_brew, container, false);

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

        mProgressBarLayout = (ConstraintLayout) view.findViewById(R.id.con_layout_countdown);
        mArcProgress = (ArcProgress) view.findViewById(R.id.progress_bar_brew_countdown);
        mTimeRemainingTextView = (TextView) view.findViewById(R.id.text_view_time_remaining);

        createTimer(mBrew.getBrewDurationMin());
        mTimeRemainingTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mCountDownTimer.start();
            }
        });

        mArcProgress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mCountDownTimer.start();
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

        mArcProgress.setMax(durationInSeconds);
        mArcProgress.setProgress(durationInSeconds);

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
                    mArcProgress.setProgress(secondsLeft);
                }

                Log.i("CountDownTimer", "Progress at " + mArcProgress.getProgress());
            }

            @Override
            public void onFinish()
            {
                mArcProgress.setProgress(0);

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
