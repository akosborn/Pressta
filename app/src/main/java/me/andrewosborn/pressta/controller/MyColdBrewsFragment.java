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

public class MyColdBrewsFragment extends Fragment
{
    private static final String TAG = "MyBrewsFragment";
    private static final String DEFAULT_COLD_BREW_KEY = "default_cold_brew";
    private static final String PREFERENCES_FILE_KEY = "app_preferences";

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private BrewListViewModel mBrewListViewModel;

    private RecyclerView mColdBrewRecyclerView;
    private ColdBrewAdapter mColdBrewAdapter;

    private List<Brew> mColdBrews;
    private SharedPreferences mSharedPreferences;

    private int mDefaultColdBrewId;

    public static MyColdBrewsFragment newInstance()
    {
        return new MyColdBrewsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ((PresstaApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSharedPreferences = getActivity().getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE);

        mDefaultColdBrewId = mSharedPreferences.getInt(DEFAULT_COLD_BREW_KEY, Brew.DEFAULT_COLD_BREW_ID);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mBrewListViewModel = ViewModelProviders.of(this, mViewModelFactory)
                .get(BrewListViewModel.class);

        mBrewListViewModel.getBrewsByType(Type.COLD).observe(this, new Observer<List<Brew>>()
        {
            @Override
            public void onChanged(@Nullable List<Brew> brews)
            {
                if (mColdBrews == null)
                {
                    Log.i(TAG, "mBrewListViewModel onChanged() called");
                    mColdBrews = brews;
                    if (mColdBrews != null)
                        setupColdBrewAdapter();
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

        View view = inflater.inflate(R.layout.fragment_my_cold_brews, container, false);

        assignViews(view);
        mColdBrewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    private void setupColdBrewAdapter()
    {
        if (mColdBrewAdapter == null)
        {
            mColdBrewAdapter = new ColdBrewAdapter(mColdBrews);
            mColdBrewRecyclerView.setAdapter(mColdBrewAdapter);
        }
        else
        {
            mColdBrewAdapter.setBrews(mColdBrews);
            mColdBrewAdapter.notifyDataSetChanged();
        }
    }

    private void assignViews(View view)
    {
        mColdBrewRecyclerView = view.findViewById(R.id.cold_brew_recycler_view);
    }

    private class ColdBrewAdapter extends RecyclerView.Adapter<ColdBrewHolder>
    {
        private List<Brew> mColdBrews;

        ColdBrewAdapter(List<Brew> coldBrews)
        {
            mColdBrews = coldBrews;
        }

        @Override
        public ColdBrewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_brew, parent, false);

            return new ColdBrewHolder(view);
        }

        @Override
        public void onBindViewHolder(ColdBrewHolder holder, int position)
        {
            Brew brew = mColdBrews.get(position);
            holder.bindBrew(brew);
        }

        @Override
        public int getItemCount()
        {
            return mColdBrews.size();
        }

        void setBrews(List<Brew> brews)
        {
            mColdBrews = brews;
        }
    }

    private class ColdBrewHolder extends RecyclerView.ViewHolder
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

        ColdBrewHolder(View itemView)
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
                    mSharedPreferences.edit().putInt(DEFAULT_COLD_BREW_KEY, mBrew.getId()).apply();
                    mColdBrewAdapter.notifyDataSetChanged();
                    // TODO: 2/12/2018 Implement more efficient way to update data set

                    mStarBorderImgView.setVisibility(View.GONE);
                    mFilledStarImgView.setVisibility(View.VISIBLE);
                }
            });
            mFilledStarImgView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mSharedPreferences.edit().putInt(DEFAULT_COLD_BREW_KEY, mBrew.getId()).apply();
                    mColdBrewAdapter.notifyDataSetChanged();
                    // TODO: 2/12/2018 Implement more efficient way to update data set

                    mFilledStarImgView.setVisibility(View.GONE);
                    mStarBorderImgView.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public void onClick(View view)
        {
            // TODO: 2/11/2018 Setup ColdBrewActivity.newIntent()
//            Intent intent = ColdBrewActivity.newIntent(getContext(), mBrew.getId());
//            startActivity(intent);
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

            if (mBrew.getId() == mDefaultColdBrewId)
            {
                mStarBorderImgView.setVisibility(View.GONE);
                mFilledStarImgView.setVisibility(View.VISIBLE);
            }
        }
    }
}
