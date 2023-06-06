package com.example.caloriescalc.Models;

public class CaloriesStatistic {
    private float caloriesInThisDay;
    private String date;
    public CaloriesStatistic(){}
    public CaloriesStatistic(float caloriesInThisDay, String date){
        this.date = date;
        this.caloriesInThisDay = caloriesInThisDay;
    }

    public void setCaloriesInThisDay(float caloriesInThisDay) {
        this.caloriesInThisDay = caloriesInThisDay;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getCaloriesInThisDay() {
        return caloriesInThisDay;
    }

    public String getDate() {
        return date;
    }
}
