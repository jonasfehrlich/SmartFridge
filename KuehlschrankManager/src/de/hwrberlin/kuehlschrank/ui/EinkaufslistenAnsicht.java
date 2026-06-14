package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Einkaufslisteneintrag;
import de.hwrberlin.kuehlschrank.model.Produktkategorie;
import de.hwrberlin.kuehlschrank.service.EinkaufslistenService;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EinkaufslistenAnsicht {
    private final KuehlschrankVerwaltung verwaltung;
    private final EinkaufslistenService service;
    private DefaultListModel<String> model;
    private JList<String> list;

    public EinkaufslistenAnsicht(KuehlschrankVerwaltung v, EinkaufslistenService s) {
        this.verwaltung = v; this.service = s;
    }

    public JPanel createPanel() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        model = new DefaultListModel<>();
        list = new JList<>(model);
        p.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton gen = new JButton("Liste neu generieren");
        JButton bought = new JButton("Als gekauft markieren");
        JButton clean = new JButton("Gekaufte entfernen");
        JButton add = new JButton("Manuell hinzufuegen");

        gen.addActionListener(e -> { service.listeGenerieren(verwaltung); refresh(); });
        bought.addActionListener(e -> {
            String sel = list.getSelectedValue();
            if (sel != null) { service.alsGekauftMarkieren(sel.replace("[ ] ", "").replace("[x] ", "").split("  ")[0]); refresh(); }
        });
        clean.addActionListener(e -> { service.gekaufteEntfernen(); refresh(); });
        add.addActionListener(e -> {
            String n = JOptionPane.showInputDialog(null, "Name:");
            if (n != null && !n.isBlank()) {
                String m = JOptionPane.showInputDialog(null, "Menge:", "1");
                String eih = JOptionPane.showInputDialog(null, "Einheit:", "Stueck");
                double md = 1;
                try { md = Double.parseDouble(m); } catch(Exception ignored) {}
                service.eintragHinzufuegen(new Einkaufslisteneintrag(n.trim(), md, eih == null ? "Stueck" : eih.trim(), Produktkategorie.SONSTIGES));
                refresh();
            }
        });

        buttons.add(gen); buttons.add(bought); buttons.add(clean); buttons.add(add);
        p.add(buttons, BorderLayout.NORTH);
        service.listeGenerieren(verwaltung); refresh();
        return p;
    }

    private void refresh() {
        model.clear();
        for (Einkaufslisteneintrag e : service.getEintraege()) model.addElement(e.toString());
    }
}
