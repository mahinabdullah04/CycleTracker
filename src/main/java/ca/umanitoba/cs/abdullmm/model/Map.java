package ca.umanitoba.cs.abdullmm.model;

import com.google.common.base.Preconditions;

import java.util.ArrayList;

public class Map {
    private Dimension dimension;
    private ArrayList<Obstacle> obstacles;
    private String name;

    public Map(Dimension dimension, String name){
        this.dimension = dimension;
        this.name = name;
        this.obstacles = new ArrayList<>();
        checkMap();
    }

    public Dimension getDimension(){
        return dimension;
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }

    public String getName(){
        return name;

    }
    public void addObstacle(Obstacle obstacle){
        obstacle.add(obstacle);
    }
    public void removeObstacle(Obstacle obstacle){
        obstacles.remove(obstacle);
    }

    public boolean isValidPosition(GridPoint point){
        // implemented later
        return true;
    }

    private void checkMap(){
        Preconditions.checkNotNull(dimension, "Map dimension cannot be null");
        Preconditions.checkNotNull(obstacles, "Map obstacles cannot be null");
        Preconditions.checkNotNull(name, "Map name cannot be null");
        Preconditions.checkState(!name.isEmpty(), "Map name cannot be empty");
    }
}
