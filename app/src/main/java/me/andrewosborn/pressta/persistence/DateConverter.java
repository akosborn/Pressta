package me.andrewosborn.pressta.persistence;

import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

public class DateConverter
{
    @TypeConverter
    public static Long fromDate(Date date)
    {
        if (date == null)
            return null;

        return date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long msSinceEpoch)
    {
        if (msSinceEpoch == null)
            return null;

        return new Date(msSinceEpoch);
    }
}
