package de.hwrberlin.kuehlschrank.model;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Basisklasse fuer Kuehlschrankprodukte.
 *
 * Abgedeckte Vorlesungsthemen:
 *  - 2.1.2 Klassen/Objekte/Methoden: Kapselung (alle Attribute private + getter/setter),
 *          Konstruktor zur Initialisierung, statisches Datenelement (anzahlErzeugt).
 *  - 2.1.4 Schnittstellen: implementiert ZWEI Interfaces (Serializable, Comparable)
 *          -> in Java die Nachbildung der Mehrfachvererbung.
 *  - 2.1.8 Generics: Comparable&lt;Produkt&gt; (typisiert, kein Cast in compareTo noetig).
 */
public class Produkt implements Serializable, Comparable<Produkt> {
    private static final long serialVersionUID = 1L;

    // Statisches Datenelement (Vorlesung 2.1.2 / Uebung 2-3): zaehlt, wie viele
    // Produkt-Objekte ueber den Konstruktor erzeugt wurden. Gehoert zur KLASSE,
    // nicht zu einer Instanz, und ist fuer alle Objekte gemeinsam sichtbar.
    private static int anzahlErzeugt = 0;

    // Private Attribute -> Datenkapselung (Zugriff nur ueber Methoden).
    private String name;
    private Produktkategorie kategorie;
    private double menge;
    private String einheit;
    private LocalDate ablaufdatum;
    private double mindestmenge;

    /** Konstruktor: initialisiert alle Attribute und erhoeht den Objektzaehler. */
    public Produkt(String name, Produktkategorie kategorie, double menge,
                   String einheit, LocalDate ablaufdatum, double mindestmenge) {
        this.name = name; this.kategorie = kategorie; this.menge = menge;
        this.einheit = einheit; this.ablaufdatum = ablaufdatum; this.mindestmenge = mindestmenge;
        anzahlErzeugt++;
    }

    // Zugriffsmethoden (getter/setter) -- Vorlesung 2.1.2.
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

    /**
     * Statische Zugriffsmethode auf das private statische Datenelement
     * (Vorlesung 2.1.2: Auslesen ueber statische Elementfunktion, ohne Instanz).
     * Hinweis fuers Review: Beim Laden aus JSON werden Objekte ohne Konstruktor
     * rekonstruiert -> der Zaehler erfasst nur per "new" erzeugte Objekte.
     */
    public static int getAnzahlErzeugt() { return anzahlErzeugt; }

    /** Laeuft das Produkt innerhalb der naechsten 'tage' Tage ab? */
    public boolean laeuftBaldAb(int tage) {
        if (ablaufdatum == null) return false;
        return !istAbgelaufen() && ablaufdatum.isBefore(LocalDate.now().plusDays(tage + 1));
    }
    /** Ist das Ablaufdatum bereits ueberschritten? */
    public boolean istAbgelaufen() {
        if (ablaufdatum == null) return false;
        return ablaufdatum.isBefore(LocalDate.now());
    }
    /** Ist die Menge unter der Mindestmenge? */
    public boolean brauchtNachkauf() { return menge < mindestmenge; }

    /**
     * Sortierung nach Ablaufdatum fuer Collections.sort()
     * (Vorlesung 2.1.4: Vertrag des Comparable-Interface; 2.1.7: Sortierung von Collections).
     */
    @Override
    public int compareTo(Produkt a) {
        if (this.ablaufdatum == null && a.ablaufdatum == null) return 0;
        if (this.ablaufdatum == null) return 1;   // Produkte ohne MHD ans Ende
        if (a.ablaufdatum == null) return -1;
        return this.ablaufdatum.compareTo(a.ablaufdatum);
    }

    /** toString() ueberschreibt Object.toString() (Vorlesung 2.1.3: Klasse Object). */
    @Override
    public String toString() {
        return name + " (" + menge + " " + einheit + ", MHD: " + ablaufdatum + ")";
    }
}
