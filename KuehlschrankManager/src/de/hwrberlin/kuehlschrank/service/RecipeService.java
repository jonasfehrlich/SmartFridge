package de.hwrberlin.kuehlschrank.service;
import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.recipe.*;
import java.util.*;

/**
 * Service for recipe suggestions (chaos mode).
 * Lecture 2.1.3: Polymorphism via RecipeProvider interface.
 * Lecture 2.1.4: Late binding – provider is decided at runtime.
 */
public class RecipeService {
    private RecipeProvider provider;

    public RecipeService(boolean onlineMode) {
        this.provider = onlineMode ? new OnlineRecipeProvider() : new LocalRecipeProvider();
    }

    /** For testing: inject interface implementation directly. */
    public RecipeService(RecipeProvider provider) { this.provider = provider; }

    /** Prioritises soon-to-expire products as ingredients (chaos mode). */
    public List<Recipe> suggestRecipes(FridgeManager manager) {
        List<String> ingredients = new ArrayList<>();
        for (Product p : manager.getExpiringSoon(5)) ingredients.add(p.getName());
        for (Product p : manager.getAllProducts()) if (!ingredients.contains(p.getName())) ingredients.add(p.getName());
        return provider.findRecipes(ingredients);
    }

    /** Which ingredients of a recipe are missing from the fridge? */
    public List<String> getMissingIngredients(Recipe recipe, FridgeManager manager) {
        List<String> missing = new ArrayList<>();
        for (String ingredient : recipe.getIngredients()) {
            if (manager.findProduct(ingredient) == null) missing.add(ingredient);
        }
        return missing;
    }

    /** Returns chaos-pan recipe suggestions from soon-to-expire products. */
    public List<Recipe> createChaosPan(FridgeManager manager) {
        List<String> expiring = new ArrayList<>();
        for (Product p : manager.getExpiringSoon(5)) expiring.add(p.getName());
        if (expiring.isEmpty()) return new ArrayList<>();
        return provider.findRecipes(expiring);
    }

    public String getProviderName() { return provider.getProviderName(); }
}
