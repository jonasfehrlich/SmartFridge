package de.hwrberlin.kuehlschrank.model;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Spezialisierung von Produkt mit Lagerort.
 * Vorlesung 2.1.3: Vererbung, super(), @Override.
 */
public class KuehlschrankProdukt extends Produkt implements Serializable {
    private static final long serialVersionUID = 1L;
    private String lagerort;

    public KuehlschrankProdukt(String name, Produktkategorie kategorie, double menge,
                                String einheit, LocalDate ablaufdatum,
                                double mindestmenge, String lagerort) {
        super(name, kategorie, menge, einheit, ablaufdatum, mindestmenge);
        this.lagerort = lagerort;
    }
    public String getLagerort()         { return lagerort; }
    public void setLagerort(String l)   { this.lagerort = l; }

    @Override
    public String toString() { return super.toString() + " [" + lagerort + "]"; }
}
