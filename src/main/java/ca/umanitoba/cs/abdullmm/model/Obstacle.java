package ca.umanitoba.cs.abdullmm.model;

import com.google.common.base.Preconditions;

public class Obstacle {
    private GridPoint position;
    private Dimension size;
    private String name;

    //constructor
    public Obstacle(String name,GridPoint position, Dimension size){
        this.name = name;
        this.position = position;
        this.size = size;
        checkObstacle();
    }

    public GridPoint getPosition(){
        return position;
    }
    public Dimension getSize(){
        return size;
    }
    public String getName(){
        return name;
    }

    public void setPosition(GridPoint position){
        this.position = position;
    }

    private void checkObstacle(){
        Preconditions.checkNotNull(position, "Obstacle Position cannot be null");
        Preconditions.checkNotNull(size, "Obstacle size cannot be null");
        Preconditions.checkNotNull(name, "Obstacle name cannot be empty");
        Preconditions.checkState(!name.isEmpty(), "Obstacle name cannot be empty");
    }
}
