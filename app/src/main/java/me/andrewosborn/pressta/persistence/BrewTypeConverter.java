package me.andrewosborn.pressta.persistence;

import android.arch.persistence.room.TypeConverter;

import me.andrewosborn.pressta.model.Type;

public class BrewTypeConverter
{
    @TypeConverter
    public static String fromType(Type type)
    {
        if (type == null)
            return null;

        return type.name();
    }

    @TypeConverter
    public static Type toType(String type)
    {
        if (type == null)
            return null;

        return Type.valueOf(type);
    }
}
