package de.hwrberlin.kuehlschrank.service;
import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.util.DataStorage;
import java.io.Serializable;
import java.util.*;

/**
 * Manages the shopping list and generates it automatically from restocking needs.
 * Lecture 2.1.7: ArrayList, removeIf.
 */
public class ShoppingListService implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FILE = "data/shoppinglist.json";
    private ArrayList<ShoppingItem> items = new ArrayList<>();

    /** Regenerates the list from current fridge stock. */
    public void generateList(FridgeManager manager) {
        items.clear();
        for (Product p : manager.getProductsNeedingRestock()) {
            if (!isAlreadyOnList(p.getName())) {
                double missing = Math.ceil(p.getMinimumAmount() - p.getAmount());
                items.add(new ShoppingItem(
                    p.getName(), missing, p.getUnit(), p.getCategory()));
            }
        }
    }

    public void addItem(ShoppingItem item) {
        if (!isAlreadyOnList(item.getProductName()))
            items.add(item);
    }

    public boolean markAsPurchased(String productName) {
        for (ShoppingItem item : items) {
            if (item.getProductName().equalsIgnoreCase(productName)) {
                item.setPurchased(true);
                return true;
            }
        }
        return false;
    }

    public void removePurchased() { items.removeIf(ShoppingItem::isPurchased); }

    public ArrayList<ShoppingItem> getItems()       { return new ArrayList<>(items); }

    public List<ShoppingItem> getOpenItems() {
        List<ShoppingItem> open = new ArrayList<>();
        for (ShoppingItem item : items)
            if (!item.isPurchased()) open.add(item);
        return open;
    }

    private boolean isAlreadyOnList(String name) {
        for (ShoppingItem item : items)
            if (item.getProductName().equalsIgnoreCase(name)) return true;
        return false;
    }

    public void save()                              { DataStorage.save(this, FILE); }

    public static ShoppingListService load() {
        ShoppingListService s = DataStorage.load(FILE, ShoppingListService.class);
        return s != null ? s : new ShoppingListService();
    }
}
