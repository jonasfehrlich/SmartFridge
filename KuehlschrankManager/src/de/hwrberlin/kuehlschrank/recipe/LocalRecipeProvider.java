package de.hwrberlin.kuehlschrank.recipe;
import de.hwrberlin.kuehlschrank.model.Recipe;
import java.util.*;

/**
 * Local recipe database without internet.
 * Lecture 2.1.4: implements RecipeProvider (interface implementation).
 * Lecture 2.1.3: Polymorphism – interchangeable with OnlineRecipeProvider.
 */
public class LocalRecipeProvider implements RecipeProvider {
    private final List<Recipe> database;

    public LocalRecipeProvider() {
        database = new ArrayList<>();
        database.add(new Recipe("Chaos Pan",
            "Fry everything together that is still in the fridge. Season with salt and pepper.",
            Arrays.asList("Eggs", "Cheese", "Bell pepper", "Potatoes"), "20 min.", "Local"));
        database.add(new Recipe("Omelette",
            "Whisk eggs, fry in butter, fold in cheese and vegetables.",
            Arrays.asList("Eggs", "Butter", "Cheese", "Bell pepper"), "15 min.", "Local"));
        database.add(new Recipe("Yoghurt Smoothie",
            "Blend yoghurt with fruit and milk until creamy.",
            Arrays.asList("Yoghurt", "Milk", "Banana", "Strawberries"), "5 min.", "Local"));
        database.add(new Recipe("Vegetable Soup",
            "Dice vegetables, cook in broth until soft, season.",
            Arrays.asList("Carrots", "Celery", "Leek", "Potatoes"), "30 min.", "Local"));
        database.add(new Recipe("Cheese Toast",
            "Top bread with cheese, gratinate in the oven.",
            Arrays.asList("Bread", "Cheese", "Butter"), "10 min.", "Local"));
        database.add(new Recipe("Pasta with Cream Sauce",
            "Cook pasta, warm cream with cheese and garlic, combine.",
            Arrays.asList("Pasta", "Cream", "Cheese", "Garlic"), "20 min.", "Local"));
        database.add(new Recipe("Scrambled Egg Toast",
            "Whisk eggs with milk, scramble in butter, serve on toast.",
            Arrays.asList("Eggs", "Milk", "Butter", "Bread"), "10 min.", "Local"));
    }

    @Override
    public List<Recipe> findRecipes(List<String> availableIngredients) {
        List<Recipe> matches = new ArrayList<>();
        List<String> lower = new ArrayList<>();
        for (String i : availableIngredients) lower.add(i.toLowerCase());
        for (Recipe r : database) {
            int hits = 0;
            for (String ingredient : r.getIngredients()) if (lower.contains(ingredient.toLowerCase())) hits++;
            if (hits >= 1) matches.add(r);
        }
        return matches;
    }

    @Override
    public String getProviderName() { return "Local Recipe Database"; }
}
