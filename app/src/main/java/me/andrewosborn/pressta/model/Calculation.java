package me.andrewosborn.pressta.model;

public class Calculation
{
    private static final double BREW_DURATION = 4.5;

    private int coffeeWeight;
    private int waterWeight;
    private int ratio;

    public Calculation()
    {
        this.coffeeWeight = 0;
        this.waterWeight = 0;
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

    public static double getBrewDuration()
    {
        return BREW_DURATION;
    }
}
