package me.andrewosborn.pressta.persistence;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import me.andrewosborn.pressta.model.Brew;


public class BrewListViewModel extends AndroidViewModel
{
    private final LiveData<List<Brew>> brewList;

    private PresstaDatabase database;

    public BrewListViewModel(@NonNull Application application)
    {
        super(application);

        database = PresstaDatabase.getInstance(this.getApplication());

        brewList = database.brewDao().findAll();
    }
}
