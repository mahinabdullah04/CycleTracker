package ca.umanitoba.cs.abdullmm.model;

import com.google.common.base.Preconditions;

public class Shoe implements Gear {

    private String name;
    private String size;
    private String shoeType;

    public Shoe(String name, String size, String shoeType) {
        this.name = name;
        this.size = size;
        this.shoeType = shoeType;
        checkShoe();
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

    private void checkShoe() {
        Preconditions.checkNotNull(name, "Shoe name cannot be null");
        Preconditions.checkNotNull(size, "Shoe size cannot be null");
        Preconditions.checkNotNull(shoeType, "Shoe type cannot be null");
        Preconditions.checkState(!name.isEmpty(), "Shoe name cannot be empty");
        Preconditions.checkState(!size.isEmpty(), "Shoe size cannot be empty");
        Preconditions.checkState(!shoeType.isEmpty(), "Shoe type cannot be empty");
    }
}
