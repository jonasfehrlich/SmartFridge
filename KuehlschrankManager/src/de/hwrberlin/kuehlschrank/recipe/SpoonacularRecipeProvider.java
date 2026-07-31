package de.hwrberlin.kuehlschrank.recipe;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import de.hwrberlin.kuehlschrank.model.FridgeProduct;
import de.hwrberlin.kuehlschrank.model.ProductCategory;
import de.hwrberlin.kuehlschrank.model.Recipe;
import de.hwrberlin.kuehlschrank.model.RecipeSearchResult;
import de.hwrberlin.kuelschrank.api.RecipeAPIClient;

public class SpoonacularRecipeProvider implements RecipeProvider {

    private static final int MAX_RESULTS = 10;

    private final RecipeAPIClient apiClient;

    public SpoonacularRecipeProvider(String apiKey) {
        this.apiClient = new RecipeAPIClient(apiKey);
    }

    @Override
    public List<Recipe> findRecipes(List<String> availableIngredients) {
        List<FridgeProduct> products =
                convertToFridgeProducts(availableIngredients);

        List<RecipeSearchResult> searchResults =
                apiClient.searchRecipes(products, MAX_RESULTS);

        List<Recipe> recipes = new ArrayList<>();

        for (RecipeSearchResult searchResult : searchResults) {
            Recipe recipe = apiClient.loadRecipe(searchResult.getId());
            recipes.add(recipe);
        }

        return recipes;
    }

    @Override
    public String getProviderName() {
        return "Spoonacular API";
    }

    private List<FridgeProduct> convertToFridgeProducts(
            List<String> ingredientNames) {

        List<FridgeProduct> products = new ArrayList<>();

        if (ingredientNames == null) {
            return products;
        }

        for (String ingredientName : ingredientNames) {
            if (ingredientName == null || ingredientName.isBlank()) {
                continue;
            }

            FridgeProduct product = new FridgeProduct(
                    ingredientName,
                    ProductCategory.OTHER,
                    1,
                    "pcs",
                    LocalDate.now(),
                    0,
                    "Fridge"
            );

            products.add(product);
        }

        return products;
    }
}
