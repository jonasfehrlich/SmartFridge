package de.hwrberlin.kuehlschrank;

import de.hwrberlin.kuehlschrank.model.Product;
import de.hwrberlin.kuehlschrank.model.ProductCategory;
import de.hwrberlin.kuehlschrank.model.Recipe;
import de.hwrberlin.kuehlschrank.recipe.RecipeProvider;
import de.hwrberlin.kuehlschrank.service.FridgeManager;
import de.hwrberlin.kuehlschrank.service.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RecipeService.
 * Uses a stub RecipeProvider so no real HTTP calls are made.
 * Covers: suggestRecipes, getMissingIngredients, createChaosPan, getProviderName.
 *
 * Recipe constructor: Recipe(name, description, ingredients, preparationTime, source)
 */
class RecipeServiceTest {

    /** Stub: always returns two fixed recipes regardless of the ingredient list. */
    private static class StubRecipeProvider implements RecipeProvider {
        @Override
        public List<Recipe> findRecipes(List<String> ingredients) {
            return Arrays.asList(
                new Recipe("Veggie Pan", "Quick vegetable stir-fry.",
                        Arrays.asList("Carrot", "Zucchini", "Olive Oil"),
                        "15 min", "stub"),
                new Recipe("Milk Soup", "Creamy potato soup.",
                        Arrays.asList("Milk", "Potato"),
                        "20 min", "stub")
            );
        }
        @Override
        public String getProviderName() { return "StubProvider"; }
    }

    private RecipeService recipeService;
    private FridgeManager manager;

    @BeforeEach
    void setUp() {
        recipeService = new RecipeService(new StubRecipeProvider());
        manager = new FridgeManager();
        // Carrot expires in 3 days -> within chaos-pan threshold of 5
        manager.addProduct(new Product("Carrot", ProductCategory.FRUITS_VEGETABLES,
                0.3, "kg", LocalDate.now().plusDays(3), 0.1));
        // Milk is fresh (14 days) -> not prioritised in chaos pan
        manager.addProduct(new Product("Milk", ProductCategory.DAIRY,
                2.0, "L", LocalDate.now().plusDays(14), 1.0));
    }

    // ---- suggestRecipes ----

    @Test
    void suggestRecipes_returnsNonEmptyList() {
        assertFalse(recipeService.suggestRecipes(manager).isEmpty());
    }

    @Test
    void suggestRecipes_containsVeggiePan() {
        assertTrue(recipeService.suggestRecipes(manager).stream()
                .anyMatch(r -> r.getName().equals("Veggie Pan")));
    }

    // ---- createChaosPan ----

    @Test
    void createChaosPan_returnsRecipes_whenExpiringSoonExists() {
        assertFalse(recipeService.createChaosPan(manager).isEmpty());
    }

    @Test
    void createChaosPan_returnsEmptyList_whenNothingExpiresSoon() {
        FridgeManager fresh = new FridgeManager();
        fresh.addProduct(new Product("Apple", ProductCategory.FRUITS_VEGETABLES,
                1.0, "kg", LocalDate.now().plusDays(30), 0.5));
        assertTrue(recipeService.createChaosPan(fresh).isEmpty());
    }

    // ---- getMissingIngredients ----

    @Test
    void getMissingIngredients_returnsOnlyMissingOnes() {
        // Fridge has: Carrot, Milk. Recipe needs: Carrot, Zucchini, Olive Oil.
        Recipe recipe = new Recipe("Veggie Pan", "Stir-fry.",
                Arrays.asList("Carrot", "Zucchini", "Olive Oil"),
                "15 min", "test");
        List<String> missing = recipeService.getMissingIngredients(recipe, manager);
        assertFalse(missing.contains("Carrot"));
        assertTrue(missing.contains("Zucchini"));
        assertTrue(missing.contains("Olive Oil"));
    }

    @Test
    void getMissingIngredients_returnsEmptyList_whenAllPresent() {
        Recipe recipe = new Recipe("Simple", "Mix.",
                Arrays.asList("Carrot", "Milk"),
                "5 min", "test");
        assertTrue(recipeService.getMissingIngredients(recipe, manager).isEmpty());
    }

    // ---- getProviderName ----

    @Test
    void getProviderName_returnsStubProviderName() {
        assertEquals("StubProvider", recipeService.getProviderName());
    }
}
