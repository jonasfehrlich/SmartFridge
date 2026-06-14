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

        JLabel title = new JLabel("Kuehlschrank-Manager", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        root.add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Inhalt", new ProduktAnsicht(verwaltung).createPanel());
        tabs.addTab("Einkaufsliste", new EinkaufslistenAnsicht(verwaltung, einkaufslistenService).createPanel());
        tabs.addTab("Rezepte", new RezeptAnsicht(verwaltung, rezeptService, einkaufslistenService).createPanel());
        tabs.addTab("Warnungen", new WarnungsAnsicht(verwaltung).createPanel());

        root.add(tabs, BorderLayout.CENTER);
        return root;
    }
}