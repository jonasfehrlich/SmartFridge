package de.hwrberlin.kuehlschrank;
import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.service.*;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** JUnit-Tests fuer EinkaufslistenService. Vorlesung 2.3.2. */
public class EinkaufslistenServiceTest {
    private EinkaufslistenService service;
    private KuehlschrankVerwaltung verwaltung;

    @BeforeEach public void setUp() {
        verwaltung = new KuehlschrankVerwaltung(); service = new EinkaufslistenService();
        verwaltung.produktHinzufuegen(new Produkt("Milch", Produktkategorie.MILCHPRODUKTE,
            0.2, "Liter", LocalDate.now().plusDays(5), 1.0));
        verwaltung.produktHinzufuegen(new Produkt("Kaese", Produktkategorie.MILCHPRODUKTE,
            500.0, "Gramm", LocalDate.now().plusDays(10), 100.0));
    }

    @Test public void testListeGenerieren() {
        service.listeGenerieren(verwaltung);
        List<Einkaufslisteneintrag> e = service.getEintraege();
        assertEquals(1, e.size()); assertEquals("Milch", e.get(0).getProduktname());
    }
    @Test public void testGekauftMarkieren() {
        service.listeGenerieren(verwaltung);
        assertTrue(service.alsGekauftMarkieren("Milch"));
        assertEquals(0, service.offeneEintraege().size());
    }
    @Test public void testGekaufteEntfernen() {
        service.listeGenerieren(verwaltung);
        service.alsGekauftMarkieren("Milch");
        service.gekaufteEntfernen();
        assertEquals(0, service.getEintraege().size());
    }
    @Test public void testKeineDoppelten() {
        Einkaufslisteneintrag e = new Einkaufslisteneintrag("Butter", 1, "Stueck", Produktkategorie.MILCHPRODUKTE);
        service.eintragHinzufuegen(e); 
        service.eintragHinzufuegen(e);
        assertEquals(1, service.getEintraege().size());
    }
    @Test public void testManuellHinzufuegen() {
        service.eintragHinzufuegen(new Einkaufslisteneintrag("Apfelsaft", 2, "Liter", Produktkategorie.GETRAENKE));
        assertEquals(1, service.getEintraege().size());
    }
}
