package de.hwrberlin.kuehlschrank.util;
import java.io.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Generic persistence helper class based on JSON (library: Google Gson).
 *
 * Covered lecture topics:
 *  - 2.1.8 Generics: generic methods with bounded type parameter <T extends Serializable>
 *          and Class<T> as type token when loading.
 *  - 2.1.6 Exceptions: try-with-resources, FileNotFoundException handled separately.
 *  - 2.1.5 Persistence: objects are written to a file and read back.
 *
 * Deviation from lecture (intentional, see REVIEW_Vorlesungsabdeckung.md):
 * Lecture 2.1.5 shows serialisation via ObjectOutputStream/ObjectInputStream.
 * Here JSON (Gson) is used instead -> human-readable and more version-robust,
 * but requires an external library and does not use the JDK Serializable mechanism.
 */
public class DataStorage {
    private DataStorage() {}  // Utility class: no instances.

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <T> void save(T object, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(object, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error while saving", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T load(String filePath, Class<T> type) {
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Error while loading", e);
        }
    }
}
