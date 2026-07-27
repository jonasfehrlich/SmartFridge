package de.hwrberlin.kuehlschrank.service;
import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.util.DataStorage;
import de.hwrberlin.kuehlschrank.util.FridgeException;
import java.io.Serializable;
import java.util.*;

/**
 * Central management class for the fridge.
 * Lecture 2.1.7: HashMap, ArrayList, HashSet, for-each loops.
 * Lecture 2.1.8: typed collections with diamond operator.
 */
public class FridgeManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FILE = "data/fridge.json";

    private HashMap<String, Product> products = new HashMap<>();
    private HashSet<ProductCategory> existingCategories = new HashSet<>();

    /** Adds a product to the fridge. Throws {@link FridgeException} if {@code product} is null. */
    public void addProduct(Product product) {
        if (product == null || product.getName() == null)
            throw new FridgeException("Product must not be null.");
        products.put(product.getName().toLowerCase(), product);
        existingCategories.add(product.getCategory());
    }

    /** Removes the product with the given name. Returns true if it existed. */
    public boolean removeProduct(String name) {
        return products.remove(name.toLowerCase()) != null;
    }

    /** Finds a product by name (case-insensitive). Returns null if not found. */
    public Product findProduct(String name) {
        return products.get(name.toLowerCase());
    }

    /** Returns all products as a new list. */
    public ArrayList<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    /** Returns all products of the given category. */
    public ArrayList<Product> getProductsByCategory(ProductCategory category) {
        ArrayList<Product> result = new ArrayList<>();
        for (Product p : products.values())
            if (p.getCategory() == category) result.add(p);
        return result;
    }

    /** Returns all products that expire within the next {@code days} days, sorted by expiry date. */
    public ArrayList<Product> getExpiringSoon(int days) {
        ArrayList<Product> list = new ArrayList<>();
        for (Product p : products.values())
            if (p.expiresSoon(days)) list.add(p);
        Collections.sort(list);
        return list;
    }

    /** Returns all products whose expiry date has already passed. */
    public ArrayList<Product> getExpiredProducts() {
        ArrayList<Product> list = new ArrayList<>();
        for (Product p : products.values())
            if (p.isExpired()) list.add(p);
        return list;
    }

    /** Returns all products whose quantity is below their minimum quantity. */
    public ArrayList<Product> getProductsNeedingRestock() {
        ArrayList<Product> list = new ArrayList<>();
        for (Product p : products.values())
            if (p.needsRestock()) list.add(p);
        return list;
    }

    /** Returns a copy of all categories currently present in the fridge. */
    public HashSet<ProductCategory> getExistingCategories() {
        return new HashSet<>(existingCategories);
    }

    /** Returns the total number of products in the fridge. */
    public int getProductCount() { return products.size(); }

    public void save() { DataStorage.save(this, FILE); }

    public static FridgeManager load() {
        FridgeManager m = DataStorage.load(FILE, FridgeManager.class);
        return m != null ? m : new FridgeManager();
    }
}
