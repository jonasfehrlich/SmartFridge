package de.hwrberlin.kuehlschrank;

import de.hwrberlin.kuehlschrank.model.Product;
import de.hwrberlin.kuehlschrank.model.ProductCategory;
import de.hwrberlin.kuehlschrank.service.FridgeManager;
import de.hwrberlin.kuehlschrank.util.FridgeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FridgeManager.
 * Covers: addProduct, removeProduct, findProduct (case-insensitive), getAllProducts,
 *         getProductsByCategory, getExpiringSoon, getExpiredProducts,
 *         getProductsNeedingRestock, getProductCount, FridgeException on null.
 */
class FridgeManagerTest {

    private FridgeManager manager;
    private Product milk;
    private Product carrot;
    private Product oldYoghurt;

    @BeforeEach
    void setUp() {
        manager    = new FridgeManager();
        milk       = new Product("Milk",    ProductCategory.DAIRY,           2.0, "L",  LocalDate.now().plusDays(4),   1.0);
        carrot     = new Product("Carrot",  ProductCategory.FRUITS_VEGETABLES, 0.3, "kg", LocalDate.now().plusDays(14), 0.5);
        oldYoghurt = new Product("Yoghurt", ProductCategory.DAIRY,           0.2, "kg", LocalDate.now().minusDays(2), 0.5);
        manager.addProduct(milk);
        manager.addProduct(carrot);
    }

    // ---- addProduct / findProduct ----

    @Test
    void addProduct_canBeFoundByName() {
        assertNotNull(manager.findProduct("Milk"));
    }

    @Test
    void findProduct_isCaseInsensitive() {
        assertNotNull(manager.findProduct("milk"));
        assertNotNull(manager.findProduct("MILK"));
    }

    @Test
    void addProduct_null_throwsFridgeException() {
        assertThrows(FridgeException.class, () -> manager.addProduct(null));
    }

    @Test
    void findProduct_nonExistent_returnsNull() {
        assertNull(manager.findProduct("Butter"));
    }

    // ---- removeProduct ----

    @Test
    void removeProduct_existingProduct_returnsTrue() {
        assertTrue(manager.removeProduct("Milk"));
        assertNull(manager.findProduct("Milk"));
    }

    @Test
    void removeProduct_nonExistentProduct_returnsFalse() {
        assertFalse(manager.removeProduct("Butter"));
    }

    // ---- getAllProducts / getProductCount ----

    @Test
    void getAllProducts_returnsAllAddedProducts() {
        assertEquals(2, manager.getAllProducts().size());
    }

    @Test
    void getProductCount_matchesAddedProducts() {
        assertEquals(2, manager.getProductCount());
    }

    // ---- getProductsByCategory ----

    @Test
    void getProductsByCategory_returnsOnlyMatchingCategory() {
        ArrayList<Product> dairy = manager.getProductsByCategory(ProductCategory.DAIRY);
        assertEquals(1, dairy.size());
        assertEquals("Milk", dairy.get(0).getName());
    }

    @Test
    void getProductsByCategory_emptyWhenNoneMatch() {
        assertTrue(manager.getProductsByCategory(ProductCategory.MEDICATION).isEmpty());
    }

    // ---- getExpiringSoon ----

    @Test
    void getExpiringSoon_includesMilk_within5Days() {
        assertTrue(manager.getExpiringSoon(5).stream()
                .anyMatch(p -> p.getName().equals("Milk")));
    }

    @Test
    void getExpiringSoon_excludesCarrot_beyond5Days() {
        assertTrue(manager.getExpiringSoon(5).stream()
                .noneMatch(p -> p.getName().equals("Carrot")));
    }

    @Test
    void getExpiringSoon_returnsEmptyList_whenNothingExpiresSoon() {
        FridgeManager fresh = new FridgeManager();
        fresh.addProduct(new Product("Apple", ProductCategory.FRUITS_VEGETABLES,
                1.0, "kg", LocalDate.now().plusDays(30), 0.5));
        assertTrue(fresh.getExpiringSoon(5).isEmpty());
    }

    // ---- getExpiredProducts ----

    @Test
    void getExpiredProducts_returnsExpiredItems() {
        manager.addProduct(oldYoghurt);
        assertTrue(manager.getExpiredProducts().stream()
                .anyMatch(p -> p.getName().equals("Yoghurt")));
    }

    @Test
    void getExpiredProducts_excludesFreshItems() {
        assertTrue(manager.getExpiredProducts().stream()
                .noneMatch(p -> p.getName().equals("Milk")));
    }

    // ---- getProductsNeedingRestock ----

    @Test
    void getProductsNeedingRestock_includesCarrot_belowMinimum() {
        // carrot: 0.3 < 0.5 minimum -> needsRestock() == true
        assertTrue(manager.getProductsNeedingRestock().stream()
                .anyMatch(p -> p.getName().equals("Carrot")));
    }

    @Test
    void getProductsNeedingRestock_excludesMilk_aboveMinimum() {
        // milk: 2.0 >= 1.0 minimum -> needsRestock() == false
        assertTrue(manager.getProductsNeedingRestock().stream()
                .noneMatch(p -> p.getName().equals("Milk")));
    }
}
