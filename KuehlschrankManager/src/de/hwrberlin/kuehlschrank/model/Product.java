package de.hwrberlin.kuehlschrank.model;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Base class for fridge products.
 *
 * Covered lecture topics:
 *  - 2.1.2 Classes/Objects/Methods: Encapsulation (all attributes private + getters/setters),
 *          constructor for initialisation, static field (createdCount).
 *  - 2.1.4 Interfaces: implements TWO interfaces (Serializable, Comparable)
 *          -> Java's way of simulating multiple inheritance.
 *  - 2.1.8 Generics: Comparable<Product> (typed, no cast needed in compareTo).
 */
public class Product implements Serializable, Comparable<Product> {
    private static final long serialVersionUID = 1L;

    // Static field (lecture 2.1.2 / exercise 2-3): counts how many
    // Product objects have been created via the constructor. Belongs to the CLASS,
    // not to an instance, and is shared across all objects.
    private static int createdCount = 0;

    // Private attributes -> data encapsulation (access only via methods).
    private String name;
    private ProductCategory category;
    private double quantity;
    private String unit;
    private LocalDate expiryDate;
    private double minimumQuantity;

    /** Constructor: initialises all attributes and increments the object counter. */
    public Product(String name, ProductCategory category, double quantity,
                   String unit, LocalDate expiryDate, double minimumQuantity) {
        this.name = name; this.category = category; this.quantity = quantity;
        this.unit = unit; this.expiryDate = expiryDate; this.minimumQuantity = minimumQuantity;
        createdCount++;
    }

    // Accessor methods (getters/setters) -- lecture 2.1.2.
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
     * Static accessor for the private static field
     * (lecture 2.1.2: read via static method, no instance needed).
     * Note for review: when loading from JSON, objects are reconstructed without
     * the constructor -> the counter only tracks objects created with "new".
     */
    public static int getCreatedCount() { return createdCount; }

    /** Does the product expire within the next 'days' days? */
    public boolean expiresSoon(int days) {
        if (expiryDate == null) return false;
        return !isExpired() && expiryDate.isBefore(LocalDate.now().plusDays(days + 1));
    }

    /** Has the expiry date already passed? */
    public boolean isExpired() {
        if (expiryDate == null) return false;
        return expiryDate.isBefore(LocalDate.now());
    }

    /** Is the quantity below the minimum quantity? */
    public boolean needsRestock() { return quantity < minimumQuantity; }

    /**
     * Sort by expiry date for Collections.sort()
     * (lecture 2.1.4: contract of the Comparable interface; 2.1.7: sorting Collections).
     */
    @Override
    public int compareTo(Product other) {
        if (this.expiryDate == null && other.expiryDate == null) return 0;
        if (this.expiryDate == null) return 1;   // Products without expiry date go to the end
        if (other.expiryDate == null) return -1;
        return this.expiryDate.compareTo(other.expiryDate);
    }

    /** toString() overrides Object.toString() (lecture 2.1.3: class Object). */
    @Override
    public String toString() {
        return name + " (" + quantity + " " + unit + ", Expiry: " + expiryDate + ")";
    }
}
