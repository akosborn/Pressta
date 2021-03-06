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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import me.andrewosborn.pressta.PresstaApplication;
import me.andrewosborn.pressta.R;
import me.andrewosborn.pressta.model.Brew;
import me.andrewosborn.pressta.util.TimerStatus;
import me.andrewosborn.pressta.viewmodel.BrewViewModel;

public class HotBrewFragment extends Fragment
{
    private static final String TAG = "HotBrewFragment";
    public static final int COUNTDOWN_INTERVAL = 150;
    private static final String TIME_REMAINING_KEY = "time_remaining";
    private static final String TIMER_STATUS_KEY = "timer_status_key";
    private static final SimpleDateFormat sDateFormat =
            new SimpleDateFormat("MMM d, h:mm a", Locale.US);
    private static final String ARG_BREW_ID = "brew_id";

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
    private Button mNewButton;
    private AnimatorSet animatorSet;
    private int mTimeRemaining;
    private TimerStatus mTimerStatus;


    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private BrewViewModel mBrewViewModel;

    private Brew mBrew;

    public static HotBrewFragment newInstance(int brewId)
    {
        Bundle args = new Bundle();
        args.putInt(ARG_BREW_ID, brewId);

        HotBrewFragment fragment = new HotBrewFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        int brewId = getArguments().getInt(ARG_BREW_ID);

        ((PresstaApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mBrewViewModel = ViewModelProviders.of(this, mViewModelFactory)
                .get(BrewViewModel.class);

        // Assign mBrewViewModel data to mBrew if a configuration change occurred
        LiveData<Brew> brewLiveData = mBrewViewModel.getBrewLiveData();
        if (brewLiveData != null && mBrew == null)
        {
            mBrew = mBrewViewModel.getBrew(brewId).getValue();
        }

        if (savedInstanceState != null)
        {
            mTimeRemaining = savedInstanceState.getInt(TIME_REMAINING_KEY);
        }

        mBrewViewModel.getBrew(brewId).observe(this, new Observer<Brew>()
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
                        if ((mTimerStatus = (TimerStatus) savedInstanceState.getSerializable(TIMER_STATUS_KEY)) != null)
                        {
                            int initMillis = 0;
                            switch (mTimerStatus)
                            {
                                case NEW:
                                    mCountDownTimer =
                                            new MyTimer(initMillis = mBrew.getBrewDuration() * 1000, COUNTDOWN_INTERVAL);
                                    break;

                                case FINISHED:
                                    mCountDownTimer =
                                            new MyTimer(initMillis = mBrew.getBrewDuration() * 1000, COUNTDOWN_INTERVAL);
                                    mCountDownTimer.onFinish();

                                    break;
                                case PAUSED:
                                    mCountDownTimer =
                                            new MyTimer(initMillis = mTimeRemaining * 1000, COUNTDOWN_INTERVAL, TimerStatus.PAUSED);
                                    break;

                                case RUNNING:
                                    mCountDownTimer =
                                            new MyTimer(initMillis = mTimeRemaining * 1000, COUNTDOWN_INTERVAL, TimerStatus.RUNNING);
                                    mCountDownTimer.start();
                                    break;
                            }

                            setupTimerUI(mBrew.getBrewDuration(), initMillis / 1000);
                        }
                    }
                    else
                    {
                        int durationSec = mBrew.getBrewDuration();
                        int durationMillis = durationSec * 1000;
                        mCountDownTimer = new MyTimer(durationMillis, COUNTDOWN_INTERVAL);
                        setupTimerUI(durationSec, durationSec);
                    }
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
        outState.putSerializable(TIMER_STATUS_KEY, mCountDownTimer.getStatus());
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
        mNewButton = (Button) view.findViewById(R.id.button_new);
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
                mPauseTimerButton.setVisibility(View.GONE);
                mStartTimerButton.setVisibility(View.VISIBLE);

                mCountDownTimer.pause();
            }
        });

        mResetTimerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mCountDownTimer.cancel();
                mCountDownTimer = new MyTimer(mBrew.getBrewDuration() * 1000, COUNTDOWN_INTERVAL);

                mArcProgress.setProgress(mArcProgress.getMax());
                mMinRemainingEditText.setText(getString(R.string.minutes,
                        mArcProgress.getProgress() / 60));
                mSecRemainingEditText.setText(getString(R.string.seconds,
                        mArcProgress.getProgress() % 60));
                mPauseTimerButton.setVisibility(View.GONE);
                mStartTimerButton.setVisibility(View.VISIBLE);
                if (animatorSet != null)
                    animatorSet.cancel();
                mMinRemainingEditText.setTextColor(Color.WHITE);
                mSecRemainingEditText.setTextColor(Color.WHITE);
            }
        });

        mNewButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mSaveButton.setVisibility(View.VISIBLE);
                // Create new brew to be edited and saved
                mBrew = new Brew(mBrew.getType(), mBrew.getCoffeeWeight(), mBrew.getRatio(),
                        mBrew.getBrewDuration(), mBrew.getCompletionDate());
                mTitleTextView.setText(mBrew.getTitle());
            }
        });

        mEditButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mSaveButton.setVisibility(View.VISIBLE);
                mEditButton.setVisibility(View.GONE);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // TODO: 2/9/2018 Handle saving more than one brew (Brew.id unique conflict)
                if (mBrew.getId() != null)
                    mBrew.setId(null);
                if (mBrew.getTitle() == null)
                {
                    Date date = new Date(System.currentTimeMillis());
                    mBrew.setTitle(sDateFormat.format(date));
                }
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
        private TimerStatus status;

        MyTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
            this.status = TimerStatus.NEW;
        }

        MyTimer(long millisInFuture, long countdownInterval, TimerStatus status)
        {
            super(millisInFuture, countdownInterval);
            this.status = status;
        }

        void pause()
        {
            this.cancel();
            mCountDownTimer = new MyTimer(secondsLeft * 1000, COUNTDOWN_INTERVAL, TimerStatus.PAUSED);
        }

        @Override
        public void onTick(long msRemaining)
        {
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
            if (!status.equals(TimerStatus.RUNNING))
                status = TimerStatus.RUNNING;
        }

        @Override
        public void onFinish()
        {
            mArcProgress.setProgress(0);
            status = TimerStatus.FINISHED;

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

        TimerStatus getStatus()
        {
            return status;
        }
    }

    private void setupTimerUI(int max, int progress)
    {
        mArcProgress.setMax(max);
        mArcProgress.setProgress(progress);

        mMinRemainingEditText.setText(getString(R.string.minutes,progress / 60));
        mSecRemainingEditText.setText(getString(R.string.seconds,progress % 60));
    }
}
