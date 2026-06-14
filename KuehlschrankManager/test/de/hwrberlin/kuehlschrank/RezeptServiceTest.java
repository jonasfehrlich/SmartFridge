package de.hwrberlin.kuehlschrank;
import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.rezept.LokalerRezeptAnbieter;
import de.hwrberlin.kuehlschrank.service.*;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer RezeptService.
 * Vorlesung 2.1.4: Polymorphie – Interface-Implementierung als Test-Ersatz eingesetzt.
 */
public class RezeptServiceTest {
    private KuehlschrankVerwaltung v;
    private RezeptService service;

    @BeforeEach public void setUp() {
        v = new KuehlschrankVerwaltung();
        service = new RezeptService(new LokalerRezeptAnbieter()); // kein Netzwerk im Test
        v.produktHinzufuegen(new Produkt("Eier", Produktkategorie.LEBENSMITTEL, 4, "Stueck", LocalDate.now().plusDays(7), 0));
        v.produktHinzufuegen(new Produkt("Kaese", Produktkategorie.MILCHPRODUKTE, 200, "Gramm", LocalDate.now().plusDays(3), 0));
        v.produktHinzufuegen(new Produkt("Butter", Produktkategorie.MILCHPRODUKTE, 100, "Gramm", LocalDate.now().plusDays(4), 0));
    }

    @Test public void testRezepteGefunden() {
        List<Rezept> rezepte = service.rezepteVorschlagen(v);
        assertFalse(rezepte.isEmpty());
    }
    @Test public void testFehlendeZutatenNichtNull() {
        List<Rezept> rezepte = service.rezepteVorschlagen(v);
        assertNotNull(service.fehlendeZutaten(rezepte.get(0), v));
    }
    @Test public void testAnbieterName() {
        assertEquals("Lokale Rezeptdatenbank", service.getAnbieterName());
    }
}
