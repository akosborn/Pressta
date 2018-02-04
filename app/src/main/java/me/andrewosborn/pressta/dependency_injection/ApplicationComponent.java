package me.andrewosborn.pressta.dependency_injection;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Component;
import me.andrewosborn.pressta.controller.ColdBrewFragment;
import me.andrewosborn.pressta.controller.HotBrewFragment;
import me.andrewosborn.pressta.controller.PresstaFragment;


/***
 * A component class lists out the required dependencies for a given object
 */

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class})
public interface ApplicationComponent
{
    void inject(PresstaFragment presstaFragment);
    void inject(HotBrewFragment hotBrewFragment);
    void inject(ColdBrewFragment coldBrewFragment);

    Application application();
}
