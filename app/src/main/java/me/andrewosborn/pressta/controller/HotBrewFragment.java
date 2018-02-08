package me.andrewosborn.pressta.controller;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import me.andrewosborn.pressta.PresstaApplication;
import me.andrewosborn.pressta.R;
import me.andrewosborn.pressta.model.Brew;
import me.andrewosborn.pressta.viewmodel.BrewViewModel;

public class HotBrewFragment extends Fragment
{
    private static final String TAG = "HotBrewFragment";
    public static final int COUNTDOWN_INTERVAL = 150;
    private static final String TIME_REMAINING_KEY = "time_remaining";
    private static final String IS_TIMER_ACTIVE_KEY = "is_timer_active";
    private static final String IS_TIMER_FINISHED_KEY = "is_timer_finished";

    private TextView mTitleTextView;
    private EditText mCoffeeWeightField;
    private EditText mWaterWeightField;
    private ArcProgress mArcProgress;
    private MyTimer mCountDownTimer;
    private EditText mMinRemainingEditText;
    private EditText mSecRemainingEditText;
    private AppCompatSeekBar mRatioSeekbar;
    private TextView mRatioTextView;
    private ImageButton mStartTimerButton;
    private ImageButton mPauseTimerButton;
    private ImageButton mResetTimerButton;
    private Button mSaveButton;
    private Button mEditButton;
    private AnimatorSet animatorSet;
    private int mTimeRemaining;
    private boolean mTimerPaused = false;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private BrewViewModel mBrewViewModel;

    private Brew mBrew;

    public static HotBrewFragment newInstance()
    {
        return new HotBrewFragment();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ((PresstaApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mBrewViewModel = ViewModelProviders.of(this, mViewModelFactory)
                .get(BrewViewModel.class);

        // Assign mBrewViewModel data to mBrew if a configuration change occurred
        LiveData<Brew> brewLiveData = mBrewViewModel.getBrewLiveData();
        if (brewLiveData != null && mBrew == null)
        {
            mBrew = mBrewViewModel.getBrew(Brew.DEFAULT_HOT_BREW_ID).getValue();
        }

        if (savedInstanceState != null)
        {
            mTimeRemaining = savedInstanceState.getInt(TIME_REMAINING_KEY);
        }

        mBrewViewModel.getBrew(Brew.DEFAULT_HOT_BREW_ID).observe(this, new Observer<Brew>()
        {
            @Override
            public void onChanged(@Nullable Brew brew)
            {
                Log.i(TAG, "mBrewViewModel onChanged() called");
                HotBrewFragment.this.mBrew = brew;
                if (mBrew != null)
                {
                    setupUI();
                    if (savedInstanceState != null)
                    {
                        boolean isTimerActive =
                                savedInstanceState.getBoolean(IS_TIMER_ACTIVE_KEY);
                        boolean isTimerFinished =
                                savedInstanceState.getBoolean(IS_TIMER_FINISHED_KEY);

                        if (mTimeRemaining == 0 && !isTimerFinished)
                            mTimeRemaining = mBrew.getBrewDuration();

                        createTimer(mBrew.getBrewDuration(), mTimeRemaining);

                        if (isTimerActive)
                            mStartTimerButton.performClick();
                        else if (isTimerFinished)
                            mCountDownTimer.onFinish();
                        else
                            mTimerPaused = true;
                    }
                    else
                        createTimer(mBrew.getBrewDuration(), mBrew.getBrewDuration());
                }
            }
        });
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

        assignViews(view);
        setListeners();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putInt(TIME_REMAINING_KEY, mTimeRemaining);
        outState.putBoolean(IS_TIMER_ACTIVE_KEY, mCountDownTimer.isActive());
        outState.putBoolean(IS_TIMER_FINISHED_KEY, mCountDownTimer.isFinished());
    }

    private void assignViews(View view)
    {
        mTitleTextView = (TextView) view.findViewById(R.id.text_view_title);
        mCoffeeWeightField = (EditText) view.findViewById(R.id.edit_text_coffee_weight);
        mWaterWeightField = (EditText) view.findViewById(R.id.edit_text_water_weight);
        mArcProgress = (ArcProgress) view.findViewById(R.id.progress_bar_brew_countdown);
        mMinRemainingEditText = (EditText) view.findViewById(R.id.text_view_min_remaining);
        mSecRemainingEditText = (EditText) view.findViewById(R.id.text_view_sec_remaining);
        mRatioTextView = (TextView) view.findViewById(R.id.text_view_seekbar_label);
        mRatioSeekbar = (AppCompatSeekBar) view.findViewById(R.id.seekbar_ratio);
        mStartTimerButton = (ImageButton) view.findViewById(R.id.button_start);
        mPauseTimerButton = (ImageButton) view.findViewById(R.id.button_pause);
        mResetTimerButton = (ImageButton) view.findViewById(R.id.button_reset);
        mSaveButton = (Button) view.findViewById(R.id.button_save);
        mEditButton = (Button) view.findViewById(R.id.button_edit);
    }

    private void setListeners()
    {
        mCoffeeWeightField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {}

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
            {}
        });

        mWaterWeightField.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {}

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
            {}
        });

        mMinRemainingEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    mMinRemainingEditText.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null)
                        imm.hideSoftInputFromWindow(mMinRemainingEditText.getWindowToken(), 0);
                }

                return false;
            }
        });

        mMinRemainingEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count)
            {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count)
            {
                // execute only if changed triggered by user and field not empty
                if (getActivity().getCurrentFocus() == mMinRemainingEditText && !charSequence.toString().equals(""))
                {
                    mCountDownTimer.cancel();
                    int minutes = Integer.parseInt(charSequence.toString());
                    int seconds = Integer.parseInt(String.valueOf(mSecRemainingEditText.getText())) + (minutes * 60);
                    mBrew.setBrewDuration(seconds);
                    configureTimer(mBrew.getBrewDuration());
                    mTimerPaused = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {}
        });

        mSecRemainingEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    mSecRemainingEditText.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null)
                        imm.hideSoftInputFromWindow(mSecRemainingEditText.getWindowToken(), 0);
                }

                return false;
            }
        });

        mSecRemainingEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count)
            {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count)
            {
                // execute only if changed by user and field not empty
                if (getActivity().getCurrentFocus() == mSecRemainingEditText && !charSequence.toString().equals(""))
                {
                    mCountDownTimer.cancel();
                    int minutes = Integer.parseInt(String.valueOf(mMinRemainingEditText.getText()));
                    int seconds = Integer.parseInt(charSequence.toString()) + (minutes * 60);
                    mBrew.setBrewDuration(seconds);
                    configureTimer(mBrew.getBrewDuration());
                    mTimerPaused = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {}
        });

        mRatioSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser)
            {
                // check if triggered by user or code
                if (fromUser)
                {
                    mRatioTextView.setText(getString(R.string.ratio, i + 1));
                    mBrew.setRatio(seekBar.getProgress() + 1);
                } else
                    mRatioTextView.setText(getString(R.string.ratio, i));

                calculate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {}
        });

        mStartTimerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                toggleEditTextInputType();

                if (mTimerPaused)
                {
                    long durationInMillis = (long) (mTimeRemaining * 1000);
                    mCountDownTimer = new MyTimer(durationInMillis, COUNTDOWN_INTERVAL);
                    mTimerPaused = false;
                }

                mStartTimerButton.setVisibility(View.GONE);
                mPauseTimerButton.setVisibility(View.VISIBLE);
                mCountDownTimer.start();
            }
        });

        mPauseTimerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                toggleEditTextInputType();

                mTimerPaused = true;
                mPauseTimerButton.setVisibility(View.GONE);
                mStartTimerButton.setVisibility(View.VISIBLE);
                mCountDownTimer.setActive(false);
                mCountDownTimer.cancel();
            }
        });

        mResetTimerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mCountDownTimer.cancel();
                mArcProgress.setProgress(mArcProgress.getMax());
                mMinRemainingEditText.setText(getString(R.string.minutes,
                        mArcProgress.getProgress() / 60));
                mSecRemainingEditText.setText(getString(R.string.seconds,
                        mArcProgress.getProgress() % 60));
                mTimerPaused = false;
                mPauseTimerButton.setVisibility(View.GONE);
                mStartTimerButton.setVisibility(View.VISIBLE);
                if (animatorSet != null)
                    animatorSet.cancel();
                mMinRemainingEditText.setTextColor(Color.WHITE);
                mSecRemainingEditText.setTextColor(Color.WHITE);
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mSaveButton.setVisibility(View.VISIBLE);
                mEditButton.setVisibility(View.GONE);
                // Create new brew to be edited and saved
                mBrew = new Brew(mBrew.getType(), mBrew.getCoffeeWeight(), mBrew.getRatio(),
                        mBrew.getBrewDuration(), mBrew.getCompletionDate());
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Save new brew to database
                mBrewViewModel.add(mBrew);
                Toast.makeText(getContext(), R.string.toast_brew_saved, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void setupUI()
    {
        mTitleTextView.setText(mBrew.getTitle());
        mRatioSeekbar.setProgress(mBrew.getRatio());
        mCoffeeWeightField.setText(String.valueOf(mBrew.getCoffeeWeight()));
        mWaterWeightField.setText(String.valueOf(mBrew.getWaterWeight()));
    }

    private void toggleEditTextInputType()
    {
        boolean isFocusable = !mMinRemainingEditText.isFocusable();
        // if currently enabled
        if (!isFocusable)
        {
            mMinRemainingEditText.setFocusable(false);
            mSecRemainingEditText.setFocusable(false);
        }
        else
        {
            mMinRemainingEditText.setFocusableInTouchMode(true);
            mSecRemainingEditText.setFocusableInTouchMode(true);
        }
    }

    private void configureTimer(int seconds)
    {
        long milliseconds = seconds * 1000;
        mCountDownTimer = new MyTimer(milliseconds, COUNTDOWN_INTERVAL);
        mArcProgress.setMax(seconds);
        mArcProgress.setProgress(seconds);
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

    class MyTimer extends CountDownTimer
    {
        private int secondsLeft;
        private boolean isActive;
        private boolean isFinished;

        MyTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        boolean isActive()
        {
            return isActive;
        }

        void setActive(boolean isActive)
        {
            this.isActive = isActive;
        }

        public boolean isFinished()
        {
            return isFinished;
        }

        @Override
        public void onTick(long msRemaining)
        {
            Log.i("CountDownTimer", "Tick at " + String.valueOf(msRemaining) + " ms");

            Log.i("CountDownTimerBeforeIf", "msRemaining/1000: " + String.valueOf(Math.round((float)msRemaining/1000.0f) + "; secondsLeft: " + secondsLeft));

            // checks to see if UI update is necessary
            if (Math.round((float) msRemaining / 1000.0f) != secondsLeft)
            {
                Log.i("CountDownTimer", "msRemaining/1000: " + String.valueOf(Math.round((float)msRemaining/1000.0f) + "; secondsLeft: " + secondsLeft));

                secondsLeft = Math.round((float) msRemaining / 1000.0f);
                mMinRemainingEditText.setText(getString(R.string.minutes,secondsLeft / 60));
                mSecRemainingEditText.setText(getString(R.string.seconds,secondsLeft % 60));
                mArcProgress.setProgress(secondsLeft);
            }

            mTimeRemaining = secondsLeft;
            if (!isActive)
                isActive = true;

            Log.i("CountDownTimer", "Progress at " + mArcProgress.getProgress());
        }

        @Override
        public void onFinish()
        {
            mArcProgress.setProgress(0);
            isActive = false;
            isFinished = true;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            {
                List<Animator> objectAnimators = new ArrayList<>();
                animatorSet = new AnimatorSet();

                ObjectAnimator minAnimator = ObjectAnimator.ofArgb(
                        mMinRemainingEditText,
                        "textColor",
                        Color.WHITE,
                        Color.RED,
                        Color.WHITE);
                minAnimator.setDuration(1500);
                minAnimator.setEvaluator(new ArgbEvaluator());
                minAnimator.setRepeatMode(ValueAnimator.REVERSE);
                minAnimator.setRepeatCount(ValueAnimator.INFINITE);
                objectAnimators.add(minAnimator);

                ObjectAnimator secAnimator = ObjectAnimator.ofArgb(
                        mSecRemainingEditText,
                        "textColor",
                        Color.WHITE,
                        Color.RED,
                        Color.WHITE);
                secAnimator.setDuration(1500);
                secAnimator.setEvaluator(new ArgbEvaluator());
                secAnimator.setRepeatMode(ValueAnimator.REVERSE);
                secAnimator.setRepeatCount(ValueAnimator.INFINITE);
                objectAnimators.add(secAnimator);

                animatorSet.playTogether(objectAnimators);
                animatorSet.start();
            }

            mPauseTimerButton.setVisibility(View.GONE);
            mStartTimerButton.setVisibility(View.GONE);
        }
    }

    private void createTimer(int seconds, int progress)
    {
        long durationInMillis = (long) (progress * 1000);

        mArcProgress.setMax(seconds);
        mArcProgress.setProgress(progress);

        mMinRemainingEditText.setText(getString(R.string.minutes,progress / 60));
        mSecRemainingEditText.setText(getString(R.string.seconds,progress % 60));

        mCountDownTimer = new MyTimer(durationInMillis, COUNTDOWN_INTERVAL);
    }
}
