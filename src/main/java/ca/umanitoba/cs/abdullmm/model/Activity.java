package ca.umanitoba.cs.abdullmm.model;

import com.google.common.base.Preconditions;

import java.time.LocalDate;

public class Activity {
    private Route route;
    private Gear gearUsed;
    private LocalDate date;
    private double distance;
    private int durationMinutes;
    private String name;

    public Activity(Route route, Gear gearUsed, LocalDate date, double distance, int durationMinutes, String name ){
        this.route = route;
        this.gearUsed = gearUsed;
        this.date = date;
        this.distance = distance;
        this.durationMinutes = durationMinutes;
        this.name = name;
        checkActivity();

    }
    public Route getRoute(){
        return route;
    }
    public Gear gearUsed() {
        return gearUsed;
    }
    public LocalDate getDate(){
        return date;
    }
    public double getDistance(){
        return distance;
    }
    public int getDurationMinutes(){
        return durationMinutes;
    }
    public String getName(){
        return name;
    }

    private void checkActivity(){
        Preconditions.checkNotNull(route, "Activity route cannot be null");
        Preconditions.checkNotNull(gearUsed, "Activity gear cannot be null");
        Preconditions.checkNotNull(date, "Activity date cannot be null");
        Preconditions.checkNotNull(name, "Activity name cannot be null");
        Preconditions.checkState(!name.isEmpty(), "Activity name cannot be empty");
        Preconditions.checkState(!route.getPoints().isEmpty(), "Activity route cannot be empty");
        Preconditions.checkArgument(distance > 0, "Activity distance must be positive");
        Preconditions.checkArgument(durationMinutes > 0, "Activity duration must be positive");




    }
}
