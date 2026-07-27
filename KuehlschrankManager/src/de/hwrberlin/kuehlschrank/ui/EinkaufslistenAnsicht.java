package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Einkaufslisteneintrag;
import de.hwrberlin.kuehlschrank.model.Produktkategorie;
import de.hwrberlin.kuehlschrank.service.EinkaufslistenService;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EinkaufslistenAnsicht {
    private final KuehlschrankVerwaltung verwaltung;
    private final EinkaufslistenService service;
    private DefaultListModel<String> model;
    private JList<String> list;

    public EinkaufslistenAnsicht(KuehlschrankVerwaltung v, EinkaufslistenService s) {
        this.verwaltung = v;
        this.service = s;
    }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(KuehlschrankApp.BG_DARK);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Header
        JLabel heading = UiHelper.sectionLabel("\uD83D\uDED2  Einkaufsliste");
        root.add(heading, BorderLayout.NORTH);

        // Liste
        model = new DefaultListModel<>();
        list  = new JList<>(model);
        list.setBackground(KuehlschrankApp.BG_CARD);
        list.setForeground(KuehlschrankApp.TEXT_PRIMARY);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        list.setFixedCellHeight(42);
        list.setSelectionBackground(new Color(99, 179, 122, 50));
        list.setSelectionForeground(KuehlschrankApp.ACCENT);
        list.setCellRenderer(new EinkaufsListenRenderer());
        list.setBorder(null);

        JScrollPane scroll = UiHelper.scrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(KuehlschrankApp.BORDER, 1, true));
        root.add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);

        JButton gen    = UiHelper.accentButton("\uD83D\uDD04  Liste neu generieren");
        JButton bought = UiHelper.ghostButton("\u2713  Als gekauft markieren");
        JButton clean  = UiHelper.ghostButton("\uD83E\uDDF9  Gekaufte entfernen");
        JButton add    = UiHelper.ghostButton("+ Manuell hinzufuegen");

        gen.addActionListener(e -> { service.listeGenerieren(verwaltung); refresh(); });
        bought.addActionListener(e -> {
            String sel = list.getSelectedValue();
            if (sel != null) {
                service.alsGekauftMarkieren(
                        sel.replace("[ ] ", "").replace("[x] ", "").split("  ")[0]);
                refresh();
            }
        });
        clean.addActionListener(e -> { service.gekaufteEntfernen(); refresh(); });
        add.addActionListener(e -> {
            String n = JOptionPane.showInputDialog(null, "Name:");
            if (n != null && !n.isBlank()) {
                String m   = JOptionPane.showInputDialog(null, "Menge:", "1");
                String eih = JOptionPane.showInputDialog(null, "Einheit:", "Stueck");
                double md  = 1;
                try { md = Double.parseDouble(m); } catch (Exception ignored) {}
                service.eintragHinzufuegen(new Einkaufslisteneintrag(
                        n.trim(), md,
                        eih == null ? "Stueck" : eih.trim(),
                        Produktkategorie.SONSTIGES));
                refresh();
            }
        });

        btnRow.add(gen);
        btnRow.add(bought);
        btnRow.add(clean);
        btnRow.add(add);
        root.add(btnRow, BorderLayout.SOUTH);

        service.listeGenerieren(verwaltung);
        refresh();
        return root;
    }

    public void refresh() {
        if (model == null) return;
        model.clear();
        for (Einkaufslisteneintrag e : service.getEintraege())
            model.addElement(e.toString());
    }

    /** Custom Renderer fuer Einkaufslisten-Eintraege */
    private static class EinkaufsListenRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            String text = value == null ? "" : value.toString();
            boolean done = text.startsWith("[x]");

            lbl.setBackground(isSelected
                    ? new Color(99, 179, 122, 50)
                    : (index % 2 == 0 ? KuehlschrankApp.BG_CARD : KuehlschrankApp.BG_HOVER));
            lbl.setForeground(done
                    ? KuehlschrankApp.TEXT_SECONDARY
                    : KuehlschrankApp.TEXT_PRIMARY);
            lbl.setFont(done
                    ? new Font("Segoe UI", Font.ITALIC, 13)
                    : new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setBorder(new EmptyBorder(0, 16, 0, 16));
            lbl.setOpaque(true);
            return lbl;
        }
    }
}
