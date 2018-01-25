package me.andrewosborn.pressta.model;

import java.util.Date;

public class Brew
{
    private Type type;
    private int coffeeWeight;
    private int waterWeight;
    private int ratio;
    private int brewDurationSeconds;
    private Date finishDate;

    public Brew(Type type, int coffeeWeight, int ratio, int brewDurationSeconds, Date finishDate)
    {
        this.type = type;
        this.coffeeWeight = coffeeWeight;
        this.waterWeight = coffeeWeight * ratio;
        this.ratio = ratio;
        this.brewDurationSeconds = brewDurationSeconds;
        this.finishDate = finishDate;
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

    public int getBrewDurationSeconds()
    {
        return brewDurationSeconds;
    }

    public void setBrewDurationSeconds(int brewDurationSeconds)
    {
        this.brewDurationSeconds = brewDurationSeconds;
    }

    public Date getFinishDate()
    {
        return finishDate;
    }

    public void setFinishDate(Date finishDate)
    {
        this.finishDate = finishDate;
    }
}
