package me.andrewosborn.pressta.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import me.andrewosborn.pressta.persistence.BrewRepository;

/**
 * Creates ViewModels with arguments.
 */

public class CustomViewModelFactory implements ViewModelProvider.Factory
{
    private final BrewRepository brewRepository;

    public CustomViewModelFactory(BrewRepository brewRepository)
    {
        this.brewRepository = brewRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass)
    {
        if (modelClass.isAssignableFrom(BrewViewModel.class))
            return (T) new BrewViewModel(brewRepository);
        else if (modelClass.isAssignableFrom(BrewListViewModel.class))
            return (T) new BrewListViewModel(brewRepository);

        else
            throw new IllegalArgumentException("ViewModel Not Found");
    }
}
