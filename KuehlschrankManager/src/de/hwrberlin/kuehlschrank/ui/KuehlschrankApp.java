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
        // Modernes Dark-Theme vor allen Swing-Aufrufen setzen
        Theme.apply();

        SwingUtilities.invokeLater(() -> {
            verwaltung          = KuehlschrankVerwaltung.laden();
            einkaufslistenService = EinkaufslistenService.laden();
            rezeptService       = new RezeptService(false);

            if (verwaltung.anzahlProdukte() == 0) {
                BeispieldatenLader.laden(verwaltung);
            }

            JFrame frame = new JFrame("SmartFridge – Kuehlschrank-Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(Theme.BG_BASE);
            frame.setContentPane(
                new HauptFenster(verwaltung, einkaufslistenService, rezeptService).createPanel());
            frame.setSize(1100, 720);
            frame.setMinimumSize(new Dimension(900, 600));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
