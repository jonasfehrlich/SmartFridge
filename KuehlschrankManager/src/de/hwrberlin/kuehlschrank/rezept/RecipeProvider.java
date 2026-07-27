package de.hwrberlin.kuehlschrank.rezept;
import de.hwrberlin.kuehlschrank.model.Recipe;
import java.util.List;

/**
 * Interface for recipe providers.
 * Lecture 2.1.4: Interface definition. Enables polymorphism (local / online).
 */
public interface RecipeProvider {
    List<Recipe> searchRecipes(List<String> availableIngredients);
    String getProviderName();
}
