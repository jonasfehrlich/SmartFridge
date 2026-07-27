package de.hwrberlin.kuehlschrank.recipe;
import de.hwrberlin.kuehlschrank.model.Recipe;
import java.util.List;

/**
 * Interface for recipe providers.
 * Lecture 2.1.4: interface definition. Enables polymorphism (local vs. online).
 * Lecture 2.1.3: late binding – the concrete implementation is decided at runtime.
 */
public interface RecipeProvider {
    /** Returns a list of recipes that can be made with the given available ingredients. */
    List<Recipe> findRecipes(List<String> availableIngredients);

    /** Returns a human-readable name of this provider (e.g. "Local" or "Spoonacular API"). */
    String getProviderName();
}
