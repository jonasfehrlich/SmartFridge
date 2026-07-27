package de.hwrberlin.kuehlschrank.util;
import java.io.*;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Generic persistence helper class based on JSON (library: Google Gson).
 *
 * Covered lecture topics:
 *  - 2.1.8 Generics: generic methods with type parameter <T> and Class<T> / Type as type token.
 *  - 2.1.6 Exceptions: try-with-resources, FileNotFoundException handled separately.
 *  - 2.1.5 Persistence: objects are written to a file and read back.
 *
 * Note: JSON (Gson) is used instead of ObjectOutputStream as it is human-readable
 * and more version-robust, but requires an external library.
 */
public class DataStorage {
    private DataStorage() {} // Utility class – no instances.

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // ---- File persistence ----

    /** Serialises {@code object} to JSON and writes it to {@code filePath}. */
    public static <T> void save(T object, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            GSON.toJson(object, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error while saving: " + filePath, e);
        }
    }

    /** Reads JSON from {@code filePath} and deserialises it into an object of type {@code type}. */
    public static <T> T load(String filePath, Class<T> type) {
        try (FileReader reader = new FileReader(filePath)) {
            return GSON.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            return null; // File does not exist yet -> return null, caller handles it.
        } catch (IOException e) {
            throw new RuntimeException("Error while loading: " + filePath, e);
        }
    }

    // ---- In-memory JSON parsing (used by API clients) ----

    /**
     * Parses a JSON string directly into an object of the given class.
     * Used when the JSON comes from an HTTP response body, not from a file.
     *
     * @param json  the raw JSON string
     * @param type  the target class (e.g. {@code RecipeDetails.class})
     * @return      the deserialised object, or {@code null} if json is null/empty
     */
    public static <T> T fromJson(String json, Class<T> type) {
        if (json == null || json.isBlank()) return null;
        return GSON.fromJson(json, type);
    }

    /**
     * Parses a JSON string into a generic type (e.g. {@code List<RecipeSearchResult>}).
     * Use a {@code TypeToken} to pass the correct generic type at runtime:
     * <pre>
     *   Type t = new TypeToken&lt;List&lt;RecipeSearchResult&gt;&gt;(){}.getType();
     *   List&lt;RecipeSearchResult&gt; list = DataStorage.fromJson(json, t);
     * </pre>
     *
     * Lecture 2.1.8: Type erasure workaround – TypeToken preserves generic type info.
     *
     * @param json  the raw JSON string
     * @param type  the generic {@link Type} obtained from a {@code TypeToken}
     * @return      the deserialised object
     */
    public static <T> T fromJson(String json, Type type) {
        if (json == null || json.isBlank()) return null;
        return GSON.fromJson(json, type);
    }
}
