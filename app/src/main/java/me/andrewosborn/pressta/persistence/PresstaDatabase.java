package me.andrewosborn.pressta.persistence;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import me.andrewosborn.pressta.model.Brew;


@Database(entities = {Brew.class}, version = 1, exportSchema = false)
public abstract class PresstaDatabase extends RoomDatabase
{
    private static final String TAG = "PresstaDatabase";
    private static PresstaDatabase INSTANCE;

    public abstract BrewDao brewDao();

    public static PresstaDatabase getInstance(Context context)
    {
        if (INSTANCE == null)
        {
            INSTANCE = buildInMemoryDatabase(context);

            // Trigger db creation and, subsequently, RoomDatabase.Callback().onCreate()
            INSTANCE.getOpenHelper().getReadableDatabase();
        }

        return INSTANCE;
    }

    private static PresstaDatabase buildInMemoryDatabase(final Context context)
    {
        return Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                PresstaDatabase.class)
                .addCallback(getCallback(context))
                .build();
    }

    private static PresstaDatabase buildDatabase(final Context context)
    {
        return Room.databaseBuilder(context.getApplicationContext(),
                PresstaDatabase.class, "pressta_db.db")
                .addCallback(getCallback(context))
                .build();
    }

    @NonNull
    private static Callback getCallback(final Context context)
    {
        return new Callback()
        {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db)
            {
                super.onCreate(db);

                Log.i(TAG, "onCreate() called");
                Completable.fromRunnable(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getInstance(context).brewDao().insertAll(Brew.getDefaults());
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .subscribe();
            }
        };
    }

    public static void destroyInstance()
    {
        INSTANCE = null;
    }
}
