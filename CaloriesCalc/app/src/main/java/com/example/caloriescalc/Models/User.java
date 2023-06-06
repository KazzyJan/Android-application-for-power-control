package com.example.caloriescalc.Models;

import java.util.ArrayList;

public class User {
    private String weight, age, height, gender, uid;
    private ArrayList<CaloriesStatistic> statList;
    public User(){}
    public User(String weight, String age, String height, String gender, String uid, ArrayList<CaloriesStatistic> statList){
        this.weight = weight;
        this.age = age;
        this.height = height;
        this.gender = gender;
        this.uid = uid;
        this.statList = statList;
    }

    public ArrayList<CaloriesStatistic> getStatList() {
        return statList;
    }
    public String getWeight() {
        return this.weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAge() {
        return this.age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {this.uid = uid;}

    public void setStatList(ArrayList<CaloriesStatistic> statList) {
        this.statList = statList;
    }
    public void addStatList(CaloriesStatistic stat){
        this.statList.add(stat);
    }
    public CaloriesStatistic getStatListToday(){
        if (this.statList.size() == 0){
            return null;
        }
        else {
            return this.statList.get(statList.size() - 1);
        }
    }
    public CaloriesStatistic getStatThisDay(String date){
        for(CaloriesStatistic stat: this.statList){
            if(date.equals(stat.getDate())){
                return stat;
            }
        }
        return null;
    }
}
