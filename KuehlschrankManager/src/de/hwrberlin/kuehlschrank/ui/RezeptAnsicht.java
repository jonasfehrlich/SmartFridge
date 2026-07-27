package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Rezept;
import de.hwrberlin.kuehlschrank.service.EinkaufslistenService;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;
import de.hwrberlin.kuehlschrank.service.RezeptService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Tab "Rezepte": schlaegt Rezepte vor und uebernimmt fehlende Zutaten in die
 * Einkaufsliste. Bug-Fix: Einkaufsliste wird nach dem Hinzufuegen sofort aktualisiert.
 */
public class RezeptAnsicht {
    private final KuehlschrankVerwaltung verwaltung;
    private final RezeptService rezeptService;
    private final EinkaufslistenService einkaufslistenService;
    private final EinkaufslistenAnsicht einkaufsAnsicht;

    private DefaultListModel<Rezept> listModel;
    private JList<Rezept> list;
    private JTextArea details;
    private List<Rezept> aktuelle;

    public RezeptAnsicht(KuehlschrankVerwaltung v, RezeptService r,
                         EinkaufslistenService e, EinkaufslistenAnsicht ea) {
        this.verwaltung = v;
        this.rezeptService = r;
        this.einkaufslistenService = e;
        this.einkaufsAnsicht = ea;
    }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Theme.BG_SURFACE);

        // --- Toolbar ---
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        toolbar.setBackground(Theme.BG_SURFACE);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        JButton search = Theme.primaryButton("\uD83D\uDD0D  Rezepte vorschlagen");
        JButton toList = Theme.secondaryButton("\uD83D\uDED2  Fehlende → Einkaufsliste");
        search.addActionListener(e -> searchRecipes());
        toList.addActionListener(e -> addMissing());
        toolbar.add(search);
        toolbar.add(toList);

        // --- Split: Liste links / Detail rechts ---
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setCellRenderer(new RezeptKarteRenderer());
        list.setBackground(Theme.BG_SURFACE);
        list.setFixedCellHeight(64);
        list.setSelectionBackground(Theme.BG_CARD);
        list.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showDetails();
        });

        details = new JTextArea();
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setFont(Theme.FONT_BODY);
        details.setBackground(Theme.BG_CARD);
        details.setForeground(Theme.TEXT_PRIMARY);
        details.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        details.setText("Rezepte vorschlagen und links auswaehlen.");
        details.setForeground(Theme.TEXT_MUTED);

        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            Theme.scrollPane(list),
            Theme.scrollPane(details));
        split.setDividerLocation(280);
        split.setDividerSize(4);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setBackground(Theme.BG_SURFACE);
        split.setOpaque(false);

        root.add(toolbar, BorderLayout.NORTH);
        root.add(split,   BorderLayout.CENTER);
        return root;
    }

    private void searchRecipes() {
        aktuelle = rezeptService.rezepteVorschlagen(verwaltung);
        listModel.clear();
        for (Rezept r : aktuelle) listModel.addElement(r);
        details.setText(aktuelle.isEmpty()
            ? "Keine passenden Rezepte gefunden."
            : "Bitte links ein Rezept auswaehlen.");
        details.setForeground(Theme.TEXT_MUTED);
    }

    private void showDetails() {
        int idx = list.getSelectedIndex();
        if (idx < 0 || aktuelle == null || idx >= aktuelle.size()) return;

        Rezept r = aktuelle.get(idx);
        StringBuilder sb = new StringBuilder();
        sb.append(r.getName()).append("\n");
        sb.append("\u2014  ").append(r.getZubereitungszeit())
          .append("  |  ").append(r.getQuelle()).append("\n\n");
        sb.append(r.getBeschreibung()).append("\n\n");
        sb.append("Zutaten:\n");
        for (String z : r.getZutaten()) {
            boolean da = verwaltung.produktSuchen(z) != null;
            sb.append(da ? "  \u2713  " : "  \u2717  ").append(z);
            sb.append(da ? "  (vorhanden)" : "  (fehlt)").append("\n");
        }
        details.setText(sb.toString());
        details.setForeground(Theme.TEXT_PRIMARY);
        details.setCaretPosition(0);
    }

    private void addMissing() {
        int idx = list.getSelectedIndex();
        if (idx < 0 || aktuelle == null || idx >= aktuelle.size()) {
            JOptionPane.showMessageDialog(null, "Bitte zuerst ein Rezept auswaehlen.");
            return;
        }
        Rezept r = aktuelle.get(idx);
        List<String> fehlend = rezeptService.fehlendeZutaten(r, verwaltung);
        for (String z : fehlend) {
            einkaufslistenService.eintragHinzufuegen(
                new de.hwrberlin.kuehlschrank.model.Einkaufslisteneintrag(
                    z, 1, "Stueck",
                    de.hwrberlin.kuehlschrank.model.Produktkategorie.SONSTIGES));
        }
        if (einkaufsAnsicht != null) einkaufsAnsicht.refresh();
        JOptionPane.showMessageDialog(null,
            fehlend.size() + " Zutat(en) zur Einkaufsliste hinzugefuegt.");
    }

    /** Karten-Renderer fuer die Rezept-JList. */
    private class RezeptKarteRenderer
            extends JPanel implements ListCellRenderer<Rezept> {
        private final JLabel titel  = new JLabel();
        private final JLabel info   = new JLabel();

        RezeptKarteRenderer() {
            setLayout(new BorderLayout(0, 4));
            setOpaque(true);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            titel.setFont(Theme.FONT_HEADING);
            info.setFont(Theme.FONT_SMALL);
            info.setForeground(Theme.TEXT_MUTED);
            add(titel, BorderLayout.CENTER);
            add(info,  BorderLayout.SOUTH);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends Rezept> list, Rezept value,
                int index, boolean isSelected, boolean cellHasFocus) {
            setBackground(isSelected ? Theme.BG_CARD : Theme.BG_SURFACE);
            titel.setForeground(Theme.TEXT_PRIMARY);
            titel.setText(value.getName());
            long fehlend = value.getZutaten().stream()
                .filter(z -> verwaltung.produktSuchen(z) == null).count();
            info.setText(value.getZubereitungszeit() + "  |  "
                + (fehlend == 0 ? "Alle Zutaten vorhanden"
                               : fehlend + " Zutat(en) fehlen"));
            return this;
        }
    }
}
