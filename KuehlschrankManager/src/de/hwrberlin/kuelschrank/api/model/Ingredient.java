package de.hwrberlin.kuelschrank.api.model;

public class Ingredient {

    private String name;
    private String original;

    public String getName() {
        return name;
    }

    public String getOriginal() {
        return original;
    }

    public String getDisplayName() {
        if (original != null && !original.isBlank()) {
            return original;
        }

        return name != null ? name : "";
    }
}