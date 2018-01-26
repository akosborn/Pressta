package me.andrewosborn.pressta.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import me.andrewosborn.pressta.model.Brew;


@Database(entities = {Brew.class}, version = 1)
public abstract class PresstaDatabase extends RoomDatabase
{
    private static PresstaDatabase INSTANCE;

    public abstract BrewDao brewDao();

    public static PresstaDatabase getPresstaDatabase(Context context)
    {
        if (INSTANCE == null)
        {
            INSTANCE = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                    PresstaDatabase.class)
                    .build();
        }

        return INSTANCE;
    }

    public static void destroyInstance()
    {
        INSTANCE = null;
    }
}
