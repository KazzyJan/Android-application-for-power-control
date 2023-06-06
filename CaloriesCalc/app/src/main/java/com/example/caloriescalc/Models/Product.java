package com.example.caloriescalc.Models;

public class Product {
    private String name;
    private int calories;
    private float fats, proteins, carbohydrates;
    public Product(){}
    public Product (String name, int calories, float fats, float proteins, float carbohydrates){
        this.name = name;
        this.calories = calories;
        this.carbohydrates = carbohydrates;
        this.fats = fats;
        this.proteins = proteins;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    public void setProteins(float proteins) {
        this.proteins = proteins;
    }

    public void setCarbohydrates(float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }

    public float getFats() {
        return fats;
    }

    public float getProteins() {
        return proteins;
    }

    public float getCarbohydrates() {
        return carbohydrates;
    }
}