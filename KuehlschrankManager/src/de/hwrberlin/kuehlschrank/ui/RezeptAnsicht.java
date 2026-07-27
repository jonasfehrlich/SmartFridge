package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Rezept;
import de.hwrberlin.kuehlschrank.service.EinkaufslistenService;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;
import de.hwrberlin.kuehlschrank.service.RezeptService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Tab "Rezepte": schlaegt Rezepte vor und uebertraegt fehlende Zutaten
 * in die Einkaufsliste. Bug-Fix: Einkaufsliste aktualisiert sich sofort.
 */
public class RezeptAnsicht {
    private final KuehlschrankVerwaltung verwaltung;
    private final RezeptService rezeptService;
    private final EinkaufslistenService einkaufslistenService;
    private final EinkaufslistenAnsicht einkaufsAnsicht;

    private DefaultListModel<String> model;
    private JList<String> list;
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
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(KuehlschrankApp.BG_DARK);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        // --- Linke Rezeptliste ---
        model = new DefaultListModel<>();
        list  = new JList<>(model);
        list.setBackground(KuehlschrankApp.BG_CARD);
        list.setForeground(KuehlschrankApp.TEXT_PRIMARY);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        list.setFixedCellHeight(40);
        list.setSelectionBackground(new Color(99, 179, 122, 50));
        list.setSelectionForeground(KuehlschrankApp.ACCENT);
        list.setCellRenderer(new RezeptListenRenderer());
        list.addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) showDetails(); });

        JScrollPane listScroll = UiHelper.scrollPane(list);
        listScroll.setBorder(BorderFactory.createLineBorder(KuehlschrankApp.BORDER, 1, true));

        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(270, 0));
        leftPanel.add(UiHelper.sectionLabel("\uD83D\uDCD6  Vorschlaege"), BorderLayout.NORTH);
        leftPanel.add(listScroll, BorderLayout.CENTER);

        // --- Detail-Bereich ---
        details = new JTextArea();
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        details.setBackground(KuehlschrankApp.BG_CARD);
        details.setForeground(KuehlschrankApp.TEXT_PRIMARY);
        details.setCaretColor(KuehlschrankApp.ACCENT);
        details.setBorder(new EmptyBorder(16, 16, 16, 16));
        details.setText("Klicke auf \"Rezepte vorschlagen\", um passende Rezepte\nfuer deinen Kuehlschrankinhalt zu finden.");

        JScrollPane detailScroll = UiHelper.scrollPane(details);
        detailScroll.setBorder(BorderFactory.createLineBorder(KuehlschrankApp.BORDER, 1, true));

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, leftPanel, detailScroll);
        split.setDividerLocation(270);
        split.setDividerSize(6);
        split.setBackground(KuehlschrankApp.BG_DARK);
        split.setBorder(null);

        // --- Buttons ---
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        JButton search = UiHelper.accentButton("\uD83D\uDD0D  Rezepte vorschlagen");
        JButton toList = UiHelper.ghostButton("\uD83D\uDED2  Fehlende Zutaten zur Einkaufsliste");
        search.addActionListener(e -> searchRecipes());
        toList.addActionListener(e -> addMissing());
        btnRow.add(search);
        btnRow.add(toList);

        root.add(btnRow, BorderLayout.NORTH);
        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private void searchRecipes() {
        aktuelle = rezeptService.rezepteVorschlagen(verwaltung);
        model.clear();
        for (Rezept r : aktuelle)
            model.addElement(r.getName() + " (" + r.getZubereitungszeit() + ")");
        details.setText(aktuelle.isEmpty()
                ? "Keine passenden Rezepte gefunden."
                : "Bitte links ein Rezept auswaehlen.");
    }

    private void showDetails() {
        int idx = list.getSelectedIndex();
        if (idx < 0 || aktuelle == null || idx >= aktuelle.size()) return;
        Rezept r = aktuelle.get(idx);
        StringBuilder sb = new StringBuilder();
        sb.append("Rezept: ").append(r.getName()).append("\n\n");
        sb.append("Beschreibung:\n").append(r.getBeschreibung()).append("\n\n");
        sb.append("Zutaten:\n");
        for (String z : r.getZutaten()) {
            boolean da = verwaltung.produktSuchen(z) != null;
            sb.append(da ? "  ✓ " : "  ✗ ").append(z)
              .append(da ? "  (vorhanden)" : "  (fehlt)").append("\n");
        }
        details.setText(sb.toString());
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
        // Bug-Fix: Einkaufsliste sofort aktualisieren
        if (einkaufsAnsicht != null) einkaufsAnsicht.refresh();
        JOptionPane.showMessageDialog(null,
                fehlend.size() + " Zutat(en) zur Einkaufsliste hinzugefuegt.");
    }

    private static class RezeptListenRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            lbl.setBackground(isSelected
                    ? new Color(99, 179, 122, 50)
                    : (index % 2 == 0 ? KuehlschrankApp.BG_CARD : KuehlschrankApp.BG_HOVER));
            lbl.setForeground(isSelected ? KuehlschrankApp.ACCENT : KuehlschrankApp.TEXT_PRIMARY);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setBorder(new EmptyBorder(0, 16, 0, 16));
            lbl.setOpaque(true);
            return lbl;
        }
    }
}
