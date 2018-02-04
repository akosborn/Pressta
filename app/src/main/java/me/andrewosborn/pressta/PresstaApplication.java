package me.andrewosborn.pressta;

import android.app.Application;

import me.andrewosborn.pressta.dependency_injection.ApplicationComponent;
import me.andrewosborn.pressta.dependency_injection.ApplicationModule;
import me.andrewosborn.pressta.dependency_injection.DaggerApplicationComponent;
import me.andrewosborn.pressta.dependency_injection.DatabaseModule;


public class PresstaApplication extends Application
{
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate()
    {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule(this))
                .build();
    }

    public ApplicationComponent getApplicationComponent()
    {
        return applicationComponent;
    }
}
