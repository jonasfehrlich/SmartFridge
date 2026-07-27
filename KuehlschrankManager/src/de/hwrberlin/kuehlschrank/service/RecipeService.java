package de.hwrberlin.kuehlschrank.service;
import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.rezept.*;
import java.util.*;

/**
 * Service for recipe suggestions (Chaos-Pan mode).
 * Lecture 2.1.3: Polymorphism via RecipeProvider interface.
 * Lecture 2.1.4: Late binding – provider is chosen at runtime.
 */
public class RecipeService {
    private RecipeProvider provider;

    public RecipeService(boolean onlineMode) {
        this.provider = onlineMode ? new OnlineRecipeProvider() : new LocalRecipeProvider();
    }

    /** For testing: inject a RecipeProvider directly. */
    public RecipeService(RecipeProvider provider) { this.provider = provider; }

    /** Suggests recipes prioritising expiring products as ingredients (Chaos mode). */
    public List<Recipe> suggestRecipes(FridgeManager manager) {
        List<String> ingredients = new ArrayList<>();
        for (Product p : manager.getExpiringSoon(5))   ingredients.add(p.getName());
        for (Product p : manager.getAllProducts())
            if (!ingredients.contains(p.getName()))    ingredients.add(p.getName());
        return provider.searchRecipes(ingredients);
    }

    /** Returns which ingredients of a recipe are missing from the fridge. */
    public List<String> getMissingIngredients(Recipe recipe, FridgeManager manager) {
        List<String> missing = new ArrayList<>();
        for (String ing : recipe.getIngredients())
            if (manager.findProduct(ing) == null) missing.add(ing);
        return missing;
    }

    /** Creates Chaos-Pan recipe suggestions from expiring products. */
    public List<Recipe> createChaosPan(FridgeManager manager) {
        return suggestRecipes(manager);
    }

    public String getProviderName() { return provider.getProviderName(); }
}
