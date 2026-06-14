package de.hwrberlin.kuehlschrank.rezept;
import de.hwrberlin.kuehlschrank.model.Rezept;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Holt Rezepte von TheMealDB (kostenlose API, kein Key noetig).
 * Vorlesung 2.1.4: implements RezeptAnbieter (Polymorphie durch Interface).
 * Nutzt nur Standard-JDK: HttpURLConnection, kein externes Framework.
 * Endpunkt: https://www.themealdb.com/api/json/v1/1/filter.php?i={Zutat}
 */
public class OnlineRezeptAnbieter implements RezeptAnbieter {
    private static final String API = "https://www.themealdb.com/api/json/v1/1/filter.php?i=";

    @Override
    public List<Rezept> rezepteSuchen(List<String> zutaten) {
        List<Rezept> ergebnis = new ArrayList<>();
        if (zutaten == null || zutaten.isEmpty()) return ergebnis;
        try {
            String json = httpGet(API + zutaten.get(0).replace(" ", "%20"));
            ergebnis = jsonParsen(json, zutaten);
        } catch (Exception e) {
            System.err.println("Online-Suche nicht verfuegbar: " + e.getMessage());
        }
        return ergebnis;
    }

    private String httpGet(String urlText) throws Exception {
    	URL url = new URL(urlText);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET"); c.setConnectTimeout(4000); c.setReadTimeout(4000);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(c.getInputStream()))) {
            String z; while ((z = r.readLine()) != null) sb.append(z);
        }
        return sb.toString();
    }

    private List<Rezept> jsonParsen(String json, List<String> zutaten) {
        List<Rezept> liste = new ArrayList<>();
        if (json == null || json.contains("\"meals\":null")) return liste;
        String[] teile = json.split("\"strMeal\":\"");
        for (int i = 1; i < teile.length && liste.size() < 5; i++) {
            String name = teile[i].split("\"")[0];
            liste.add(new Rezept(name, "Online-Rezept von TheMealDB",
                new ArrayList<>(zutaten), "Siehe Quelle", "TheMealDB"));
        }
        return liste;
    }

    @Override
    public String getAnbieterName() { return "TheMealDB (Online)"; }
}
