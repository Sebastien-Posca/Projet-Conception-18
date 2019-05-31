package fr.unice.polytech.cookieFactory.Shop;


import java.time.LocalTime;

public class TimeInterval {
    private LocalTime startTime;
    private LocalTime endTime;


    TimeInterval(LocalTime startTime, LocalTime endTime) {

        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean intersects(LocalTime pickupTime) {
        return (pickupTime.isAfter(this.startTime) && pickupTime.isBefore(endTime));

    }

    public void changeOpeningTime(LocalTime newTime) {
        this.startTime = newTime;
    }

    public void changeTime(LocalTime openingTime, LocalTime closingTime) {
        this.startTime = openingTime;
        this.endTime= closingTime;
    }

    public void changeClosingTime(LocalTime newTime) {
        this.endTime = newTime;
    }

    public String toString() {
        return  this.startTime.getHour()+"h"+this.startTime.getMinute() + ", " + this.endTime.getHour()+"h"+this.endTime.getMinute();
    }


    public TimeInterval newInstance() {
        return new TimeInterval(this.startTime, this.endTime);

    }
}

