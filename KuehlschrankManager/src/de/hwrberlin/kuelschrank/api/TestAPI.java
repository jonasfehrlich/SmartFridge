package de.hwrberlin.kuelschrank.api;

import java.util.LinkedList;
import java.util.List;

import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.service.*;

/**
 * Manual integration test / smoke test for RecipeAPIClient.
 * Run this class directly to verify that the Spoonacular API key works
 * and that JSON parsing produces the expected Recipe objects.
 *
 * NOTE: This class is NOT a JUnit test. It is a main-method runner
 * intended for quick manual verification during development.
 */
public class TestAPI {

    public static void main(String[] args) {
        RecipeAPIClient client = new RecipeAPIClient("33f9c011c2a14681b1bb71041e3f4081");

        FridgeProduct testItem1 = new FridgeProduct("tomato", null, 1, "piece", null, 2, null);
        FridgeProduct testItem2 = new FridgeProduct("cheese", null, 1, "piece", null, 2, null);
        LinkedList<FridgeProduct> productList = new LinkedList<>();
        productList.add(testItem1);
        productList.add(testItem2);

        List<RecipeSearchResult> results = client.searchRecipes(productList, 2);

        for (RecipeSearchResult result : results) {
            System.out.println(result.toString());
        }

        Recipe recipe = client.loadRecipe(results.get(0).getId());

        System.out.println(recipe.getName());
        System.out.println();
        System.out.println(recipe.getDescription());
        System.out.println();
        System.out.println(recipe.getPreparationTime());
        System.out.println();
        System.out.println(recipe.getSource());
        System.out.println();
        System.out.println(recipe.getIngredients());
    }
}
