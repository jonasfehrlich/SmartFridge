package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Einkaufslisteneintrag;
import de.hwrberlin.kuehlschrank.model.Produktkategorie;
import de.hwrberlin.kuehlschrank.service.EinkaufslistenService;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;

import javax.swing.*;
import java.awt.*;

public class EinkaufslistenAnsicht {
    private final KuehlschrankVerwaltung verwaltung;
    private final EinkaufslistenService service;
    private DefaultListModel<Einkaufslisteneintrag> listModel;
    private JList<Einkaufslisteneintrag> list;

    public EinkaufslistenAnsicht(KuehlschrankVerwaltung v, EinkaufslistenService s) {
        this.verwaltung = v;
        this.service = s;
    }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Theme.BG_SURFACE);

        // --- Toolbar ---
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        toolbar.setBackground(Theme.BG_SURFACE);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        JButton gen    = Theme.secondaryButton("\uD83D\uDD04  Neu generieren");
        JButton bought = Theme.primaryButton("\u2713  Als gekauft");
        JButton clean  = Theme.secondaryButton("\uD83D\uDDD1  Gekaufte entfernen");
        JButton add    = Theme.secondaryButton("+ Manuell");

        gen.addActionListener(e -> { service.listeGenerieren(verwaltung); refresh(); });
        bought.addActionListener(e -> {
            Einkaufslisteneintrag sel = list.getSelectedValue();
            if (sel != null) {
                service.alsGekauftMarkieren(sel.getProduktname());
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
                    n.trim(), md, eih == null ? "Stueck" : eih.trim(),
                    Produktkategorie.SONSTIGES));
                refresh();
            }
        });

        toolbar.add(gen); toolbar.add(bought); toolbar.add(clean); toolbar.add(add);

        // --- Liste ---
        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setCellRenderer(new EinkaufslistenRenderer());
        list.setBackground(Theme.BG_SURFACE);
        list.setFixedCellHeight(52);
        list.setSelectionBackground(Theme.BG_CARD);
        list.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        service.listeGenerieren(verwaltung);
        refresh();

        root.add(toolbar,                BorderLayout.NORTH);
        root.add(Theme.scrollPane(list), BorderLayout.CENTER);
        return root;
    }

    public void refresh() {
        if (listModel == null) return;
        listModel.clear();
        for (Einkaufslisteneintrag e : service.getEintraege()) listModel.addElement(e);
    }

    /** Card-Renderer fuer Einkaufslisteneintraege. */
    private static class EinkaufslistenRenderer
            extends JPanel implements ListCellRenderer<Einkaufslisteneintrag> {

        private final JLabel name   = new JLabel();
        private final JLabel detail = new JLabel();
        private final JLabel check  = new JLabel();

        EinkaufslistenRenderer() {
            setLayout(new BorderLayout(12, 0));
            setOpaque(true);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));

            JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
            text.setOpaque(false);
            name.setFont(Theme.FONT_BODY);
            detail.setFont(Theme.FONT_SMALL);
            text.add(name);
            text.add(detail);

            check.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            check.setPreferredSize(new Dimension(28, 28));

            add(check, BorderLayout.WEST);
            add(text,  BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends Einkaufslisteneintrag> list,
                Einkaufslisteneintrag value, int index,
                boolean isSelected, boolean cellHasFocus) {

            boolean gekauft = value.isGekauft();
            setBackground(isSelected ? Theme.BG_CARD : Theme.BG_SURFACE);

            check.setText(gekauft ? "\u2611" : "\u2610");
            check.setForeground(gekauft ? Theme.SUCCESS : Theme.TEXT_MUTED);

            name.setText(value.getProduktname());
            name.setForeground(gekauft ? Theme.TEXT_FAINT : Theme.TEXT_PRIMARY);

            detail.setText(value.getBenoetigteMenge() + " "
                + value.getEinheit() + "  |  " + value.getKategorie());
            detail.setForeground(Theme.TEXT_MUTED);

            return this;
        }
    }
}
