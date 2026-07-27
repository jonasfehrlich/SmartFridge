package de.hwrberlin.kuehlschrank.model;
import java.util.List;

/**
 * DTO for a recipe search result from an external API.
 * Keeps the original field names as returned by the API (title, usedIngredients, missedIngredients).
 * Lecture 2.1.3: simple data-holding class; 2.1.8: typed lists.
 */
public class RezeptSuchtreffer {
    private int id;
    private String title;
    private List<String> usedIngredients;
    private List<String> missedIngredients;

    public RezeptSuchtreffer(int id, String title,
                              List<String> usedIngredients,
                              List<String> missedIngredients) {
        this.id = id;
        this.title = title;
        this.usedIngredients = usedIngredients;
        this.missedIngredients = missedIngredients;
    }

    public int getId()                           { return id; }
    public String getTitle()                     { return title; }
    public List<String> getUsedIngredients()     { return usedIngredients; }
    public List<String> getMissedIngredients()   { return missedIngredients; }

    @Override
    public String toString() {
        return "RecipeResult[id=" + id + ", title='" + title + "']";
    }
}
