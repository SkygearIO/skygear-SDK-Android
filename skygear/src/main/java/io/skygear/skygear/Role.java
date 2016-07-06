package io.skygear.skygear;

/**
 * The Skygear User Role.
 */
public class Role {
    private final String name;

    /**
     * Instantiates a new Role.
     *
     * @param name the name
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * Gets the role name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object another) {
        return another instanceof Role && this.name.equals(((Role) another).name);
    }
}
