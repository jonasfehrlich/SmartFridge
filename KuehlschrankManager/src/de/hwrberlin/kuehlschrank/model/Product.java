package de.hwrberlin.kuehlschrank.model;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Base class for fridge products.
 *
 * Covered lecture topics:
 *  - 2.1.2 Classes/Objects/Methods: encapsulation (all fields private + getters/setters),
 *          constructor for initialisation, static field (createdCount).
 *  - 2.1.4 Interfaces: implements TWO interfaces (Serializable, Comparable)
 *          -> Java's way of emulating multiple inheritance.
 *  - 2.1.8 Generics: Comparable<Product> (typed, no cast needed in compareTo).
 */
public class Product implements Serializable, Comparable<Product> {
    private static final long serialVersionUID = 1L;

    /**
     * Static field (Lecture 2.1.2 / Exercise 2-3): counts how many Product
     * objects have been created via the constructor. Belongs to the CLASS,
     * not to any instance, and is shared across all objects.
     */
    private static int createdCount = 0;

    // Private fields -> data encapsulation (access only via methods).
    private String         name;
    private ProductCategory category;
    private double         amount;
    private String         unit;
    private LocalDate      expiryDate;
    private double         minimumAmount;

    /** Constructor: initialises all fields and increments the object counter. */
    public Product(String name, ProductCategory category, double amount,
                   String unit, LocalDate expiryDate, double minimumAmount) {
        this.name          = name;
        this.category      = category;
        this.amount        = amount;
        this.unit          = unit;
        this.expiryDate    = expiryDate;
        this.minimumAmount = minimumAmount;
        createdCount++;
    }

    // Accessor methods (getters/setters) -- Lecture 2.1.2.
    public String          getName()                          { return name; }
    public void            setName(String n)                  { this.name = n; }
    public ProductCategory getCategory()                      { return category; }
    public void            setCategory(ProductCategory k)     { this.category = k; }
    public double          getAmount()                        { return amount; }
    public void            setAmount(double m)                { this.amount = m; }
    public String          getUnit()                          { return unit; }
    public void            setUnit(String e)                  { this.unit = e; }
    public LocalDate       getExpiryDate()                    { return expiryDate; }
    public void            setExpiryDate(LocalDate d)         { this.expiryDate = d; }
    public double          getMinimumAmount()                 { return minimumAmount; }
    public void            setMinimumAmount(double m)         { this.minimumAmount = m; }

    /**
     * Static accessor for the private static field
     * (Lecture 2.1.2: read via static method, without an instance).
     * Note for review: objects deserialised from JSON bypass the constructor
     * -> the counter only captures objects created with 'new'.
     */
    public static int getCreatedCount() { return createdCount; }

    /** Returns true if the product expires within the given number of days. */
    public boolean expiresSoon(int days) {
        return expiryDate != null && !expiryDate.isBefore(LocalDate.now())
                && !expiryDate.isAfter(LocalDate.now().plusDays(days));
    }

    /** Returns true if the product has already expired. */
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    /** Returns true if the current amount falls below the minimum amount. */
    public boolean needsRestocking() {
        return amount < minimumAmount;
    }

    /**
     * Natural ordering by expiry date (Lecture 2.1.4 + 2.1.8: Comparable<Product>).
     * Null expiry dates sort to the end.
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
        return name + " (" + amount + " " + unit + ", expires: " + expiryDate + ")";
    }
}
