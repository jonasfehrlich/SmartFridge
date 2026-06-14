package de.hwrberlin.kuehlschrank.rezept;
import de.hwrberlin.kuehlschrank.model.Rezept;
import java.util.*;

/**
 * Lokale Rezeptdatenbank ohne Internet.
 * Vorlesung 2.1.4: implements RezeptAnbieter (Interface-Implementierung).
 * Vorlesung 2.1.3: Polymorphie – austauschbar gegen OnlineRezeptAnbieter.
 */
public class LokalerRezeptAnbieter implements RezeptAnbieter {
    private final List<Rezept> datenbank;

    public LokalerRezeptAnbieter() {
        datenbank = new ArrayList<>();
        datenbank.add(new Rezept("Chaospfanne",
            "Alles zusammen anbraten, was noch im Kuehlschrank ist. Mit Salz und Pfeffer wuerzen.",
            Arrays.asList("Eier", "Kaese", "Paprika", "Kartoffeln"), "20 Min.", "Lokal"));
        datenbank.add(new Rezept("Omelette",
            "Eier verquirlen, in Butter anbraten, Kaese und Gemuese einfalten.",
            Arrays.asList("Eier", "Butter", "Kaese", "Paprika"), "15 Min.", "Lokal"));
        datenbank.add(new Rezept("Joghurt-Smoothie",
            "Joghurt mit Fruechten und Milch cremig mixen.",
            Arrays.asList("Joghurt", "Milch", "Banane", "Erdbeeren"), "5 Min.", "Lokal"));
        datenbank.add(new Rezept("Gemuesesuppe",
            "Gemuese wuerfeln, in Bruehe weichkochen, wuerzen.",
            Arrays.asList("Karotten", "Sellerie", "Lauch", "Kartoffeln"), "30 Min.", "Lokal"));
        datenbank.add(new Rezept("Kaese-Toast",
            "Brot mit Kaese belegen, im Ofen gratinieren.",
            Arrays.asList("Brot", "Kaese", "Butter"), "10 Min.", "Lokal"));
        datenbank.add(new Rezept("Nudeln mit Sahnesauce",
            "Nudeln kochen, Sahne mit Kaese und Knoblauch erwaermen, vermengen.",
            Arrays.asList("Nudeln", "Sahne", "Kaese", "Knoblauch"), "20 Min.", "Lokal"));
        datenbank.add(new Rezept("Ruehrei-Toast",
            "Eier mit Milch verquirlen, in Butter scrambled, auf Toast servieren.",
            Arrays.asList("Eier", "Milch", "Butter", "Brot"), "10 Min.", "Lokal"));
    }

    @Override
    public List<Rezept> rezepteSuchen(List<String> verfuegbareZutaten) {
        List<Rezept> passende = new ArrayList<>();
        List<String> klein = new ArrayList<>();
        for (String z : verfuegbareZutaten) klein.add(z.toLowerCase());
        for (Rezept r : datenbank) {
            int treffer = 0;
            for (String z : r.getZutaten()) if (klein.contains(z.toLowerCase())) treffer++;
            if (treffer >= 1) passende.add(r);
        }
        return passende;
    }

    @Override
    public String getAnbieterName() { return "Lokale Rezeptdatenbank"; }
}
