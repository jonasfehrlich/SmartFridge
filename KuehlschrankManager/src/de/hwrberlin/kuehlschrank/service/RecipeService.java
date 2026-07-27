package de.hwrberlin.kuehlschrank.service;
import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.recipe.*;
import java.util.*;

/**
 * Service class for recipe suggestions and the Chaos Pan feature.
 * Lecture 2.1.3: Polymorphism via the RecipeProvider interface.
 * Lecture 2.1.4: late binding – the provider is decided at runtime.
 */
public class RecipeService {
    private RecipeProvider provider;

    /** Production constructor: chooses online or local provider automatically. */
    public RecipeService(boolean onlineMode) {
        this.provider = onlineMode ? new OnlineRecipeProvider() : new LocalRecipeProvider();
    }

    /** Test constructor: inject any RecipeProvider implementation directly (e.g. a stub). */
    public RecipeService(RecipeProvider provider) { this.provider = provider; }

    /**
     * Returns recipe suggestions prioritising soon-to-expire products (chaos mode).
     * Products expiring within 5 days come first in the ingredient list.
     */
    public List<Recipe> suggestRecipes(FridgeManager manager) {
        List<String> ingredients = new ArrayList<>();
        for (Product p : manager.getExpiringSoon(5)) ingredients.add(p.getName());
        for (Product p : manager.getAllProducts())
            if (!ingredients.contains(p.getName())) ingredients.add(p.getName());
        return provider.findRecipes(ingredients);
    }

    /** Returns the ingredients of a recipe that are NOT currently in the fridge. */
    public List<String> getMissingIngredients(Recipe recipe, FridgeManager manager) {
        List<String> missing = new ArrayList<>();
        for (String ingredient : recipe.getIngredients())
            if (manager.findProduct(ingredient) == null) missing.add(ingredient);
        return missing;
    }

    /**
     * Chaos Pan: uses only soon-to-expire products as the search base.
     * Returns an empty list if nothing is expiring soon.
     */
    public List<Recipe> createChaosPan(FridgeManager manager) {
        List<String> expiring = new ArrayList<>();
        for (Product p : manager.getExpiringSoon(5)) expiring.add(p.getName());
        if (expiring.isEmpty()) return new ArrayList<>();
        return provider.findRecipes(expiring);
    }

    /** Returns the name of the currently active recipe provider. */
    public String getProviderName() { return provider.getProviderName(); }
}
