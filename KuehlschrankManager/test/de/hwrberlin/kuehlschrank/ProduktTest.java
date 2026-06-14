package de.hwrberlin.kuehlschrank;
import de.hwrberlin.kuehlschrank.model.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

/** JUnit-Tests fuer Produkt. Vorlesung 2.3.2: Unit-Tests. */
public class ProduktTest {
    @Test public void testIstAbgelaufen() {
        Produkt p = new Produkt("Test", Produktkategorie.LEBENSMITTEL, 1, "Stueck", LocalDate.now().minusDays(1), 0);
        assertTrue(p.istAbgelaufen());
    }
    @Test public void testNichtAbgelaufen() {
        Produkt p = new Produkt("Test", Produktkategorie.LEBENSMITTEL, 1, "Stueck", LocalDate.now().plusDays(5), 0);
        assertFalse(p.istAbgelaufen());
    }
    @Test public void testLaeufBaldAb_Wahr() {
        Produkt p = new Produkt("Test", Produktkategorie.LEBENSMITTEL, 1, "Stueck", LocalDate.now().plusDays(2), 0);
        assertTrue(p.laeufBaldAb(3));
    }
    @Test public void testLaeufBaldAb_Falsch() {
        Produkt p = new Produkt("Test", Produktkategorie.LEBENSMITTEL, 1, "Stueck", LocalDate.now().plusDays(10), 0);
        assertFalse(p.laeufBaldAb(3));
    }
    @Test public void testBrauchtNachkauf() {
        Produkt p = new Produkt("Test", Produktkategorie.GETRAENKE, 0.5, "Liter", null, 1.0);
        assertTrue(p.brauchtnachkauf());
    }
    @Test public void testKeinNachkauf() {
        Produkt p = new Produkt("Test", Produktkategorie.GETRAENKE, 2.0, "Liter", null, 1.0);
        assertFalse(p.brauchtnachkauf());
    }
    @Test public void testKeinAblaufdatum_KeinAbsturz() {
        Produkt p = new Produkt("Salz", Produktkategorie.LEBENSMITTEL, 500, "Gramm", null, 0);
        assertFalse(p.istAbgelaufen()); assertFalse(p.laeufBaldAb(30));
    }
}
