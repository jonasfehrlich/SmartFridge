package de.hwrberlin.kuehlschrank.model;
import java.io.Serializable;

/** Entry on the shopping list. Lecture: classes, Serializable. */
public class ShoppingItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String productName;
    private double requiredQuantity;
    private String unit;
    private ProductCategory category;
    private boolean purchased;

    public ShoppingItem(String productName, double requiredQuantity,
                        String unit, ProductCategory category) {
        this.productName = productName;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
        this.category = category;
        this.purchased = false;
    }

    public String getProductName()             { return productName; }
    public void setProductName(String n)       { this.productName = n; }
    public double getRequiredQuantity()        { return requiredQuantity; }
    public void setRequiredQuantity(double q)  { this.requiredQuantity = q; }
    public String getUnit()                    { return unit; }
    public void setUnit(String u)              { this.unit = u; }
    public ProductCategory getCategory()       { return category; }
    public void setCategory(ProductCategory c) { this.category = c; }
    public boolean isPurchased()               { return purchased; }
    public void setPurchased(boolean p)        { this.purchased = p; }

    @Override
    public String toString() {
        return (purchased ? "[x] " : "[ ] ") + productName + "  " + requiredQuantity + " " + unit;
    }
}
