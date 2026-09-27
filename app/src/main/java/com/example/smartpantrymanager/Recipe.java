package com.example.smartpantrymanager;

public class Recipe {

    private final int id;
    private final String name;
    private final String description;
    private final String instructions;

    public Recipe(
            int id,
            String name,
            String description,
            String instructions) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.instructions = instructions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription(){
        return description;
    }

    public String getInstructions(){
        return instructions;
    }
}
