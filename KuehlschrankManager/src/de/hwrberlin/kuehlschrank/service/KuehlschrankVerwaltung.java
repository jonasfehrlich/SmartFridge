package de.hwrberlin.kuehlschrank.service;
import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.util.Datenspeicher;
import de.hwrberlin.kuehlschrank.util.KuehlschrankAusnahme;
import java.io.Serializable;
import java.util.*;

/**
 * Zentrale Verwaltung des Kuehlschranks.
 * Vorlesung 2.1.7: HashMap, ArrayList, HashSet, foreach-Schleifen.
 * Vorlesung 2.1.8: Typisierte Collections mit Diamant-Operator.
 */
public class KuehlschrankVerwaltung implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String DATEI = "data/kuehlschrank.json";

    private HashMap<String, Produkt> produkte = new HashMap<>();
    private HashSet<Produktkategorie> vorhandeneKategorien = new HashSet<>();

    public void produktHinzufuegen(Produkt produkt) {
        if (produkt == null || produkt.getName() == null)
            throw new KuehlschrankAusnahme("Produkt darf nicht null sein.");
        produkte.put(produkt.getName().toLowerCase(), produkt);
        vorhandeneKategorien.add(produkt.getKategorie());
    }

    public boolean produktEntfernen(String name) {
        return produkte.remove(name.toLowerCase()) != null;
    }

    public Produkt produktSuchen(String name) {
        return produkte.get(name.toLowerCase());
    }

    public ArrayList<Produkt> alleProdukte() {
        return new ArrayList<>(produkte.values());
    }

    public ArrayList<Produkt> produkteNachKategorie(Produktkategorie kategorie) {
        ArrayList<Produkt> ergebnis = new ArrayList<>();
        for (Produkt p : produkte.values())
            if (p.getKategorie() == kategorie) ergebnis.add(p);
        return ergebnis;
    }

    public ArrayList<Produkt> baldAblaufendeProdukte(int tage) {
        ArrayList<Produkt> liste = new ArrayList<>();
        for (Produkt p : produkte.values())
            if (p.laeufBaldAb(tage)) liste.add(p);
        Collections.sort(liste);
        return liste;
    }

    public ArrayList<Produkt> abgelaufeneProdukte() {
        ArrayList<Produkt> liste = new ArrayList<>();
        for (Produkt p : produkte.values())
            if (p.istAbgelaufen()) liste.add(p);
        return liste;
    }

    public ArrayList<Produkt> produkteMitNachkaufbedarf() {
        ArrayList<Produkt> liste = new ArrayList<>();
        for (Produkt p : produkte.values())
            if (p.brauchtnachkauf()) liste.add(p);
        return liste;
    }

    public HashSet<Produktkategorie> getVorhandeneKategorien() {
        return new HashSet<>(vorhandeneKategorien);
    }

    public int anzahlProdukte() { return produkte.size(); }

    public void speichern() { Datenspeicher.speichern(this, DATEI); }

    public static KuehlschrankVerwaltung laden() {
        KuehlschrankVerwaltung v = Datenspeicher.laden(DATEI,KuehlschrankVerwaltung.class);
        return v != null ? v : new KuehlschrankVerwaltung();
    }
}
