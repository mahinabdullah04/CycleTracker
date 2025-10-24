package ca.umanitoba.cs.abdullmm.model;

public class Helmet implements Gear {

    private String name;
    private String size;


    public Helmet(String name, String size){
        this.name = name;
        this.size = size;

    }






    public String getSize(){
        return size;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getType() {
        return "Helmet";
    }

    @Override
    public String getDescription() {
        return String.format("This is a Helmet named %s of size: %s", name, size);
    }
}
