package de.hwrberlin.kuehlschrank.service;
import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.util.DataStorage;
import java.io.Serializable;
import java.util.*;

/**
 * Manages the shopping list. Automatically generates entries from restock needs.
 * Lecture 2.1.7: ArrayList, removeIf.
 */
public class ShoppingListService implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FILE = "data/shoppinglist.json";
    private ArrayList<ShoppingItem> items = new ArrayList<>();

    /**
     * Clears the current list and regenerates it from all products that need restocking.
     * This is the fix for the shopping-list update bug: the list is always cleared first,
     * then rebuilt from the current fridge state, so the UI always reflects reality.
     */
    public void generateList(FridgeManager manager) {
    	if (manager==null) {
    		return;
    		}
        for (Product p : manager.getProductsNeedingRestock()) {
            if (!isAlreadyOnList(p.getName())) {
                double missing = Math.ceil(p.getMinimumQuantity() - p.getQuantity());
                items.add(new ShoppingItem(p.getName(), missing, p.getUnit(), p.getCategory()));
            }
        }
    }

    /** Adds an item manually. Silently ignores duplicates (same product name). */
    public void addItem(ShoppingItem item) {
        if (!isAlreadyOnList(item.getProductName())) {
            items.add(item);
        }
    }

    /** Marks the item with the given name as purchased (case-insensitive). Returns false if not found. */
    public boolean markAsPurchased(String productName) {
        for (ShoppingItem item : items) {
            if (item.getProductName().equalsIgnoreCase(productName)) {
                item.setPurchased(true);
                return true;
            }
        }
        return false;
    }

    /** Removes all items that have been marked as purchased. */
    public void removePurchased() { items.removeIf(ShoppingItem::isPurchased); }

    /** Returns a copy of all items on the list. */
    public ArrayList<ShoppingItem> getItems() {
        return new ArrayList<>(items);
    }

    /** Returns only the items that have not been purchased yet. */
    public List<ShoppingItem> getOpenItems() {
        List<ShoppingItem> open = new ArrayList<>();
        for (ShoppingItem item : items) if (!item.isPurchased()) open.add(item);
        return open;
    }

    private boolean isAlreadyOnList(String name) {
        for (ShoppingItem item : items)
            if (item.getProductName().equalsIgnoreCase(name)) return true;
        return false;
    }

    public void save() { DataStorage.save(this, FILE); }

    public static ShoppingListService load() {
        ShoppingListService s = DataStorage.load(FILE, ShoppingListService.class);
        return s != null ? s : new ShoppingListService();
    }
}
