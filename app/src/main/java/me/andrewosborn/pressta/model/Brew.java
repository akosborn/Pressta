package me.andrewosborn.pressta.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.andrewosborn.pressta.persistence.BrewTypeConverter;
import me.andrewosborn.pressta.persistence.DateConverter;

@Entity
public class Brew
{
    public static final int DEFAULT_HOT_BREW_ID = 1;
    public static final int DEFAULT_COLD_BREW_ID = 2;

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "type")
    @TypeConverters({BrewTypeConverter.class})
    private Type type;

    @ColumnInfo(name = "coffee_weight")
    private int coffeeWeight;

    @ColumnInfo(name = "water_weight")
    private int waterWeight;

    @ColumnInfo(name = "ratio")
    private int ratio;

    @ColumnInfo(name = "brew_duration")
    private int brewDuration;

    @ColumnInfo(name = "completion_date")
    @TypeConverters({DateConverter.class})
    private Date completionDate;

    public Brew() {}

    @Ignore
    public Brew(Type type, int coffeeWeight, int ratio, int brewDuration, Date completionDate)
    {
        this.type = type;
        this.coffeeWeight = coffeeWeight;
        this.waterWeight = coffeeWeight * ratio;
        this.ratio = ratio;
        this.brewDuration = brewDuration;
        this.completionDate = completionDate;
    }

    @Ignore
    public Brew(Type type, String title, int coffeeWeight, int ratio, int brewDuration, Date completionDate)
    {
        this.type = type;
        this.title = title;
        this.coffeeWeight = coffeeWeight;
        this.waterWeight = coffeeWeight * ratio;
        this.ratio = ratio;
        this.brewDuration = brewDuration;
        this.completionDate = completionDate;
    }



    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public int getCoffeeWeight()
    {
        return coffeeWeight;
    }

    public void setCoffeeWeight(int coffeeWeight)
    {
        this.coffeeWeight = coffeeWeight;
    }

    public int getWaterWeight()
    {
        return waterWeight;
    }

    public void setWaterWeight(int waterWeight)
    {
        this.waterWeight = waterWeight;
    }

    public int getRatio()
    {
        return ratio;
    }

    public void setRatio(int ratio)
    {
        this.ratio = ratio;
    }

    public int getCalculatedWater(int coffeeWeight)
    {
        return ratio * coffeeWeight;
    }

    public int getCalculatedCoffee(int waterWeight)
    {
        return waterWeight/ratio;
    }

    public int getBrewDuration()
    {
        return brewDuration;
    }

    public void setBrewDuration(int brewDuration)
    {
        this.brewDuration = brewDuration;
    }

    public Date getCompletionDate()
    {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate)
    {
        this.completionDate = completionDate;
    }

    public static List<Brew> getDefaults()
    {
        List<Brew> defaultBrews = new ArrayList<>();

        Brew defaultHotBrew = new Brew(Type.HOT, "Default Hot", 20, 16, (int) 4.5*60,
                new Date(System.currentTimeMillis() + ((long) 4.5 * 60 * 3600 * 1000)));
        Brew defaultColdBrew = new Brew(Type.COLD, "Default Cold", 20, 8, (12 * 3600),
                new Date(System.currentTimeMillis() + (12 * 3600 * 1000)));
        defaultBrews.add(defaultHotBrew);
        defaultBrews.add(defaultColdBrew);

        return defaultBrews;
    }
}
