package me.andrewosborn.pressta.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import io.reactivex.Completable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.andrewosborn.pressta.model.Brew;
import me.andrewosborn.pressta.persistence.BrewRepository;

public class BrewViewModel extends ViewModel
{
    private static final String TAG = "BrewViewModel";
    private BrewRepository brewRepository;

    BrewViewModel(BrewRepository brewRepository)
    {
        this.brewRepository = brewRepository;
    }

    public LiveData<Brew> getBrew(int id)
    {
        return brewRepository.getById(id);
    }

    public void add(final Brew mBrew)
    {
        Completable.fromRunnable(new Runnable()
        {
            @Override
            public void run()
            {
                brewRepository.add(mBrew);
            }
        })
                .doOnError(new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        Log.e(TAG, mBrew.toString() + "couldn't be saved to db", throwable);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
