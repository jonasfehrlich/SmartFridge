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
 * Unit tests specifically for the Chaos Pan feature (RecipeService.createChaosPan).
 * Uses a stub RecipeProvider — no real HTTP calls are made.
 *
 * Covered lecture topics:
 *  - 2.1.3: Polymorphism / interface via RecipeProvider.
 *  - 2.1.4: Late binding — provider is injected at runtime (test constructor).
 *  - 2.1.8: Generics — List<Recipe>, List<String>.
 */
class ChaosPanServiceTest {

    /** Stub: echoes the received ingredient list back as recipe ingredients. */
    private static class StubRecipeProvider implements RecipeProvider {
        private List<String> lastIngredients;

        @Override
        public List<Recipe> findRecipes(List<String> ingredients) {
            this.lastIngredients = ingredients;
            return Arrays.asList(
                new Recipe("Chaos Veggie Pan", "Use up what's left.",
                        new java.util.ArrayList<>(ingredients), "10 min", "stub"),
                new Recipe("Quick Stir-Fry", "Fast and easy.",
                        new java.util.ArrayList<>(ingredients), "15 min", "stub")
            );
        }

        public List<String> getLastIngredients() { return lastIngredients; }

        @Override
        public String getProviderName() { return "StubProvider"; }
    }

    private StubRecipeProvider stub;
    private RecipeService recipeService;
    private FridgeManager manager;

    @BeforeEach
    void setUp() {
        stub          = new StubRecipeProvider();
        recipeService = new RecipeService(stub);
        manager       = new FridgeManager();
    }

    // ---- createChaosPan — products expiring soon ----

    @Test
    void createChaosPan_returnsRecipes_whenProductExpiresTomorrow() {
        manager.addProduct(new Product("Sausage", ProductCategory.MEAT_FISH,
                0.3, "kg", LocalDate.now().plusDays(1), 0.1));
        List<Recipe> result = recipeService.createChaosPan(manager);
        assertFalse(result.isEmpty());
    }

    @Test
    void createChaosPan_returnsRecipes_whenProductExpiresInFiveDays() {
        manager.addProduct(new Product("Spinach", ProductCategory.FRUITS_VEGETABLES,
                0.2, "kg", LocalDate.now().plusDays(5), 0.1));
        List<Recipe> result = recipeService.createChaosPan(manager);
        assertFalse(result.isEmpty());
    }

    @Test
    void createChaosPan_returnsEmptyList_whenNoProductExpiresSoon() {
        manager.addProduct(new Product("Apple", ProductCategory.FRUITS_VEGETABLES,
                1.0, "kg", LocalDate.now().plusDays(30), 0.5));
        List<Recipe> result = recipeService.createChaosPan(manager);
        assertTrue(result.isEmpty());
    }

    @Test
    void createChaosPan_returnsEmptyList_whenFridgeIsEmpty() {
        assertTrue(recipeService.createChaosPan(manager).isEmpty());
    }

    // ---- createChaosPan — ingredient list passed to provider ----

    @Test
    void createChaosPan_passesExpiringProductNameToProvider() {
        manager.addProduct(new Product("Carrot", ProductCategory.FRUITS_VEGETABLES,
                0.3, "kg", LocalDate.now().plusDays(2), 0.1));
        recipeService.createChaosPan(manager);
        assertTrue(stub.getLastIngredients().contains("Carrot"));
    }

    @Test
    void createChaosPan_doesNotPassFreshProductToProvider() {
        manager.addProduct(new Product("Carrot", ProductCategory.FRUITS_VEGETABLES,
                0.3, "kg", LocalDate.now().plusDays(2), 0.1));
        manager.addProduct(new Product("Butter", ProductCategory.DAIRY,
                0.5, "kg", LocalDate.now().plusDays(20), 0.1));
        recipeService.createChaosPan(manager);
        // Only Carrot (expiring in 2 days) should be sent — not Butter (20 days)
        assertFalse(stub.getLastIngredients().contains("Butter"));
    }

    // ---- multiple expiring products ----

    @Test
    void createChaosPan_includesAllExpiringProducts_inIngredients() {
        manager.addProduct(new Product("Zucchini", ProductCategory.FRUITS_VEGETABLES,
                0.4, "kg", LocalDate.now().plusDays(3), 0.1));
        manager.addProduct(new Product("Tomato", ProductCategory.FRUITS_VEGETABLES,
                0.3, "kg", LocalDate.now().plusDays(4), 0.1));
        recipeService.createChaosPan(manager);
        assertTrue(stub.getLastIngredients().contains("Zucchini"));
        assertTrue(stub.getLastIngredients().contains("Tomato"));
    }

    // ---- recipe content ----

    @Test
    void createChaosPan_returnsRecipeWithCorrectName() {
        manager.addProduct(new Product("Pepper", ProductCategory.FRUITS_VEGETABLES,
                0.2, "kg", LocalDate.now().plusDays(1), 0.1));
        List<Recipe> recipes = recipeService.createChaosPan(manager);
        assertTrue(recipes.stream().anyMatch(r -> r.getName().equals("Chaos Veggie Pan")));
    }
}
