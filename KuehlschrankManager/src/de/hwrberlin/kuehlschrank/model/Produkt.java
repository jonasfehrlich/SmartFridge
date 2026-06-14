package de.hwrberlin.kuehlschrank.model;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Basisklasse fuer Kuehlschrankprodukte.
 * Vorlesung: Klassen/Objekte, Kapselung, Serializable, Comparable (Collections).
 */
public class Produkt implements Serializable, Comparable<Produkt> {
    private static final long serialVersionUID = 1L;
    private String name;
    private Produktkategorie kategorie;
    private double menge;
    private String einheit;
    private LocalDate ablaufdatum;
    private double mindestmenge;

    public Produkt(String name, Produktkategorie kategorie, double menge,
                   String einheit, LocalDate ablaufdatum, double mindestmenge) {
        this.name = name; this.kategorie = kategorie; this.menge = menge;
        this.einheit = einheit; this.ablaufdatum = ablaufdatum; this.mindestmenge = mindestmenge;
    }

    public String getName()                         { return name; }
    public void setName(String n)                   { this.name = n; }
    public Produktkategorie getKategorie()          { return kategorie; }
    public void setKategorie(Produktkategorie k)    { this.kategorie = k; }
    public double getMenge()                        { return menge; }
    public void setMenge(double m)                  { this.menge = m; }
    public String getEinheit()                      { return einheit; }
    public void setEinheit(String e)                { this.einheit = e; }
    public LocalDate getAblaufdatum()               { return ablaufdatum; }
    public void setAblaufdatum(LocalDate d)         { this.ablaufdatum = d; }
    public double getMindestmenge()                 { return mindestmenge; }
    public void setMindestmenge(double m)           { this.mindestmenge = m; }

    /** Laeuft das Produkt innerhalb der naechsten 'tage' Tage ab? */
    public boolean laeufBaldAb(int tage) {
        if (ablaufdatum == null) return false;
        return !istAbgelaufen() && ablaufdatum.isBefore(LocalDate.now().plusDays(tage + 1));
    }
    /** Ist das Ablaufdatum bereits ueberschritten? */
    public boolean istAbgelaufen() {
        if (ablaufdatum == null) return false;
        return ablaufdatum.isBefore(LocalDate.now());
    }
    /** Ist die Menge unter der Mindestmenge? */
    public boolean brauchtnachkauf() { return menge < mindestmenge; }

    /** Sortierung nach Ablaufdatum fuer Collections.sort(). */
    @Override
    public int compareTo(Produkt a) {
        if (this.ablaufdatum == null && a.ablaufdatum == null) return 0;
        if (this.ablaufdatum == null) return 1;
        if (a.ablaufdatum == null) return -1;
        return this.ablaufdatum.compareTo(a.ablaufdatum);
    }
    @Override
    public String toString() {
        return name + " (" + menge + " " + einheit + ", MHD: " + ablaufdatum + ")";
    }
}
