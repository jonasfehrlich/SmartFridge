package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.service.EinkaufslistenService;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;
import de.hwrberlin.kuehlschrank.service.RezeptService;

import javax.swing.*;
import java.awt.*;

/**
 * Einstiegspunkt der Swing-Anwendung.
 * Modernes UI ohne externe Bibliotheken – reine Swing-Eigenimplementierung.
 */
public class KuehlschrankApp {

    public static KuehlschrankVerwaltung verwaltung;
    public static EinkaufslistenService einkaufslistenService;
    public static RezeptService rezeptService;

    // Modernes Farbschema (Dark-Mode inspiriert, aber hell und clean)
    public static final Color BG_DARK        = new Color(24, 26, 32);
    public static final Color BG_CARD        = new Color(34, 37, 46);
    public static final Color BG_HOVER       = new Color(44, 48, 60);
    public static final Color ACCENT         = new Color(99, 179, 122);   // Gruen
    public static final Color ACCENT_WARN    = new Color(255, 183, 77);   // Orange
    public static final Color ACCENT_DANGER  = new Color(229, 92, 92);    // Rot
    public static final Color ACCENT_BLUE    = new Color(100, 160, 230);  // Blau
    public static final Color TEXT_PRIMARY   = new Color(230, 232, 240);
    public static final Color TEXT_SECONDARY = new Color(140, 145, 165);
    public static final Color BORDER         = new Color(55, 60, 75);

    public static void applyGlobalStyle() {
        // Eigenes modernes Look-and-Feel auf Basis von Nimbus
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception e2) { /* fallback */ }
        }

        // Globale UI-Defaults ueberschreiben
        UIManager.put("Panel.background",             BG_DARK);
        UIManager.put("OptionPane.background",         BG_CARD);
        UIManager.put("OptionPane.messageForeground",  TEXT_PRIMARY);
        UIManager.put("Label.foreground",              TEXT_PRIMARY);
        UIManager.put("Label.background",              BG_DARK);
        UIManager.put("TextField.background",          BG_CARD);
        UIManager.put("TextField.foreground",          TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",     ACCENT);
        UIManager.put("TextField.border",
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        UIManager.put("TextArea.background",           BG_CARD);
        UIManager.put("TextArea.foreground",           TEXT_PRIMARY);
        UIManager.put("TextArea.caretForeground",      ACCENT);
        UIManager.put("ComboBox.background",           BG_CARD);
        UIManager.put("ComboBox.foreground",           TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground",  BG_HOVER);
        UIManager.put("ComboBox.selectionForeground",  ACCENT);
        UIManager.put("List.background",               BG_CARD);
        UIManager.put("List.foreground",               TEXT_PRIMARY);
        UIManager.put("List.selectionBackground",      new Color(99, 179, 122, 60));
        UIManager.put("List.selectionForeground",      ACCENT);
        UIManager.put("Table.background",              BG_CARD);
        UIManager.put("Table.foreground",              TEXT_PRIMARY);
        UIManager.put("Table.selectionBackground",     new Color(99, 179, 122, 60));
        UIManager.put("Table.selectionForeground",     ACCENT);
        UIManager.put("Table.gridColor",               BORDER);
        UIManager.put("TableHeader.background",        BG_DARK);
        UIManager.put("TableHeader.foreground",        TEXT_SECONDARY);
        UIManager.put("ScrollPane.background",         BG_DARK);
        UIManager.put("ScrollBar.background",          BG_DARK);
        UIManager.put("ScrollBar.thumb",               BORDER);
        UIManager.put("TabbedPane.background",         BG_DARK);
        UIManager.put("TabbedPane.foreground",         TEXT_PRIMARY);
        UIManager.put("TabbedPane.selected",           BG_CARD);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
    }

    public static void main(String[] args) {
        applyGlobalStyle();
        SwingUtilities.invokeLater(() -> {
            verwaltung = KuehlschrankVerwaltung.laden();
            einkaufslistenService = EinkaufslistenService.laden();
            rezeptService = new RezeptService(false);

            if (verwaltung.anzahlProdukte() == 0) {
                BeispieldatenLader.laden(verwaltung);
            }

            JFrame frame = new JFrame("SmartFridge");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().setBackground(KuehlschrankApp.BG_DARK);
            frame.setContentPane(new HauptFenster(verwaltung, einkaufslistenService, rezeptService).createPanel());
            frame.setSize(1100, 750);
            frame.setMinimumSize(new Dimension(900, 600));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
