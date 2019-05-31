package fr.unice.polytech.cookieFactory.Shop;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;


public class Schedule {
    private static final String EXCEPTSTRING ="The schedule for this day does not exist";
    private Map<LocalDate, TimeInterval> schedule;

    public Schedule() {
        schedule = new HashMap<>();
    }

    public boolean isDuringSchedule(int pickupHour, int pickupMinute, LocalDate day) {
        if(!schedule.containsKey(day)) {
            throw new IllegalArgumentException(EXCEPTSTRING);
        }
        return schedule.get(day).intersects(LocalTime.of(pickupHour, pickupMinute));


    }

    public boolean isDuringSchedule(LocalTime pickupTime, LocalDate day) {
        if(!schedule.containsKey(day)) {
            throw new IllegalArgumentException(EXCEPTSTRING);
        }
        return schedule.get(day).intersects(pickupTime);

    }

    public void changeOpeningScheduleOfDay(int startHour, int startMinute, LocalDate day) {
        if(!schedule.containsKey(day)) {
            throw new IllegalArgumentException(EXCEPTSTRING);
        }
        schedule.get(day).changeOpeningTime(LocalTime.of(startHour, startMinute));
    }

    public void changeScheduleOfDay(LocalTime openingTime, LocalTime closingTime, LocalDate day) {
        if(!schedule.containsKey(day)) {
            throw new IllegalArgumentException(EXCEPTSTRING);
        }
        schedule.get(day).changeTime(openingTime, closingTime);
    }
    void changeClosingScheduleOfDay(int endHour, int endMinute, int day) {
        if(!schedule.containsKey(day)) {
            throw new IllegalArgumentException(EXCEPTSTRING);
        }
        schedule.get(day).changeClosingTime(LocalTime.of(endHour, endMinute));
    }

    public void addScheduleOfDay(LocalTime openingTime, LocalTime closingTime, LocalDate newDate) {
        if(schedule.containsKey(newDate)) {
            throw new IllegalArgumentException("The schedule for this day already exist");
        }
        schedule.put(newDate, new TimeInterval(openingTime, closingTime));
    }

    public void printScheduleOfDay(LocalDate day) {
        if (this.schedule.containsKey(day)) {
            System.out.println("Schedule of " + day.toString() + " : " + schedule.get(day));
        }
    }

    public TimeInterval getScheduleIntervalOfDay(LocalDate day) {
        if (this.schedule.containsKey(day)) {
            return schedule.get(day);
        }
        return null;
    }

    Map<LocalDate, TimeInterval> getSchedule() {
            return this.schedule;
    }

}
