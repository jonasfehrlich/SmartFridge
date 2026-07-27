package de.hwrberlin.kuehlschrank.model;
import java.io.Serializable;

/** An entry on the shopping list. Lecture: Classes, Serializable. */
public class ShoppingItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String productName;
    private double requiredAmount;
    private String unit;
    private ProductCategory category;
    private boolean purchased;

    public ShoppingItem(String productName, double requiredAmount,
                        String unit, ProductCategory category) {
        this.productName     = productName;
        this.requiredAmount  = requiredAmount;
        this.unit            = unit;
        this.category        = category;
        this.purchased       = false;
    }

    public String getProductName()                   { return productName; }
    public void   setProductName(String n)           { this.productName = n; }
    public double getRequiredAmount()                { return requiredAmount; }
    public void   setRequiredAmount(double m)        { this.requiredAmount = m; }
    public String getUnit()                          { return unit; }
    public void   setUnit(String e)                  { this.unit = e; }
    public ProductCategory getCategory()             { return category; }
    public void   setCategory(ProductCategory k)     { this.category = k; }
    public boolean isPurchased()                     { return purchased; }
    public void   setPurchased(boolean g)            { this.purchased = g; }

    @Override
    public String toString() {
        return (purchased ? "[x] " : "[ ] ") + productName + "  " + requiredAmount + " " + unit;
    }
}
