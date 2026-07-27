package de.hwrberlin.kuehlschrank.util;

import com.google.gson.*;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import de.hwrberlin.kuehlschrank.model.FridgeProduct;
import de.hwrberlin.kuehlschrank.model.Product;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;

/**
 * JSON-based persistence helper using Gson.
 * Lecture 2.1.9: Exception handling (IOException, JsonParseException).
 */
public class DataStorage {

    private static final Gson GSON = buildGson();

    private static Gson buildGson() {
        // RuntimeTypeAdapterFactory so that FridgeProduct is deserialised correctly
        RuntimeTypeAdapterFactory<Product> rta =
            RuntimeTypeAdapterFactory.of(Product.class, "type")
                .registerSubtype(Product.class,      "Product")
                .registerSubtype(FridgeProduct.class, "FridgeProduct");

        return new GsonBuilder()
            .registerTypeAdapterFactory(rta)
            .registerTypeAdapter(LocalDate.class,
                (JsonSerializer<LocalDate>)   (src, t, ctx) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                (JsonDeserializer<LocalDate>) (json, t, ctx) -> LocalDate.parse(json.getAsString()))
            .setPrettyPrinting()
            .create();
    }

    public static <T> void save(T object, String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(object));
        } catch (IOException e) {
            System.err.println("Save failed: " + e.getMessage());
        }
    }

    public static <T> T load(String filePath, Class<T> type) {
        try {
            String json = Files.readString(Paths.get(filePath));
            return GSON.fromJson(json, type);
        } catch (IOException | JsonParseException e) {
            return null;
        }
    }
}
