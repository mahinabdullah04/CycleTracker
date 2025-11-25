package ca.umanitoba.cs.abdullmm.model;

import com.google.common.base.Preconditions;

public class Helmet implements Gear {

    private String name;
    private String size;

    public Helmet(String name, String size) {
        this.name = name;
        this.size = size;
        checkHelmet();
    }

    public String getSize() {
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

    private void checkHelmet() {
        Preconditions.checkNotNull(name, "Helmet name cannot be null");
        Preconditions.checkNotNull(size, "Helmet size cannot be null");
        Preconditions.checkState(!name.isEmpty(), "Helmet name cannot be empty");
        Preconditions.checkState(!size.isEmpty(), "Helmet size cannot be empty");
    }
}
