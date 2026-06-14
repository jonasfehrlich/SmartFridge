package de.hwrberlin.kuehlschrank.service;
import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.rezept.*;
import java.util.*;

/**
 * Service fuer Rezeptvorschlaege (Chaos-Modus).
 * Vorlesung 2.1.3: Polymorphie durch RezeptAnbieter-Interface.
 * Vorlesung 2.1.4: Late Binding – Anbieter wird erst zur Laufzeit entschieden.
 */
public class RezeptService {
    private RezeptAnbieter anbieter;

    public RezeptService(boolean onlineModus) {
        this.anbieter = onlineModus ? new OnlineRezeptAnbieter() : new LokalerRezeptAnbieter();
    }

    /** Fuer Tests: Interface-Implementierung direkt injizieren. */
    public RezeptService(RezeptAnbieter anbieter) { this.anbieter = anbieter; }

    /** Bevorzugt bald ablaufende Produkte als Zutaten (Chaos-Modus). */
    public List<Rezept> rezepteVorschlagen(KuehlschrankVerwaltung v) {
        List<String> zutaten = new ArrayList<>();
        for (Produkt p : v.baldAblaufendeProdukte(5)) zutaten.add(p.getName());
        for (Produkt p : v.alleProdukte()) if (!zutaten.contains(p.getName())) zutaten.add(p.getName());
        return anbieter.rezepteSuchen(zutaten);
    }

    /** Welche Zutaten eines Rezepts fehlen im Kuehlschrank? */
    public List<String> fehlendeZutaten(Rezept rezept, KuehlschrankVerwaltung v) {
        List<String> fehlend = new ArrayList<>();
        for (String z : rezept.getZutaten()) if (v.produktSuchen(z) == null) fehlend.add(z);
        return fehlend;
    }

    public String getAnbieterName() { return anbieter.getAnbieterName(); }
}
