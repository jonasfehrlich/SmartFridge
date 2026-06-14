package de.hwrberlin.kuehlschrank.service;
import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.util.Datenspeicher;
import java.io.Serializable;
import java.util.*;

/**
 * Verwaltet die Einkaufsliste. Generiert sie automatisch aus Nachkaufbedarf.
 * Vorlesung 2.1.7: ArrayList, removeIf.
 */
public class EinkaufslistenService implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATEI = "data/einkaufsliste.dat";
    private ArrayList<Einkaufslisteneintrag> eintraege = new ArrayList<>();

    public void listeGenerieren(KuehlschrankVerwaltung v) {
        eintraege.clear();
        for (Produkt p : v.produkteMitNachkaufbedarf()) {
            if (!istBereitsAufListe(p.getName())) {
                double fehlend = Math.ceil(p.getMindestmenge() - p.getMenge());
                eintraege.add(new Einkaufslisteneintrag(p.getName(), fehlend, p.getEinheit(), p.getKategorie()));
            }
        }
    }

    public void eintragHinzufuegen(Einkaufslisteneintrag eintrag) {
        if (!istBereitsAufListe(eintrag.getProduktname())) eintraege.add(eintrag);
    }

    public boolean alsGekauftMarkieren(String produktname) {
        for (Einkaufslisteneintrag e : eintraege) {
            if (e.getProduktname().equalsIgnoreCase(produktname)) { e.setGekauft(true); return true; }
        }
        return false;
    }

    public void gekaufteEntfernen() { eintraege.removeIf(Einkaufslisteneintrag::isGekauft); }

    public ArrayList<Einkaufslisteneintrag> getEintraege() { return new ArrayList<>(eintraege); }

    public List<Einkaufslisteneintrag> offeneEintraege() {
        List<Einkaufslisteneintrag> offen = new ArrayList<>();
        for (Einkaufslisteneintrag e : eintraege) if (!e.isGekauft()) offen.add(e);
        return offen;
    }

    private boolean istBereitsAufListe(String name) {
        for (Einkaufslisteneintrag e : eintraege)
            if (e.getProduktname().equalsIgnoreCase(name)) return true;
        return false;
    }

    public void speichern() { Datenspeicher.speichern(this, DATEI); }

    public static EinkaufslistenService laden() {
        EinkaufslistenService s = Datenspeicher.laden(DATEI);
        return s != null ? s : new EinkaufslistenService();
    }
}
