package me.andrewosborn.pressta.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;
import java.util.UUID;

@Entity
public class Brew
{
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @Ignore
    private Type type;

    @ColumnInfo(name = "coffee_weight")
    private int coffeeWeight;

    @ColumnInfo(name = "water_weight")
    private int waterWeight;

    @ColumnInfo(name = "ratio")
    private int ratio;

    @ColumnInfo(name = "brew_duration")
    private int brewDuration;

    @Ignore
    private Date completionDate;

    public Brew()
    {
    }

    public Brew(Type type, int coffeeWeight, int ratio, int brewDuration, Date completionDate)
    {
        this.type = type;
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
}
