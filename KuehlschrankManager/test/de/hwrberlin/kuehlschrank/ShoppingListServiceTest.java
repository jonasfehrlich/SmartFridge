package de.hwrberlin.kuehlschrank;

import de.hwrberlin.kuehlschrank.model.Product;
import de.hwrberlin.kuehlschrank.model.ProductCategory;
import de.hwrberlin.kuehlschrank.model.ShoppingItem;
import de.hwrberlin.kuehlschrank.service.FridgeManager;
import de.hwrberlin.kuehlschrank.service.ShoppingListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ShoppingListService.
 * Covers: generateList, addItem (no duplicates), markAsPurchased,
 *         removePurchased, getItems, getOpenItems.
 */
class ShoppingListServiceTest {

    private ShoppingListService shoppingListService;
    private FridgeManager manager;

    @BeforeEach
    void setUp() {
        shoppingListService = new ShoppingListService();
        manager = new FridgeManager();
        // carrot: 0.2 kg < 0.5 minimum -> needsRestock() -> appears in generated list
        manager.addProduct(new Product("Carrot", ProductCategory.FRUITS_VEGETABLES,
                0.2, "kg", LocalDate.now().plusDays(5), 0.5));
        // milk: 2.0 L >= 1.0 minimum -> no restock needed
        manager.addProduct(new Product("Milk", ProductCategory.DAIRY,
                2.0, "L", LocalDate.now().plusDays(7), 1.0));
    }

    // ---- generateList ----

    @Test
    void generateList_addsCarrot_notMilk() {
        shoppingListService.generateList(manager);
        assertTrue(shoppingListService.getItems().stream()
                .anyMatch(i -> i.getProductName().equals("Carrot")));
        assertTrue(shoppingListService.getItems().stream()
                .noneMatch(i -> i.getProductName().equals("Milk")));
    }

    @Test
    void generateList_clearsOldItems_beforeRegenerating() {
        shoppingListService.generateList(manager);
        shoppingListService.generateList(manager);
        long count = shoppingListService.getItems().stream()
                .filter(i -> i.getProductName().equals("Carrot")).count();
        assertEquals(1, count);
    }

    // ---- addItem ----

    @Test
    void addItem_addsNewItem_successfully() {
        shoppingListService.addItem(
                new ShoppingItem("Butter", 1.0, "pack", ProductCategory.DAIRY));
        assertEquals(1, shoppingListService.getItems().size());
    }

    @Test
    void addItem_doesNotAddDuplicate() {
        ShoppingItem item = new ShoppingItem("Butter", 1.0, "pack", ProductCategory.DAIRY);
        shoppingListService.addItem(item);
        shoppingListService.addItem(item);
        assertEquals(1, shoppingListService.getItems().size());
    }

    // ---- markAsPurchased ----

    @Test
    void markAsPurchased_returnsTrue_forExistingItem() {
        shoppingListService.generateList(manager);
        assertTrue(shoppingListService.markAsPurchased("Carrot"));
    }

    @Test
    void markAsPurchased_returnsFalse_forNonExistentItem() {
        assertFalse(shoppingListService.markAsPurchased("Unicorn"));
    }

    @Test
    void markAsPurchased_isCaseInsensitive() {
        shoppingListService.generateList(manager);
        assertTrue(shoppingListService.markAsPurchased("carrot"));
    }

    // ---- removePurchased ----

    @Test
    void removePurchased_removesOnlyPurchasedItems() {
        shoppingListService.generateList(manager);
        shoppingListService.addItem(
                new ShoppingItem("Butter", 1.0, "pack", ProductCategory.DAIRY));
        shoppingListService.markAsPurchased("Carrot");
        shoppingListService.removePurchased();
        assertTrue(shoppingListService.getItems().stream()
                .noneMatch(i -> i.getProductName().equals("Carrot")));
        assertTrue(shoppingListService.getItems().stream()
                .anyMatch(i -> i.getProductName().equals("Butter")));
    }

    // ---- getOpenItems ----

    @Test
    void getOpenItems_excludesPurchasedItems() {
        shoppingListService.generateList(manager);
        shoppingListService.markAsPurchased("Carrot");
        assertTrue(shoppingListService.getOpenItems().isEmpty());
    }

    @Test
    void getOpenItems_includesUnpurchasedItems() {
        shoppingListService.generateList(manager);
        assertEquals(1, shoppingListService.getOpenItems().size());
    }
}
