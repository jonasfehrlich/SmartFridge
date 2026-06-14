package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Rezept;
import de.hwrberlin.kuehlschrank.service.EinkaufslistenService;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;
import de.hwrberlin.kuehlschrank.service.RezeptService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RezeptAnsicht {
    private final KuehlschrankVerwaltung verwaltung;
    private final RezeptService rezeptService;
    private final EinkaufslistenService einkaufslistenService;

    private DefaultListModel<String> model;
    private JList<String> list;
    private JTextArea details;
    private List<Rezept> aktuelle;

    public RezeptAnsicht(KuehlschrankVerwaltung v, RezeptService r, EinkaufslistenService e) {
        this.verwaltung = v;
        this.rezeptService = r;
        this.einkaufslistenService = e;
    }

    public JPanel createPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));

        model = new DefaultListModel<>();
        list = new JList<>(model);
        details = new JTextArea();
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showDetails();
            }
        });

        JPanel left = new JPanel(new BorderLayout());
        left.setPreferredSize(new Dimension(260, 0));
        left.add(new JLabel("Rezepte"), BorderLayout.NORTH);
        left.add(new JScrollPane(list), BorderLayout.CENTER);

        p.add(left, BorderLayout.WEST);
        p.add(new JScrollPane(details), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton search = new JButton("Rezepte vorschlagen");
        JButton toList = new JButton("Fehlende Zutaten zur Einkaufsliste");

        search.addActionListener(e -> searchRecipes());
        toList.addActionListener(e -> addMissing());

        top.add(search);
        top.add(toList);
        p.add(top, BorderLayout.NORTH);

        return p;
    }

    private void searchRecipes() {
        aktuelle = rezeptService.rezepteVorschlagen(verwaltung);
        model.clear();

        for (Rezept r : aktuelle) {
            model.addElement(r.getName() + " (" + r.getZubereitungszeit() + " min)");
        }

        if (aktuelle.isEmpty()) {
            details.setText("Keine passenden Rezepte gefunden.");
        } else {
            details.setText("Bitte links ein Rezept auswählen.");
        }
    }

    private void showDetails() {
        int idx = list.getSelectedIndex();
        if (idx < 0 || aktuelle == null || idx >= aktuelle.size()) {
            return;
        }

        Rezept r = aktuelle.get(idx);
        StringBuilder sb = new StringBuilder();
        sb.append("Rezept: ").append(r.getName()).append("\n\n");
        sb.append("Beschreibung:\n").append(r.getBeschreibung()).append("\n\n");
        sb.append("Zutaten:\n");

        for (String z : r.getZutaten()) {
            sb.append("- ").append(z);
            if (verwaltung.produktSuchen(z) != null) {
                sb.append(" (vorhanden)");
            } else {
                sb.append(" (fehlt)");
            }
            sb.append("\n");
        }

        details.setText(sb.toString());
    }

    private void addMissing() {
        int idx = list.getSelectedIndex();
        if (idx < 0 || aktuelle == null || idx >= aktuelle.size()) {
            JOptionPane.showMessageDialog(null, "Bitte zuerst ein Rezept auswählen.");
            return;
        }

        Rezept r = aktuelle.get(idx);
        List<String> fehlend = rezeptService.fehlendeZutaten(r, verwaltung);

        for (String z : fehlend) {
            einkaufslistenService.eintragHinzufuegen(
                new de.hwrberlin.kuehlschrank.model.Einkaufslisteneintrag
                (z, 1, "Stueck", de.hwrberlin.kuehlschrank.model.Produktkategorie.SONSTIGES));
        }

        JOptionPane.showMessageDialog(null, fehlend.size() + " Zutaten hinzugefuegt.");
    }
}