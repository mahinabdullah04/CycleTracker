package ca.umanitoba.cs.abdullmm.model;

import com.google.common.base.Preconditions;

import java.util.ArrayList;

public class Route {

    private ArrayList<GridPoint> points;
    private String name;

    public Route(String name){
        this.name = name;
        this.points = new ArrayList<>();
        checkRoute();
    }

    public ArrayList<GridPoint> getPoints(){
        return points;
    }

    public String getName(){
        return name;
    }

    public void addPoint(GridPoint point){
        points.add(point);
    }

    public int getDistance(){
        return points.size();
    }

    public boolean contains(GridPoint point){
        return points.contains(point);
    }

    private void checkRoute(){
        Preconditions.checkNotNull(points, "Route points list cannot be null");
        Preconditions.checkNotNull(name, "Route namme cannot be null");
        Preconditions.checkState(!name.isEmpty(), "Route name cannot be empty");
        // Activity class will check for empty points list
    }


}
