package de.hwrberlin.kuehlschrank.rezept;
import de.hwrberlin.kuehlschrank.model.Recipe;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Fetches recipes from TheMealDB (free API, no key required).
 * Lecture 2.1.4: implements RecipeProvider (polymorphism via interface).
 * Uses only standard JDK: HttpURLConnection, no external framework.
 * Endpoint: https://www.themealdb.com/api/json/v1/1/filter.php?i={ingredient}
 */
public class OnlineRecipeProvider implements RecipeProvider {
    private static final String API =
        "https://www.themealdb.com/api/json/v1/1/filter.php?i=";

    @Override
    public List<Recipe> searchRecipes(List<String> ingredients) {
        List<Recipe> result = new ArrayList<>();
        if (ingredients == null || ingredients.isEmpty()) return result;
        try {
            String json = httpGet(API + ingredients.get(0).replace(" ", "%20"));
            result = parseJson(json, ingredients);
        } catch (Exception e) {
            System.err.println("Online search unavailable: " + e.getMessage());
        }
        return result;
    }

    private String httpGet(String urlText) throws Exception {
        URL url = new URL(urlText);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(4000);
        conn.setReadTimeout(4000);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private List<Recipe> parseJson(String json, List<String> ingredients) {
        List<Recipe> list = new ArrayList<>();
        if (json == null || json.contains("\"meals\":null")) return list;
        String[] parts = json.split("\"strMeal\":\"");
        for (int i = 1; i < parts.length && list.size() < 5; i++) {
            String name = parts[i].split("\"")[0];
            list.add(new Recipe(name, "Online recipe from TheMealDB",
                new ArrayList<>(ingredients), "See source", "TheMealDB"));
        }
        return list;
    }

    @Override
    public String getProviderName() { return "TheMealDB (Online)"; }
}
