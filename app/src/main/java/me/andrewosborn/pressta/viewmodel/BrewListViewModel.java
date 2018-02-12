package me.andrewosborn.pressta.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import java.util.List;

import me.andrewosborn.pressta.model.Brew;
import me.andrewosborn.pressta.model.Type;
import me.andrewosborn.pressta.persistence.BrewRepository;

public class BrewListViewModel extends ViewModel
{
    private BrewRepository brewRepository;

    public BrewListViewModel(BrewRepository brewRepository)
    {
        this.brewRepository = brewRepository;
    }

    public LiveData<List<Brew>> getBrews()
    {
        return brewRepository.getAll();
    }

    public LiveData<List<Brew>> getBrewsByType(Type type)
    {
        return brewRepository.getByType(type);
    }

    public void deleteBrew(Brew brew)
    {
        DeleteBrewTask task = new DeleteBrewTask();
        task.execute(brew);
    }

    private class DeleteBrewTask extends AsyncTask<Brew, Void, Void>
    {

        @Override
        protected Void doInBackground(Brew... brew)
        {
            brewRepository.remove(brew[0]);
            return null;
        }
    }
}
