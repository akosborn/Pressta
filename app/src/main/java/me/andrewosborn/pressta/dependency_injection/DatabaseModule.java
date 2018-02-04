package me.andrewosborn.pressta.dependency_injection;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.andrewosborn.pressta.persistence.BrewDao;
import me.andrewosborn.pressta.persistence.BrewRepository;
import me.andrewosborn.pressta.persistence.PresstaDatabase;
import me.andrewosborn.pressta.viewmodel.CustomViewModelFactory;

/***
 * Modules are responsible for creating/satisfying dependencies.
 */

@Module
public class DatabaseModule
{
    private final PresstaDatabase database;

    public DatabaseModule(Application application)
    {
        this.database = PresstaDatabase
                .getInstance(application.getApplicationContext());
    }

    @Provides
    @Singleton
    BrewRepository provideBrewRepository(BrewDao brewDao)
    {
        return new BrewRepository(brewDao);
    }

    @Provides
    @Singleton
    BrewDao provideBrewDao(PresstaDatabase database)
    {
        return database.brewDao();
    }

    @Provides
    @Singleton
    PresstaDatabase providePresstaDatabase(Application application)
    {
        return database;
    }

    @Provides
    @Singleton
    ViewModelProvider.Factory provideViewModelFactory(BrewRepository brewRepository)
    {
        return new CustomViewModelFactory(brewRepository);
    }
}
