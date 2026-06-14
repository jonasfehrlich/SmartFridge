package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.service.EinkaufslistenService;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;
import de.hwrberlin.kuehlschrank.service.RezeptService;

import javax.swing.*;
import java.awt.*;

/**
 * Einstiegspunkt der Swing-Anwendung.
 * Vorlesung: GUI-Programmierung mit Swing.
 */
public class KuehlschrankApp {

    public static KuehlschrankVerwaltung verwaltung;
    public static EinkaufslistenService einkaufslistenService;
    public static RezeptService rezeptService;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            verwaltung = KuehlschrankVerwaltung.laden();
            einkaufslistenService = EinkaufslistenService.laden();
            rezeptService = new RezeptService(false);

            if (verwaltung.anzahlProdukte() == 0) {
                BeispieldatenLader.laden(verwaltung);
            }

            JFrame frame = new JFrame("Kuehlschrank-Manager – Swing");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new HauptFenster(verwaltung, einkaufslistenService, rezeptService).createPanel());
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
