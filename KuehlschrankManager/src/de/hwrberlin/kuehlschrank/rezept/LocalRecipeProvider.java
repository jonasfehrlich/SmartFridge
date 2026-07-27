package de.hwrberlin.kuehlschrank.rezept;
import de.hwrberlin.kuehlschrank.model.Recipe;
import java.util.*;

/**
 * Built-in recipe database – no internet required.
 * Lecture 2.1.4: implements RecipeProvider (interface implementation).
 * Lecture 2.1.3: Polymorphism – interchangeable with OnlineRecipeProvider.
 */
public class LocalRecipeProvider implements RecipeProvider {
    private final List<Recipe> database;

    public LocalRecipeProvider() {
        database = new ArrayList<>();
        database.add(new Recipe("Chaos Pan",
            "Fry everything left in the fridge together. Season with salt and pepper.",
            Arrays.asList("Eggs", "Cheese", "Bell Pepper", "Potatoes"), "20 min.", "Local"));
        database.add(new Recipe("Omelette",
            "Whisk eggs, fry in butter, fold in cheese and vegetables.",
            Arrays.asList("Eggs", "Butter", "Cheese", "Bell Pepper"), "15 min.", "Local"));
        database.add(new Recipe("Yoghurt Smoothie",
            "Blend yoghurt with fruit and milk until creamy.",
            Arrays.asList("Yoghurt", "Milk", "Banana", "Strawberries"), "5 min.", "Local"));
        database.add(new Recipe("Vegetable Soup",
            "Dice vegetables, simmer in broth, season to taste.",
            Arrays.asList("Carrots", "Celery", "Leek", "Potatoes"), "30 min.", "Local"));
        database.add(new Recipe("Cheese Toast",
            "Top bread with cheese, grill in oven until golden.",
            Arrays.asList("Bread", "Cheese", "Butter"), "10 min.", "Local"));
        database.add(new Recipe("Pasta with Cream Sauce",
            "Cook pasta, warm cream with cheese and garlic, combine.",
            Arrays.asList("Pasta", "Cream", "Cheese", "Garlic"), "20 min.", "Local"));
        database.add(new Recipe("Scrambled Egg Toast",
            "Whisk eggs with milk, scramble in butter, serve on toast.",
            Arrays.asList("Eggs", "Milk", "Butter", "Bread"), "10 min.", "Local"));
    }

    @Override
    public List<Recipe> searchRecipes(List<String> availableIngredients) {
        List<Recipe> matching = new ArrayList<>();
        List<String> lower    = new ArrayList<>();
        for (String z : availableIngredients) lower.add(z.toLowerCase());
        for (Recipe r : database) {
            int hits = 0;
            for (String ing : r.getIngredients())
                if (lower.contains(ing.toLowerCase())) hits++;
            if (hits >= 1) matching.add(r);
        }
        return matching;
    }

    @Override
    public String getProviderName() { return "Local Recipe Database"; }
}
