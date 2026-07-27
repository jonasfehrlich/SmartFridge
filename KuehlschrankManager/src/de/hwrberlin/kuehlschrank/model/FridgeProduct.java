package de.hwrberlin.kuehlschrank.model;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Specialisation of Product with a storage location.
 * Lecture 2.1.3: Inheritance, super(), @Override.
 */
public class FridgeProduct extends Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private String storageLocation;

    public FridgeProduct(String name, ProductCategory category, double quantity,
                         String unit, LocalDate expiryDate,
                         double minimumQuantity, String storageLocation) {
        super(name, category, quantity, unit, expiryDate, minimumQuantity);
        this.storageLocation = storageLocation;
    }

    public String getStorageLocation()       { return storageLocation; }
    public void setStorageLocation(String l) { this.storageLocation = l; }

    @Override
    public String toString() { return super.toString() + " [" + storageLocation + "]"; }
}
