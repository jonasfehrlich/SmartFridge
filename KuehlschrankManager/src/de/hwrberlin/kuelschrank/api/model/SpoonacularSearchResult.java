package de.hwrberlin.kuelschrank.api.model;
import java.util.List;

public class SpoonacularSearchResult {
	private int id;
    private String title;
    private List<Ingredient> usedIngredients;
    private List<Ingredient> missedIngredients;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Ingredient> getUsedIngredients() {
        return usedIngredients;
    }

    public List<Ingredient> getMissedIngredients() {
        return missedIngredients;
    }

}
