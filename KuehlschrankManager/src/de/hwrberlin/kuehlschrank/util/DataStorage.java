package de.hwrberlin.kuehlschrank.util;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Generic persistence helper class based on JSON (library: Google Gson).
 *
 * Covered lecture topics:
 *  - 2.1.8 Generics: generic methods with type parameter <T> and Class<T> as type token.
 *  - 2.1.6 Exceptions: try-with-resources, FileNotFoundException handled separately.
 *  - 2.1.5 Persistence: objects are written to a file and read back.
 *
 * Note: JSON (Gson) is used instead of ObjectOutputStream as it is human-readable
 * and more version-robust, but requires an external library.
 */
public class DataStorage {
    private DataStorage() {} // Utility class – no instances.

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Serialises {@code object} to JSON and writes it to {@code filePath}. */
    public static <T> void save(T object, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            GSON.toJson(object, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error while saving: " + filePath, e);
        }
    }

    /** Reads JSON from {@code filePath} and deserialises it into an object of type {@code type}. */
    @SuppressWarnings("unchecked")
    public static <T> T load(String filePath, Class<T> type) {
        try (FileReader reader = new FileReader(filePath)) {
            return GSON.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            return null; // File does not exist yet -> return null, caller handles it.
        } catch (IOException e) {
            throw new RuntimeException("Error while loading: " + filePath, e);
        }
    }
}
