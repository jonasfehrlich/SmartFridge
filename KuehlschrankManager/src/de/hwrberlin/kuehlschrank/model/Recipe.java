package de.hwrberlin.kuehlschrank.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** A cooking recipe. Lecture 2.1.7: ArrayList as a typed collection. */
public class Recipe implements Serializable {
    private static final long serialVersionUID = 1L;
    private String       name;
    private String       description;
    private List<String> ingredients;
    private String       preparationTime;
    private String       source;

    public Recipe(String name, String description, List<String> ingredients,
                  String preparationTime, String source) {
        this.name            = name;
        this.description     = description;
        this.ingredients     = ingredients != null ? new ArrayList<>(ingredients) : new ArrayList<>();
        this.preparationTime = preparationTime;
        this.source          = source;
    }

    public String       getName()                        { return name; }
    public void         setName(String n)                { this.name = n; }
    public String       getDescription()                 { return description; }
    public void         setDescription(String d)         { this.description = d; }
    public List<String> getIngredients()                 { return ingredients; }
    public void         setIngredients(List<String> i)   { this.ingredients = i; }
    public String       getPreparationTime()             { return preparationTime; }
    public void         setPreparationTime(String t)     { this.preparationTime = t; }
    public String       getSource()                      { return source; }
    public void         setSource(String s)              { this.source = s; }

    @Override
    public String toString() {
        return name + " (approx. " + preparationTime + " | " + source + ")";
    }
}
