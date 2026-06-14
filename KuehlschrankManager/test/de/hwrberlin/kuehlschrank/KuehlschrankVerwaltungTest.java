package de.hwrberlin.kuehlschrank;
import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

/** JUnit-Tests fuer KuehlschrankVerwaltung (HashMap, Filter, Sortierung). */
public class KuehlschrankVerwaltungTest {
    private KuehlschrankVerwaltung v;

    @BeforeEach public void setUp() {
        v = new KuehlschrankVerwaltung();
        v.produktHinzufuegen(new Produkt("Milch", Produktkategorie.MILCHPRODUKTE,
            0.3, "Liter", LocalDate.now().plusDays(2), 1.0));
        v.produktHinzufuegen(new Produkt("Kaese", Produktkategorie.MILCHPRODUKTE,
            300.0, "Gramm", LocalDate.now().plusDays(14), 100.0));
        v.produktHinzufuegen(new Produkt("Altes Brot", Produktkategorie.LEBENSMITTEL,
            1, "Stueck", LocalDate.now().minusDays(3), 0));
    }

    @Test public void testAnzahl()     { assertEquals(3, v.anzahlProdukte()); }
    @Test public void testSuchen()     { assertNotNull(v.produktSuchen("MILCH")); }
    @Test public void testNichtFound() { assertNull(v.produktSuchen("Sahne")); }
    @Test public void testEntfernen()  { assertTrue(v.produktEntfernen("Milch")); assertEquals(2, v.anzahlProdukte()); }

    @Test public void testAbgelaufen() {
        ArrayList<Produkt> ab = v.abgelaufeneProdukte();
        assertEquals(1, ab.size()); assertEquals("Altes Brot", ab.get(0).getName());
    }
    @Test public void testBaldAblaufend() {
        boolean gefunden = v.baldAblaufendeProdukte(5).stream().anyMatch(p -> p.getName().equals("Milch"));
        assertTrue(gefunden);
    }
    @Test public void testNachkauf() {
        ArrayList<Produkt> nk = v.produkteMitNachkaufbedarf();
        assertEquals(1, nk.size()); assertEquals("Milch", nk.get(0).getName());
    }
    @Test public void testKategorie() {
        assertEquals(2, v.produkteNachKategorie(Produktkategorie.MILCHPRODUKTE).size());
    }
}
