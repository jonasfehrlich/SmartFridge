package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.service.EinkaufslistenService;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;
import de.hwrberlin.kuehlschrank.service.RezeptService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HauptFenster {
    private final KuehlschrankVerwaltung verwaltung;
    private final EinkaufslistenService einkaufslistenService;
    private final RezeptService rezeptService;

    // Tab-Konstanten
    private static final String[] TAB_NAMES   = {"Inhalt", "Einkaufsliste", "Rezepte", "Chaos-Pfanne", "Warnungen"};
    private static final String[] TAB_ICONS   = {"\uD83D\uDCE6", "\uD83D\uDED2", "\uD83D\uDCD6", "\uD83C\uDF73", "\u26A0"};

    public HauptFenster(KuehlschrankVerwaltung v, EinkaufslistenService e, RezeptService r) {
        this.verwaltung = v;
        this.einkaufslistenService = e;
        this.rezeptService = r;
    }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(KuehlschrankApp.BG_DARK);

        // --- Moderner Header ---
        root.add(buildHeader(), BorderLayout.NORTH);

        // --- Tabs ---
        EinkaufslistenAnsicht einkaufsAnsicht =
                new EinkaufslistenAnsicht(verwaltung, einkaufslistenService);

        JTabbedPane tabs = buildTabbedPane();
        tabs.addTab(TAB_NAMES[0], new ProduktAnsicht(verwaltung).createPanel());
        tabs.addTab(TAB_NAMES[1], einkaufsAnsicht.createPanel());
        tabs.addTab(TAB_NAMES[2],
                new RezeptAnsicht(verwaltung, rezeptService,
                                  einkaufslistenService, einkaufsAnsicht).createPanel());
        tabs.addTab(TAB_NAMES[3],
                new ChaosPfanneAnsicht(verwaltung, rezeptService, einkaufsAnsicht).createPanel());
        tabs.addTab(TAB_NAMES[4], new WarnungsAnsicht(verwaltung).createPanel());

        // Tab-Icons setzen
        for (int i = 0; i < TAB_NAMES.length; i++) {
            JLabel lbl = new JLabel(TAB_ICONS[i] + "  " + TAB_NAMES[i]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(KuehlschrankApp.TEXT_SECONDARY);
            lbl.setBorder(new EmptyBorder(8, 14, 8, 14));
            tabs.setTabComponentAt(i, lbl);
        }
        tabs.addChangeListener(e -> {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                Component c = tabs.getTabComponentAt(i);
                if (c instanceof JLabel lbl) {
                    if (i == tabs.getSelectedIndex()) {
                        lbl.setForeground(KuehlschrankApp.ACCENT);
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else {
                        lbl.setForeground(KuehlschrankApp.TEXT_SECONDARY);
                        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    }
                }
            }
        });
        // Initial ersten Tab hervorheben
        if (tabs.getTabComponentAt(0) instanceof JLabel lbl) {
            lbl.setForeground(KuehlschrankApp.ACCENT);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        }

        root.add(tabs, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(KuehlschrankApp.BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, KuehlschrankApp.BORDER),
            new EmptyBorder(14, 24, 14, 24)
        ));

        // Logo + Titel
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel logo = new JLabel("\uD83D\uDC0B"); // Frosch-Emoji als Icon
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        JLabel title = new JLabel("SmartFridge");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(KuehlschrankApp.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Dein intelligenter Kuehlschrank-Manager");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(KuehlschrankApp.TEXT_SECONDARY);
        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        titleBlock.add(title);
        titleBlock.add(subtitle);
        left.add(logo);
        left.add(titleBlock);
        header.add(left, BorderLayout.WEST);

        // Rechts: Info-Badge
        JLabel badge = new JLabel("v1.0  |  HWR Berlin");
        badge.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        badge.setForeground(KuehlschrankApp.TEXT_SECONDARY);
        badge.setBorder(new EmptyBorder(0, 0, 0, 4));
        header.add(badge, BorderLayout.EAST);

        return header;
    }

    private JTabbedPane buildTabbedPane() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setBackground(KuehlschrankApp.BG_DARK);
        tabs.setForeground(KuehlschrankApp.TEXT_PRIMARY);
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, KuehlschrankApp.BORDER));
        UIManager.put("TabbedPane.tabInsets", new Insets(8, 16, 8, 16));
        return tabs;
    }
}
