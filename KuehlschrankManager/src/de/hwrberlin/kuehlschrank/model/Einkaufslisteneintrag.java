package de.hwrberlin.kuehlschrank.model;
import java.io.Serializable;

/** Eintrag auf der Einkaufsliste. Vorlesung: Klassen, Serializable. */
public class Einkaufslisteneintrag implements Serializable {
    private static final long serialVersionUID = 1L;
    private String produktname;
    private double benoetigteMenge;
    private String einheit;
    private Produktkategorie kategorie;
    private boolean gekauft;

    public Einkaufslisteneintrag(String produktname, double benoetigteMenge,
                                  String einheit, Produktkategorie kategorie) {
        this.produktname = produktname; this.benoetigteMenge = benoetigteMenge;
        this.einheit = einheit; this.kategorie = kategorie; this.gekauft = false;
    }
    public String getProduktname()                  { return produktname; }
    public void setProduktname(String n)            { this.produktname = n; }
    public double getBenoetigteMenge()              { return benoetigteMenge; }
    public void setBenoetigteMenge(double m)        { this.benoetigteMenge = m; }
    public String getEinheit()                      { return einheit; }
    public void setEinheit(String e)                { this.einheit = e; }
    public Produktkategorie getKategorie()          { return kategorie; }
    public void setKategorie(Produktkategorie k)    { this.kategorie = k; }
    public boolean isGekauft()                      { return gekauft; }
    public void setGekauft(boolean g)               { this.gekauft = g; }

    @Override
    public String toString() {
        return (gekauft ? "[x] " : "[ ] ") + produktname + "  " + benoetigteMenge + " " + einheit;
    }
}
