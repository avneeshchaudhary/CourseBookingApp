package com.courseapp.models;

import java.io.Serializable;
import java.util.HashMap;

public class StudentSchedule{

    private HashMap<String, HashMap<String, Boolean>> schedule;

    public StudentSchedule() {
        schedule = new HashMap<>();
        HashMap<String, Boolean> day = new HashMap<>();
        for (ClassTiming timing : ClassTiming.values()){
            if (timing.equals(ClassTiming.OFF)) continue;
            day.put(timing.name(), false);
        }
        for (Weekday weekday : Weekday.values()){
            schedule.put(weekday.name(), day);
        }
    }

    public HashMap<String, HashMap<String, Boolean>> getSchedule() {
        return schedule;
    }

    public void setSchedule(HashMap<String, HashMap<String, Boolean>> schedule) {
        this.schedule = schedule;
    }

    public void updateAddSchedule(Weekday weekday, ClassTiming classTiming){
        HashMap<String, Boolean> map = schedule.get(weekday.name());
        map.put(classTiming.name(), true);
        schedule.put(weekday.name(), map);
    }

    public void updateRemoveSchedule(Weekday weekday, ClassTiming classTiming){
        HashMap<String, Boolean> map = schedule.get(weekday.name());
        map.put(classTiming.name(), false);
        schedule.put(weekday.name(), map);
    }

    public boolean isStudentAvailable(Weekday weekday, ClassTiming classTiming){
        return !schedule.get(weekday.name()).get(classTiming.name());
    }
}
