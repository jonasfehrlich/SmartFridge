package de.hwrberlin.kuehlschrank;

import de.hwrberlin.kuehlschrank.model.Product;
import de.hwrberlin.kuehlschrank.model.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Product class.
 * Covers: constructor, getters/setters, isExpired(), expiresSoon(),
 *         needsRestock(), compareTo(), getCreatedCount(), toString().
 */
class ProductTest {

    private Product milk;
    private Product cheese;

    @BeforeEach
    void setUp() {
        // milk:   2.0 L,  expires in 3 days,  min 1.0  -> sufficient stock, expiring soon
        // cheese: 0.5 kg, expires in 10 days, min 1.0  -> needs restock, not expiring soon
        milk   = new Product("Milk",   ProductCategory.DAIRY, 2.0, "L",  LocalDate.now().plusDays(3),  1.0);
        cheese = new Product("Cheese", ProductCategory.DAIRY, 0.5, "kg", LocalDate.now().plusDays(10), 1.0);
    }

    // ---- Constructor & Getters ----

    @Test
    void constructor_setsAllFieldsCorrectly() {
        assertEquals("Milk", milk.getName());
        assertEquals(ProductCategory.DAIRY, milk.getCategory());
        assertEquals(2.0, milk.getQuantity(), 0.001);
        assertEquals("L", milk.getUnit());
        assertEquals(1.0, milk.getMinimumQuantity(), 0.001);
    }

    @Test
    void staticCounter_incrementsOnEachNewProduct() {
        int before = Product.getCreatedCount();
        new Product("Butter", ProductCategory.DAIRY, 0.25, "kg", LocalDate.now().plusDays(7), 0.1);
        assertEquals(before + 1, Product.getCreatedCount());
    }

    // ---- Setters ----

    @Test
    void setName_updatesName() {
        milk.setName("Whole Milk");
        assertEquals("Whole Milk", milk.getName());
    }

    @Test
    void setQuantity_updatesQuantity() {
        milk.setQuantity(3.5);
        assertEquals(3.5, milk.getQuantity(), 0.001);
    }

    @Test
    void setExpiryDate_updatesDate() {
        LocalDate newDate = LocalDate.now().plusDays(20);
        milk.setExpiryDate(newDate);
        assertEquals(newDate, milk.getExpiryDate());
    }

    // ---- isExpired() ----

    @Test
    void isExpired_returnsFalse_whenExpiryIsInFuture() {
        assertFalse(milk.isExpired());
    }

    @Test
    void isExpired_returnsTrue_whenExpiryIsInPast() {
        milk.setExpiryDate(LocalDate.now().minusDays(1));
        assertTrue(milk.isExpired());
    }

    @Test
    void isExpired_returnsFalse_whenExpiryIsToday() {
        milk.setExpiryDate(LocalDate.now());
        assertFalse(milk.isExpired()); // today is NOT before today
    }

    // ---- expiresSoon(int days) ----

    @Test
    void expiresSoon_returnsTrue_whenWithinThreshold() {
        assertTrue(milk.expiresSoon(5)); // expires in 3 days, threshold 5
    }

    @Test
    void expiresSoon_returnsFalse_whenBeyondThreshold() {
        assertFalse(cheese.expiresSoon(5)); // expires in 10 days, threshold 5
    }

    @Test
    void expiresSoon_returnsFalse_whenAlreadyExpired() {
        milk.setExpiryDate(LocalDate.now().minusDays(2));
        assertFalse(milk.expiresSoon(5));
    }

    // ---- needsRestock() ----

    @Test
    void needsRestock_returnsFalse_whenQuantitySufficient() {
        assertFalse(milk.needsRestock()); // 2.0 >= 1.0
    }

    @Test
    void needsRestock_returnsTrue_whenQuantityTooLow() {
        assertTrue(cheese.needsRestock()); // 0.5 < 1.0
    }

    // ---- compareTo() ----

    @Test
    void compareTo_milkBeforeCheese_becauseMilkExpiresSooner() {
        assertTrue(milk.compareTo(cheese) < 0);
    }

    @Test
    void compareTo_sameExpiryDate_returnsZero() {
        LocalDate same = LocalDate.now().plusDays(5);
        milk.setExpiryDate(same);
        cheese.setExpiryDate(same);
        assertEquals(0, milk.compareTo(cheese));
    }

    @Test
    void compareTo_nullExpiryGoesToEnd() {
        milk.setExpiryDate(null);
        assertTrue(milk.compareTo(cheese) > 0);
    }

    // ---- toString() ----

    @Test
    void toString_containsName() {
        assertTrue(milk.toString().contains("Milk"));
    }
}
