package me.andrewosborn.pressta.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import me.andrewosborn.pressta.model.Brew;
import me.andrewosborn.pressta.persistence.BrewRepository;

public class BrewViewModel extends ViewModel
{
    private BrewRepository brewRepository;

    BrewViewModel(BrewRepository brewRepository)
    {
        this.brewRepository = brewRepository;
    }

    public LiveData<Brew> getBrew(int id)
    {
        return brewRepository.getById(id);
    }

    public LiveData<List<Brew>> getAllBrews()
    {
        return brewRepository.getAll();
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
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
