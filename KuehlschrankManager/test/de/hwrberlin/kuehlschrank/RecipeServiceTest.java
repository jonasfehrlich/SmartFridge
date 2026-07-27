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
 * Uses a stub RecipeProvider (no real HTTP calls).
 * Covers: suggestRecipes, getMissingIngredients, createChaosPan, getProviderName.
 */
class RecipeServiceTest {

    /** Stub provider: returns fixed recipes regardless of input. */
    private static class StubRecipeProvider implements RecipeProvider {
        @Override
        public List<Recipe> findRecipes(List<String> ingredients) {
            return Arrays.asList(
                new Recipe("Veggie Pan",
                        Arrays.asList("Carrot", "Zucchini", "Olive Oil"),
                        "Fry all vegetables in olive oil.", "stub"),
                new Recipe("Milk Soup",
                        Arrays.asList("Milk", "Potato"),
                        "Boil potatoes in milk.", "stub")
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
        // Expires in 3 days -> appears in chaos pan (threshold = 5)
        manager.addProduct(new Product("Carrot", ProductCategory.VEGETABLE,
                0.3, "kg", LocalDate.now().plusDays(3), 0.1));
        // Fresh -> not prioritised in chaos pan
        manager.addProduct(new Product("Milk", ProductCategory.DAIRY,
                2.0, "L", LocalDate.now().plusDays(14), 1.0));
    }

    @Test
    void suggestRecipes_returnsNonEmptyList() {
        assertFalse(recipeService.suggestRecipes(manager).isEmpty());
    }

    @Test
    void suggestRecipes_returnsRecipesFromProvider() {
        assertTrue(recipeService.suggestRecipes(manager).stream()
                .anyMatch(r -> r.getName().equals("Veggie Pan")));
    }

    @Test
    void createChaosPan_returnsRecipes_whenExpiringSoonExists() {
        assertFalse(recipeService.createChaosPan(manager).isEmpty());
    }

    @Test
    void createChaosPan_returnsEmptyList_whenNothingExpiresSoon() {
        FridgeManager fresh = new FridgeManager();
        fresh.addProduct(new Product("Apple", ProductCategory.FRUIT,
                1.0, "kg", LocalDate.now().plusDays(30), 0.5));
        assertTrue(recipeService.createChaosPan(fresh).isEmpty());
    }

    @Test
    void getMissingIngredients_returnsOnlyMissingOnes() {
        Recipe recipe = new Recipe("Veggie Pan",
                Arrays.asList("Carrot", "Zucchini", "Olive Oil"),
                "Fry all vegetables.", "test");
        List<String> missing = recipeService.getMissingIngredients(recipe, manager);
        assertFalse(missing.contains("Carrot"));
        assertTrue(missing.contains("Zucchini"));
        assertTrue(missing.contains("Olive Oil"));
    }

    @Test
    void getMissingIngredients_returnsEmptyList_whenAllPresent() {
        Recipe recipe = new Recipe("Simple",
                Arrays.asList("Carrot", "Milk"),
                "Mix together.", "test");
        assertTrue(recipeService.getMissingIngredients(recipe, manager).isEmpty());
    }

    @Test
    void getProviderName_returnsStubProviderName() {
        assertEquals("StubProvider", recipeService.getProviderName());
    }
}
