package ca.umanitoba.cs.abdullmm.model;

import com.google.common.base.Preconditions;

public class Bike implements  Gear{

    private String name;
    private String bikeType;
    private int numberOfGears;

    public Bike(String name, String bikeType, int numberOfGears){
        this.name = name;
        this.bikeType = bikeType;
        this.numberOfGears = numberOfGears;
        checkBike();
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "Bike";
    }

    @Override
    public String getDescription() {
        return String.format("This is a %s bike named %s with %d gears.", bikeType, name, numberOfGears);
    }

    private void checkBike(){
        Preconditions.checkNotNull(name, "Bike name cannot be null");
        Preconditions.checkNotNull(bikeType, "Bike type cannot be null");
        Preconditions.checkArgument(numberOfGears > 0, "Number of Bike gears must be positive ");
        Preconditions.checkState(!bikeType.isEmpty(), "Bike type cannot be empty");
        Preconditions.checkState(!name.isEmpty(), "Bike name cannot be empty");
    }
}
