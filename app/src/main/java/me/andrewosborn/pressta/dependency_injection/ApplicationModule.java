package me.andrewosborn.pressta.dependency_injection;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import me.andrewosborn.pressta.PresstaApplication;

/***
 * A module class is responsible for creating or satisfying the dependency
 */

@Module
public class ApplicationModule
{
    private final PresstaApplication application;

    public ApplicationModule(PresstaApplication application)
    {
        this.application = application;
    }

    @Provides
    PresstaApplication providePresstaApplication()
    {
        return application;
    }

    @Provides
    Application provideApplication()
    {
        return application;
    }
}
