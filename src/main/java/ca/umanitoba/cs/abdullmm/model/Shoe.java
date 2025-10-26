package ca.umanitoba.cs.abdullmm.model;

public class Shoe implements  Gear{

    private String name;
    private String size;
    private String shoeType;

    public Shoe(String name, String size, String shoeType){
        this.name = name;
        this.size = size;
        this.shoeType = shoeType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "Shoe";
    }
    public String getShoeType() {
        return shoeType;
    }
    public String getSize() {
        return size;
    }

    @Override
    public String getDescription() {
        return String.format("This is a Shoe named %s of size: %s and of type %s", name, size, shoeType);
    }
}
