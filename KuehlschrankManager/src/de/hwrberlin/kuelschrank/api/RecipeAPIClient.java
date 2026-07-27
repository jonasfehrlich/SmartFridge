package de.hwrberlin.kuelschrank.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import de.hwrberlin.kuehlschrank.util.APIException;
import de.hwrberlin.kuehlschrank.util.DataStorage;
import de.hwrberlin.kuelschrank.api.model.Ingredient;
import de.hwrberlin.kuelschrank.api.model.RecipeDetails;
import de.hwrberlin.kuehlschrank.model.*;

/**
 * HTTP client for the Spoonacular Recipe API.
 * Searches recipes by available fridge products and loads full recipe details.
 *
 * Covered lecture topics:
 *  - 2.1.3: Encapsulation (private fields, public methods).
 *  - 2.1.4: Exception handling via APIException.
 */
public class RecipeAPIClient {

    private final HttpClient httpClient;
    private final String apiKey;
    private final String baseUrl = "https://api.spoonacular.com";

    public RecipeAPIClient(String apiKey) {
        this.apiKey     = apiKey;
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Searches for recipes that use the given fridge products.
     *
     * @param products list of fridge products whose names are sent as ingredients
     * @param count    maximum number of results to return
     * @return list of matching RecipeSearchResult objects
     * @throws APIException if the HTTP request fails or is interrupted
     */
    public List<RecipeSearchResult> searchRecipes(List<FridgeProduct> products, int count) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(buildUri(products, count)))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();
            Type type = new TypeToken<List<RecipeSearchResult>>() {}.getType();
            return DataStorage.fromJson(json, type);
        } catch (IOException e) {
            throw new APIException("Could not connect to the Recipe API.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new APIException("Connection to the Recipe API was interrupted.", e);
        }
    }

    /**
     * Loads the full details of a recipe by its Spoonacular ID.
     *
     * @param recipeId the Spoonacular recipe ID
     * @return a fully populated Recipe object
     * @throws APIException if the HTTP request fails or is interrupted
     */
    public Recipe loadRecipe(int recipeId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(buildDetailUri(recipeId)))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());
            return parseRecipeFromJson(response.body());
        } catch (IOException e) {
            throw new APIException("Could not connect to the Recipe API.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new APIException("Connection to the Recipe API was interrupted.", e);
        }
    }

    // ---- private helpers ----

    private String buildIngredientList(List<FridgeProduct> products) {
        StringBuilder sb = new StringBuilder();
        for (FridgeProduct product : products) {
            if (!sb.isEmpty()) sb.append(',');
            sb.append(product.getName());
        }
        return sb.toString();
    }

    private String buildUri(List<FridgeProduct> products, int count) {
        return baseUrl
                + "/recipes/findByIngredients"
                + "?ingredients=" + buildIngredientList(products)
                + "&number=" + count
                + "&apiKey=" + apiKey;
    }

    private String buildDetailUri(int recipeId) {
        return baseUrl
                + "/recipes/" + recipeId
                + "/information"
                + "?apiKey=" + apiKey;
    }

    private List<String> extractIngredients(List<Ingredient> ingredients) {
        List<String> list = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            list.add(ingredient.getOriginal());
        }
        return list;
    }

    private Recipe parseRecipeFromJson(String json) {
        RecipeDetails details = DataStorage.fromJson(json, RecipeDetails.class);
        List<String> ingredients = extractIngredients(details.getExtendedIngredients());
        return new Recipe(
                details.getTitle(),
                stripHtmlTags(details.getInstructions()),
                ingredients,
                details.getReadyInMinutes() + " min",
                details.getSourceUrl()
        );
    }

    private String stripHtmlTags(String text) {
        if (text == null) return "";
        return text.replaceAll("<[^>]*>", "");
    }
}
