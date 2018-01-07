package me.andrewosborn.pressta.model;

public class Brew
{
    private Type type;
    private int coffeeWeight;
    private int waterWeight;
    private int ratio;
    private float brewDurationMin;

    public Brew(Type type, int coffeeWeight, int ratio, float brewDurationMin)
    {
        this.type = type;
        this.coffeeWeight = coffeeWeight;
        this.waterWeight = coffeeWeight * ratio;
        this.ratio = ratio;
        this.brewDurationMin = brewDurationMin;
    }

    public Brew(Type type, int ratio, float brewDurationMin, int waterWeight)
    {
        this.type = type;
        this.coffeeWeight = ratio / waterWeight;
        this.waterWeight = waterWeight;
        this.ratio = ratio;
        this.brewDurationMin = brewDurationMin;
    }

    public Brew()
    {
        this.coffeeWeight = 0;
        this.waterWeight = 0;
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

    public float getBrewDurationMin()
    {
        return brewDurationMin;
    }

    public void setBrewDurationMin(float brewDurationMin)
    {
        this.brewDurationMin = brewDurationMin;
    }
}
