package de.hwrberlin.kuehlschrank.model;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Specialisation of Product that also stores a shelf location.
 * Lecture 2.1.3: Inheritance, super(), @Override.
 */
public class FridgeProduct extends Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private String shelfLocation;

    public FridgeProduct(String name, ProductCategory category, double amount,
                         String unit, LocalDate expiryDate,
                         double minimumAmount, String shelfLocation) {
        super(name, category, amount, unit, expiryDate, minimumAmount);
        this.shelfLocation = shelfLocation;
    }

    public String getShelfLocation()              { return shelfLocation; }
    public void   setShelfLocation(String loc)    { this.shelfLocation = loc; }

    @Override
    public String toString() { return super.toString() + " [" + shelfLocation + "]"; }
}
