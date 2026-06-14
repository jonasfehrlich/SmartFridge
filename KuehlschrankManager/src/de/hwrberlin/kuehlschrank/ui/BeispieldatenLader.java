package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.*;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;

import java.time.LocalDate;

public class BeispieldatenLader {
    public static void laden(KuehlschrankVerwaltung v) {
        v.produktHinzufuegen(new KuehlschrankProdukt("Milch", Produktkategorie.MILCHPRODUKTE, 0.3, "Liter", LocalDate.now().plusDays(2), 1.0, "Tuerablage"));
        v.produktHinzufuegen(new KuehlschrankProdukt("Joghurt", Produktkategorie.MILCHPRODUKTE, 2.0, "Stueck", LocalDate.now().plusDays(4), 1.0, "Oberfach"));
        v.produktHinzufuegen(new KuehlschrankProdukt("Kaese", Produktkategorie.MILCHPRODUKTE, 150.0, "Gramm", LocalDate.now().plusDays(7), 100.0, "Oberfach"));
        v.produktHinzufuegen(new KuehlschrankProdukt("Paprika", Produktkategorie.OBST_GEMUESE, 2.0, "Stueck", LocalDate.now().plusDays(3), 1.0, "Gemuesefach"));
        v.produktHinzufuegen(new KuehlschrankProdukt("Karotten", Produktkategorie.OBST_GEMUESE, 300.0, "Gramm", LocalDate.now().plusDays(5), 200.0, "Gemuesefach"));
        v.produktHinzufuegen(new KuehlschrankProdukt("Haehnchenbrustfilet", Produktkategorie.FLEISCH_FISCH, 400.0, "Gramm", LocalDate.now().plusDays(1), 0.0, "Unterfach"));
        v.produktHinzufuegen(new Produkt("Salami", Produktkategorie.FLEISCH_FISCH, 50.0, "Gramm", LocalDate.now().minusDays(1), 0.0));
        v.produktHinzufuegen(new Produkt("Orangensaft", Produktkategorie.GETRAENKE, 0.2, "Liter", LocalDate.now().plusDays(10), 1.0));
        v.produktHinzufuegen(new Produkt("Insulin", Produktkategorie.MEDIKAMENTE, 1.0, "Flasche", LocalDate.now().plusDays(60), 1.0));
    }
}
