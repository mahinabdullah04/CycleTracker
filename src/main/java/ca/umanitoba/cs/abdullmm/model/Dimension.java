package ca.umanitoba.cs.abdullmm.model;

import com.google.common.base.Preconditions;

public class Dimension {
    private int width;
    private int height;

    //constructor
    public Dimension(int height, int width){
        this.height = height;
        this.width = width;
        checkDimension();
    }

    public int width(){
        return width;
    }
    public int height(){
        return height;
    }

    private  void checkDimension(){

        Preconditions.checkArgument(width > 0, "Width must be positive");
        Preconditions.checkArgument(height > 0, "Height must be positive");
    }
}
