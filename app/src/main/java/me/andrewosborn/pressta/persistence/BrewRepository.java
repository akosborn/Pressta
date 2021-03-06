package me.andrewosborn.pressta.persistence;

import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import me.andrewosborn.pressta.model.Brew;
import me.andrewosborn.pressta.model.Type;

public class BrewRepository
{
    private final BrewDao brewDao;

    @Inject
    public BrewRepository(BrewDao brewDao)
    {
        this.brewDao = brewDao;
    }

    public LiveData<List<Brew>> getAll()
    {
        return brewDao.findAll();
    }

    public LiveData<List<Brew>> getByType(Type type)
    {
        return brewDao.findByType(type);
    }

    public LiveData<Brew> getById(int id)
    {
        return brewDao.findById(id);
    }

    public LiveData<Brew> getByTitle(String title)
    {
        return brewDao.findByTitle(title);
    }

    public void addAll(List<Brew> brews)
    {
        brewDao.insertAll(brews);
    }

    public void remove(Brew brew)
    {
        brewDao.delete(brew);
    }

    public void add(Brew brew)
    {
        brewDao.insert(brew);
    }
}
