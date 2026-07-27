package de.hwrberlin.kuehlschrank.service;
import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.util.DataStorage;
import de.hwrberlin.kuehlschrank.util.FridgeException;
import java.io.Serializable;
import java.util.*;

/**
 * Central management class for the fridge.
 * Lecture 2.1.7: HashMap, ArrayList, HashSet, for-each loops.
 * Lecture 2.1.8: Typed collections with diamond operator.
 */
public class FridgeManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FILE = "data/fridge.json";

    private HashMap<String, Product>         products           = new HashMap<>();
    private HashSet<ProductCategory>         presentCategories  = new HashSet<>();

    public void addProduct(Product product) {
        if (product == null || product.getName() == null)
            throw new FridgeException("Product must not be null.");
        products.put(product.getName().toLowerCase(), product);
        presentCategories.add(product.getCategory());
    }

    public boolean removeProduct(String name) {
        return products.remove(name.toLowerCase()) != null;
    }

    public Product findProduct(String name) {
        return products.get(name.toLowerCase());
    }

    public ArrayList<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public ArrayList<Product> getProductsByCategory(ProductCategory category) {
        ArrayList<Product> result = new ArrayList<>();
        for (Product p : products.values())
            if (p.getCategory() == category) result.add(p);
        return result;
    }

    public ArrayList<Product> getExpiringSoon(int days) {
        ArrayList<Product> list = new ArrayList<>();
        for (Product p : products.values())
            if (p.expiresSoon(days)) list.add(p);
        Collections.sort(list);
        return list;
    }

    public ArrayList<Product> getExpiredProducts() {
        ArrayList<Product> list = new ArrayList<>();
        for (Product p : products.values())
            if (p.isExpired()) list.add(p);
        return list;
    }

    public ArrayList<Product> getProductsNeedingRestock() {
        ArrayList<Product> list = new ArrayList<>();
        for (Product p : products.values())
            if (p.needsRestocking()) list.add(p);
        return list;
    }

    public HashSet<ProductCategory> getPresentCategories() {
        return new HashSet<>(presentCategories);
    }

    public int getProductCount()    { return products.size(); }

    public void save()              { DataStorage.save(this, FILE); }

    public static FridgeManager load() {
        FridgeManager m = DataStorage.load(FILE, FridgeManager.class);
        return m != null ? m : new FridgeManager();
    }
}
