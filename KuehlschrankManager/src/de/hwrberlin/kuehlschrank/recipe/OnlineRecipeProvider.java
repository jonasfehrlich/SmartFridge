package de.hwrberlin.kuehlschrank.recipe;

import de.hwrberlin.kuehlschrank.model.Recipe;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Fetches recipes from TheMealDB (free, no API key required).
 *
 * Fix log:
 *  - Bug 1 fixed: recipe ingredients now come from the API, not from the fridge list.
 *  - Bug 2 fixed: a second API call (/lookup.php) fetches real instructions per recipe.
 *  - Bug 3 fixed: searches the top-3 most relevant (food-only) ingredients and
 *                 prefers recipes that match the most of them.
 *
 * Lecture 2.1.4: implements RecipeProvider (late binding / polymorphism).
 * Uses only standard JDK – no external libraries.
 *
 * Endpoints used:
 *   filter : https://www.themealdb.com/api/json/v1/1/filter.php?i={ingredient}
 *   detail : https://www.themealdb.com/api/json/v1/1/lookup.php?i={mealId}
 */
public class OnlineRecipeProvider implements RecipeProvider {

    private static final String BASE      = "https://www.themealdb.com/api/json/v1/1/";
    private static final String FILTER    = BASE + "filter.php?i=";
    private static final String LOOKUP    = BASE + "lookup.php?i=";
    private static final int    MAX_RESULTS = 6;
    private static final int    TIMEOUT_MS  = 5000;

    /**
     * Known non-food categories that should never be sent to a recipe API.
     * Extend this list if needed.
     */
    private static final Set<String> NON_FOOD_KEYWORDS = new HashSet<>(Arrays.asList(
        "insulin", "medication", "medicine", "tablet", "capsule", "pill",
        "syrup", "drops", "injection", "cream", "ointment", "spray"
    ));

    // -------------------------------------------------------------------------
    // RecipeProvider interface
    // -------------------------------------------------------------------------

    @Override
    public List<Recipe> findRecipes(List<String> ingredients) {
        List<Recipe> result = new ArrayList<>();
        if (ingredients == null || ingredients.isEmpty()) return result;

        // 1. Filter out non-food items (medications etc.)
        List<String> foodOnly = filterFoodItems(ingredients);
        if (foodOnly.isEmpty()) return result;

        // 2. Search up to 3 ingredients, collect candidate meal IDs with a score
        //    (score = how many of our ingredients matched this meal)
        Map<String, Integer> scoreMap = new LinkedHashMap<>();
        int searched = 0;
        for (String ingredient : foodOnly) {
            if (searched >= 3) break;
            try {
                List<String> ids = fetchMealIds(ingredient);
                for (String id : ids) {
                    scoreMap.merge(id, 1, Integer::sum);
                }
                searched++;
            } catch (Exception e) {
                System.err.println("[OnlineRecipeProvider] filter call failed for '"
                        + ingredient + "': " + e.getMessage());
            }
        }

        if (scoreMap.isEmpty()) return result;

        // 3. Sort by score descending so best matches come first
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(scoreMap.entrySet());
        sorted.sort((a, b) -> b.getValue() - a.getValue());

        // 4. Fetch full details for the top candidates
        for (Map.Entry<String, Integer> entry : sorted) {
            if (result.size() >= MAX_RESULTS) break;
            try {
                Recipe r = fetchRecipeDetail(entry.getKey());
                if (r != null) result.add(r);
            } catch (Exception e) {
                System.err.println("[OnlineRecipeProvider] detail call failed for id="
                        + entry.getKey() + ": " + e.getMessage());
            }
        }
        return result;
    }

    @Override
    public String getProviderName() { return "TheMealDB (Online)"; }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Returns meal IDs returned by the filter endpoint for one ingredient. */
    private List<String> fetchMealIds(String ingredient) throws Exception {
        String json = httpGet(FILTER + urlEncode(ingredient));
        List<String> ids = new ArrayList<>();
        if (json == null || json.contains("\"meals\":null")) return ids;
        // Extract all "idMeal":"12345" values
        String[] parts = json.split("\"idMeal\":\"");
        for (int i = 1; i < parts.length; i++) {
            String id = parts[i].split("\"")[0].trim();
            if (!id.isEmpty()) ids.add(id);
        }
        return ids;
    }

    /**
     * Fetches full recipe details from /lookup.php and builds a Recipe object
     * with REAL ingredients (from the API) and instructions.
     */
    private Recipe fetchRecipeDetail(String mealId) throws Exception {
        String json = httpGet(LOOKUP + mealId);
        if (json == null || json.contains("\"meals\":null")) return null;

        String name         = extractField(json, "strMeal");
        String instructions = extractField(json, "strInstructions");
        String category     = extractField(json, "strCategory");
        String area         = extractField(json, "strArea");
        String source       = extractField(json, "strSource");
        String youtube      = extractField(json, "strYoutube");

        if (name == null || name.isBlank()) return null;

        // Shorten instructions for display (first 3 sentences or 300 chars)
        String shortInstructions = shortenInstructions(instructions);

        // Build preparation time label from category/area (TheMealDB has no time field)
        String prepTime = buildPrepLabel(category, area);

        // Extract real ingredient list (strIngredient1 .. strIngredient20)
        List<String> recipeIngredients = extractIngredients(json);

        // Build source URL (prefer strSource, fall back to YouTube)
        String sourceUrl = (source != null && !source.isBlank()) ? source
                         : (youtube != null && !youtube.isBlank()) ? youtube
                         : "https://www.themealdb.com/meal/" + mealId;

        return new Recipe(name, shortInstructions, recipeIngredients, prepTime, sourceUrl);
    }

    /** Extracts strIngredient1..strIngredient20 from the JSON, skipping empty values. */
    private List<String> extractIngredients(String json) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String ingredient = extractField(json, "strIngredient" + i);
            String measure    = extractField(json, "strMeasure" + i);
            if (ingredient != null && !ingredient.isBlank()) {
                String entry = ingredient.trim();
                if (measure != null && !measure.isBlank()) {
                    entry = measure.trim() + " " + entry;
                }
                list.add(entry);
            }
        }
        return list;
    }

    /** Extracts the string value of a named JSON field (simple, no external library). */
    private String extractField(String json, String fieldName) {
        String needle = "\"" + fieldName + "\":\"";
        int start = json.indexOf(needle);
        if (start < 0) return null;
        start += needle.length();
        // Handle escaped quotes inside the value
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                if (next == '"') { sb.append('"'); i++; continue; }
                if (next == 'n')  { sb.append('\n'); i++; continue; }
                if (next == 'r')  { i++; continue; }
                if (next == '\\') { sb.append('\\'); i++; continue; }
            }
            if (c == '"') break;
            sb.append(c);
        }
        return sb.toString();
    }

    /** Returns the first ~300 characters / 3 sentences of the instructions. */
    private String shortenInstructions(String text) {
        if (text == null || text.isBlank()) return "See source link for full instructions.";
        String cleaned = text.replace("\r\n", "\n").replace("\r", "\n").trim();
        // Take first 3 sentences
        String[] sentences = cleaned.split("(?<=[.!?])\\s+");
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String s : sentences) {
            if (count >= 3 || sb.length() > 300) break;
            sb.append(s.trim()).append(" ");
            count++;
        }
        String result = sb.toString().trim();
        if (result.length() < cleaned.length()) result += "\n\n[...] See source for full recipe.";
        return result;
    }

    /** Builds a human-readable prep-time / category label. */
    private String buildPrepLabel(String category, String area) {
        if (category != null && !category.isBlank() && area != null && !area.isBlank())
            return category + " • " + area;
        if (category != null && !category.isBlank()) return category;
        if (area     != null && !area.isBlank())     return area;
        return "Online recipe";
    }

    /** Filters the ingredient list to food items only (removes medications etc.). */
    private List<String> filterFoodItems(List<String> ingredients) {
        List<String> food = new ArrayList<>();
        for (String s : ingredients) {
            if (s == null || s.isBlank()) continue;
            String lower = s.toLowerCase(Locale.ROOT);
            boolean isNonFood = false;
            for (String kw : NON_FOOD_KEYWORDS) {
                if (lower.contains(kw)) { isNonFood = true; break; }
            }
            if (!isNonFood) food.add(s);
        }
        return food;
    }

    private String urlEncode(String s) {
        return s.replace(" ", "%20").replace("&", "%26");
    }

    private String httpGet(String urlText) throws Exception {
        URL url = new URL(urlText);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setRequestProperty("User-Agent", "SmartFridgeApp/1.0");
        int status = conn.getResponseCode();
        if (status != 200) throw new IOException("HTTP " + status + " for " + urlText);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }
}
