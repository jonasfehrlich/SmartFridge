package de.hwrberlin.kuehlschrank;

import de.hwrberlin.kuehlschrank.model.RecipeSearchResult;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RecipeSearchResult.
 * Verifies correct storage and retrieval of all DTO fields.
 *
 * Covered lecture topics:
 *  - 2.1.3: data-holding class (DTO); constructor, getters.
 *  - 2.1.8: Generics – typed lists (List<String>).
 */
class RecipeSearchResultTest {

    private RecipeSearchResult buildResult() {
        return new RecipeSearchResult(
                42,
                "Veggie Stir-Fry",
                Arrays.asList("Carrot", "Zucchini"),
                Arrays.asList("Olive Oil", "Garlic")
        );
    }

    // ---- constructor / getters ----

    @Test
    void getId_returnsCorrectId() {
        assertEquals(42, buildResult().getId());
    }

    @Test
    void getTitle_returnsCorrectTitle() {
        assertEquals("Veggie Stir-Fry", buildResult().getTitle());
    }

    @Test
    void getUsedIngredients_returnsBothEntries() {
        List<String> used = buildResult().getUsedIngredients();
        assertEquals(2, used.size());
        assertTrue(used.contains("Carrot"));
        assertTrue(used.contains("Zucchini"));
    }

    @Test
    void getMissedIngredients_returnsBothEntries() {
        List<String> missed = buildResult().getMissedIngredients();
        assertEquals(2, missed.size());
        assertTrue(missed.contains("Olive Oil"));
        assertTrue(missed.contains("Garlic"));
    }

    @Test
    void getUsedIngredients_emptyList_whenNoUsed() {
        RecipeSearchResult r = new RecipeSearchResult(
                1, "Plain", List.of(), Arrays.asList("Salt"));
        assertTrue(r.getUsedIngredients().isEmpty());
    }

    @Test
    void getMissedIngredients_emptyList_whenAllPresent() {
        RecipeSearchResult r = new RecipeSearchResult(
                2, "Full Recipe", Arrays.asList("Egg", "Milk"), List.of());
        assertTrue(r.getMissedIngredients().isEmpty());
    }

    // ---- toString ----

    @Test
    void toString_containsIdAndTitle() {
        String s = buildResult().toString();
        assertTrue(s.contains("42"));
        assertTrue(s.contains("Veggie Stir-Fry"));
    }
}
