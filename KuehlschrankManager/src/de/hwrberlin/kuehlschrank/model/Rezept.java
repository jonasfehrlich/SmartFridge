package de.hwrberlin.kuehlschrank.model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Kochrezept. Vorlesung 2.1.7: ArrayList als typisierte Collection. */
public class Rezept implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String beschreibung;
    private List<String> zutaten;
    private String zubereitungszeit;
    private String quelle;

    public Rezept(String name, String beschreibung, List<String> zutaten,
                  String zubereitungszeit, String quelle) {
        this.name = name; this.beschreibung = beschreibung;
        this.zutaten = zutaten != null ? new ArrayList<>(zutaten) : new ArrayList<>();
        this.zubereitungszeit = zubereitungszeit; this.quelle = quelle;
    }
    public String getName()                     { return name; }
    public void setName(String n)               { this.name = n; }
    public String getBeschreibung()             { return beschreibung; }
    public void setBeschreibung(String b)       { this.beschreibung = b; }
    public List<String> getZutaten()            { return zutaten; }
    public void setZutaten(List<String> z)      { this.zutaten = z; }
    public String getZubereitungszeit()         { return zubereitungszeit; }
    public void setZubereitungszeit(String z)   { this.zubereitungszeit = z; }
    public String getQuelle()                   { return quelle; }
    public void setQuelle(String q)             { this.quelle = q; }

    @Override
    public String toString() { return name + " (ca. " + zubereitungszeit + " | " + quelle + ")"; }
}
