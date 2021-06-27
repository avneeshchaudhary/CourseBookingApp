package com.courseapp.models;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class CourseConfig {

    private String description;
    private int capacity;
    private HashMap<String, String> schedule;

    public static void setCourseConfiguration(CourseConfig courseConfig, DatabaseReference courseRef){
        DatabaseReference ref = courseRef.child("config");
        ref.child("description").setValue(courseConfig.description);
        ref.child("capacity").setValue(courseConfig.capacity);
        for (String day : courseConfig.schedule.keySet()){
            ref.child("schedule").child(day).setValue(courseConfig.schedule.getOrDefault(day, "OFF"));
        }
    }

    public CourseConfig() {
        description = "";
        capacity = 100;
        schedule = new HashMap<>();
        schedule.put(Weekday.MON.name(), "OFF");
        schedule.put(Weekday.THU.name(), "OFF");
        schedule.put(Weekday.TUE.name(), "OFF");
        schedule.put(Weekday.WED.name(), "OFF");
        schedule.put(Weekday.FRI.name(), "OFF");
    }

    public CourseConfig(String description, int capacity) {
        this.description = description;
        this.capacity = capacity;
    }

    public HashMap<String, String> getSchedule() {
        return schedule;
    }

    public void setSchedule(HashMap<String, String> schedule) {
        this.schedule = schedule;
    }

    public void updateSchedule(Weekday weekday, ClassTiming classTiming){
        schedule.put(weekday.name(), classTiming.name());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getClassTimingOn(Weekday weekday){
        return schedule.getOrDefault(weekday.name(), "OFF");
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean checkCollision(HashMap<String, String> schedule2){
        if (!ClassTiming.valueOf(schedule.get(Weekday.MON.name())).equals(ClassTiming.OFF) &&
                ClassTiming.valueOf(schedule.get(Weekday.MON.name())).equals(ClassTiming.valueOf(schedule2.get(Weekday.MON.name()))))
            return true;

        if (!ClassTiming.valueOf(schedule.get(Weekday.TUE.name())).equals(ClassTiming.OFF) &&
                ClassTiming.valueOf(schedule.get(Weekday.TUE.name())).equals(ClassTiming.valueOf(schedule2.get(Weekday.TUE.name()))))
            return true;

        if (!ClassTiming.valueOf(schedule.get(Weekday.WED.name())).equals(ClassTiming.OFF) &&
                ClassTiming.valueOf(schedule.get(Weekday.WED.name())).equals(ClassTiming.valueOf(schedule2.get(Weekday.WED.name()))))
            return true;

        if (!ClassTiming.valueOf(schedule.get(Weekday.THU.name())).equals(ClassTiming.OFF) &&
                ClassTiming.valueOf(schedule.get(Weekday.THU.name())).equals(ClassTiming.valueOf(schedule2.get(Weekday.THU.name()))))
            return true;

        if (!ClassTiming.valueOf(schedule.get(Weekday.FRI.name())).equals(ClassTiming.OFF) &&
                ClassTiming.valueOf(schedule.get(Weekday.FRI.name())).equals(ClassTiming.valueOf(schedule2.get(Weekday.FRI.name()))))
            return true;

        return false;
    }
}
