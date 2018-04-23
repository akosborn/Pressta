package me.andrewosborn.pressta.controller;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import me.andrewosborn.pressta.PresstaApplication;
import me.andrewosborn.pressta.R;
import me.andrewosborn.pressta.model.Brew;
import me.andrewosborn.pressta.model.Type;
import me.andrewosborn.pressta.viewmodel.BrewListViewModel;

public class MyHotBrewsFragment extends Fragment
{
    private static final String TAG = "MyBrewsFragment";
    private static final String DEFAULT_HOT_BREW_KEY = "default_hot_brew";
    private static final String PREFERENCES_FILE_KEY = "app_preferences";

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private BrewListViewModel mBrewListViewModel;

    private RecyclerView mHotBrewRecyclerView;
    private HotBrewAdapter mHotBrewAdapter;

    private List<Brew> mHotBrews;
    private SharedPreferences mSharedPreferences;

    private int mDefaultHotBrewId;

    public static MyHotBrewsFragment newInstance()
    {
        return new MyHotBrewsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ((PresstaApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSharedPreferences = getActivity().getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);

        mDefaultHotBrewId = mSharedPreferences.getInt(DEFAULT_HOT_BREW_KEY, Brew.DEFAULT_HOT_BREW_ID);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mBrewListViewModel = ViewModelProviders.of(this, mViewModelFactory)
                .get(BrewListViewModel.class);
        mBrewListViewModel.getBrewsByType(Type.HOT).observe(this, new Observer<List<Brew>>()
        {
            @Override
            public void onChanged(@Nullable List<Brew> brews)
            {
                if (mHotBrews == null)
                {
                    Log.i(TAG, "mBrewListViewModel onChanged() called");
                    mHotBrews = brews;
                    if (mHotBrews != null)
                        setupHotBrewAdapter();
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

        View view = inflater.inflate(R.layout.fragment_my_hot_brews, container, false);

        assignViews(view);
        mHotBrewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private void setupHotBrewAdapter()
    {
        if (mHotBrewAdapter == null)
        {
            mHotBrewAdapter = new HotBrewAdapter(mHotBrews);
            mHotBrewRecyclerView.setAdapter(mHotBrewAdapter);
        } else
        {
            mHotBrewAdapter.setBrews(mHotBrews);
            mHotBrewAdapter.notifyDataSetChanged();
        }
    }

    private void assignViews(View view)
    {
        mHotBrewRecyclerView = view.findViewById(R.id.hot_brew_recycler_view);
    }

    private class HotBrewAdapter extends RecyclerView.Adapter<HotBrewHolder>
    {
        private List<Brew> mBrews;

        HotBrewAdapter(List<Brew> brews)
        {
            mBrews = brews;
        }

        @Override
        public HotBrewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_brew, parent, false);

            return new HotBrewHolder(view);
        }

        @Override
        public void onBindViewHolder(HotBrewHolder holder, int position)
        {
            Brew brew = mBrews.get(position);
            holder.bindBrew(brew);
        }

        @Override
        public int getItemCount()
        {
            return mBrews.size();
        }

        void setBrews(List<Brew> brews)
        {
            mBrews = brews;
        }
    }

    private class HotBrewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener
    {
        private Brew mBrew;

        private TextView mTitleTextView;
        private TextView mHoursTextView;
        private TextView mMinutesTextView;
        private TextView mSecondsTextView;
        private TextView mRatioTextView;
        private TextView mCoffeeWeightTextView;
        private TextView mWaterWeightTextView;
        private LinearLayout mHoursLinLayout;
        private ImageView mStarBorderImgView;
        private ImageView mFilledStarImgView;

        HotBrewHolder(View itemView)
        {
            super(itemView);

            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.text_view_title);
            mHoursTextView = (TextView) itemView.findViewById(R.id.text_view_hours);
            mMinutesTextView = (TextView) itemView.findViewById(R.id.text_view_minutes);
            mSecondsTextView = (TextView) itemView.findViewById(R.id.text_view_seconds);
            mRatioTextView = (TextView) itemView.findViewById(R.id.text_view_ratio);
            mCoffeeWeightTextView = (TextView) itemView.findViewById(R.id.text_view_coffee_weight);
            mWaterWeightTextView = (TextView) itemView.findViewById(R.id.text_view_water_weight);
            mHoursLinLayout = (LinearLayout) itemView.findViewById(R.id.lin_layout_hours);
            mStarBorderImgView = (ImageView) itemView.findViewById(R.id.img_btn_star_border);
            mFilledStarImgView = (ImageView) itemView.findViewById(R.id.img_btn_star);

            mStarBorderImgView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mSharedPreferences.edit().putInt(DEFAULT_HOT_BREW_KEY, mBrew.getId()).apply();
                    mDefaultHotBrewId = mSharedPreferences.getInt(DEFAULT_HOT_BREW_KEY, 1);
                    mHotBrewAdapter.notifyDataSetChanged();
                    // TODO: 2/12/2018 Implement more efficient way to update data set

//                    mStarBorderImgView.setVisibility(View.GONE);
//                    mFilledStarImgView.setVisibility(View.VISIBLE);
                }
            });
            mFilledStarImgView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mHotBrewAdapter.notifyDataSetChanged();
                    // TODO: 2/12/2018 Implement more efficient way to update data set

//                    mFilledStarImgView.setVisibility(View.GONE);
//                    mStarBorderImgView.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onClick(View view)
        {
            Intent intent = HotBrewActivity.newIntent(getContext(), mBrew.getId());
            startActivity(intent);
        }

        void bindBrew(Brew brew)
        {
            mBrew = brew;

            int minutes;
            int seconds;
            int brewDuration = mBrew.getBrewDuration();
            int hours = brewDuration / 3600;

            if (hours > 0)
            {
                minutes = (brewDuration % 3600) / 60;
                seconds = minutes / 60;

                mHoursLinLayout.setVisibility(View.VISIBLE);
                mHoursTextView.setText(getString(R.string.hours, hours));
            }
            else
            {
                minutes = brewDuration / 60;
                seconds = brewDuration % 60;
            }

            mMinutesTextView.setText(getString(R.string.minutes, minutes));
            mSecondsTextView.setText(getString(R.string.seconds, seconds));
            mTitleTextView.setText(brew.getTitle());
            mRatioTextView.setText(getString(R.string.ratio, mBrew.getRatio()));
            mCoffeeWeightTextView.setText(String.valueOf(mBrew.getCoffeeWeight()));
            mWaterWeightTextView.setText(String.valueOf(mBrew.getWaterWeight()));

            if (mBrew.getId() == mDefaultHotBrewId)
            {
                mStarBorderImgView.setVisibility(View.GONE);
                mFilledStarImgView.setVisibility(View.VISIBLE);
            }
            else
            {
                mStarBorderImgView.setVisibility(View.VISIBLE);
                mFilledStarImgView.setVisibility(View.GONE);
            }
        }
    }
}
