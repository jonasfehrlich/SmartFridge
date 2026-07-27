package de.hwrberlin.kuehlschrank.model;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Base class for fridge products.
 *
 * Covered lecture topics:
 *  - 2.1.2 Classes/Objects/Methods: encapsulation (private attributes + getters/setters),
 *          constructor for initialisation, static field (createdCount).
 *  - 2.1.4 Interfaces: implements TWO interfaces (Serializable, Comparable)
 *          -> Java's way of simulating multiple inheritance.
 *  - 2.1.8 Generics: Comparable<Product> (typed, no cast needed in compareTo).
 */
public class Product implements Serializable, Comparable<Product> {
    private static final long serialVersionUID = 1L;

    /**
     * Static field (lecture 2.1.2 / exercise 2-3): counts how many Product objects
     * have been created via the constructor. Belongs to the CLASS, not to an instance.
     */
    private static int createdCount = 0;

    private String name;
    private ProductCategory category;
    private double quantity;
    private String unit;
    private LocalDate expiryDate;
    private double minimumQuantity;

    /** Constructor: initialises all attributes and increments the object counter. */
    public Product(String name, ProductCategory category, double quantity,
                   String unit, LocalDate expiryDate, double minimumQuantity) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.minimumQuantity = minimumQuantity;
        createdCount++;
    }

    public String getName()                          { return name; }
    public void setName(String n)                    { this.name = n; }
    public ProductCategory getCategory()             { return category; }
    public void setCategory(ProductCategory c)       { this.category = c; }
    public double getQuantity()                      { return quantity; }
    public void setQuantity(double q)                { this.quantity = q; }
    public String getUnit()                          { return unit; }
    public void setUnit(String u)                    { this.unit = u; }
    public LocalDate getExpiryDate()                 { return expiryDate; }
    public void setExpiryDate(LocalDate d)           { this.expiryDate = d; }
    public double getMinimumQuantity()               { return minimumQuantity; }
    public void setMinimumQuantity(double m)         { this.minimumQuantity = m; }

    /**
     * Static accessor for the private static field.
     * Note: when loading from JSON, objects are reconstructed without the constructor
     * -> the counter only tracks objects created with "new".
     */
    public static int getCreatedCount() { return createdCount; }

    /** Returns true if the product expires within the next {@code days} days. */
    public boolean expiresSoon(int days) {
        if (expiryDate == null) return false;
        return !isExpired() && expiryDate.isBefore(LocalDate.now().plusDays(days + 1));
    }

    /** Returns true if the expiry date has already passed. */
    public boolean isExpired() {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDate.now());
    }

    /** Returns true if the current quantity is below the minimum quantity. */
    public boolean needsRestock() { return quantity < minimumQuantity; }

    /**
     * Sort by expiry date for Collections.sort().
     * Products without an expiry date are sorted to the end.
     */
    @Override
    public int compareTo(Product other) {
        if (this.expiryDate == null && other.expiryDate == null) return 0;
        if (this.expiryDate == null) return 1;
        if (other.expiryDate == null) return -1;
        return this.expiryDate.compareTo(other.expiryDate);
    }

    @Override
    public String toString() {
        return name + " (" + quantity + " " + unit + ", Expiry: " + expiryDate + ")";
    }
}
