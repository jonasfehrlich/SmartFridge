package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.service.EinkaufslistenService;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;
import de.hwrberlin.kuehlschrank.service.RezeptService;

import javax.swing.*;
import java.awt.*;

public class HauptFenster {
    private final KuehlschrankVerwaltung verwaltung;
    private final EinkaufslistenService einkaufslistenService;
    private final RezeptService rezeptService;

    public HauptFenster(KuehlschrankVerwaltung v, EinkaufslistenService e, RezeptService r) {
        this.verwaltung = v;
        this.einkaufslistenService = e;
        this.rezeptService = r;
    }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_BASE);

        // --- Titelleiste ---
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(
                    0, 0, new Color(0x1A, 0x1E, 0x2E),
                    getWidth(), 0, new Color(0x12, 0x14, 0x18)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(12, 20, 12, 20)));

        JLabel logo = new JLabel("\uD83C\uDF69  SmartFridge");
        logo.setFont(Theme.FONT_TITLE);
        logo.setForeground(Theme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Kuehlschrank-Manager  |  HWR Berlin");
        sub.setFont(Theme.FONT_SMALL);
        sub.setForeground(Theme.TEXT_MUTED);

        header.add(logo, BorderLayout.WEST);
        header.add(sub,  BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // --- EinkaufslistenAnsicht zentral instanziieren ---
        EinkaufslistenAnsicht einkaufsAnsicht =
            new EinkaufslistenAnsicht(verwaltung, einkaufslistenService);

        // --- Tabs ---
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setBackground(Theme.BG_BASE);
        tabs.setForeground(Theme.TEXT_MUTED);
        tabs.setFont(Theme.FONT_BODY);

        tabs.addTab("\uD83E\uDDCA  Inhalt",
            new ProduktAnsicht(verwaltung).createPanel());
        tabs.addTab("\uD83D\uDED2  Einkaufsliste",
            einkaufsAnsicht.createPanel());
        tabs.addTab("\uD83D\uDCD6  Rezepte",
            new RezeptAnsicht(verwaltung, rezeptService,
                einkaufslistenService, einkaufsAnsicht).createPanel());
        tabs.addTab("\uD83C\uDF73  Chaos-Pfanne",
            new ChaosPfanneAnsicht(verwaltung, rezeptService, einkaufsAnsicht).createPanel());
        tabs.addTab("\u26A0\uFE0F  Warnungen",
            new WarnungsAnsicht(verwaltung).createPanel());

        root.add(tabs, BorderLayout.CENTER);
        return root;
    }
}
