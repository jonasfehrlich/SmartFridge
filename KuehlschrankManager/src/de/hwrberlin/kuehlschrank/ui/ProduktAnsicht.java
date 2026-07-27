package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Produkt;
import de.hwrberlin.kuehlschrank.model.Produktkategorie;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;

public class ProduktAnsicht {
    private final KuehlschrankVerwaltung verwaltung;
    private DefaultTableModel model;
    private JTable table;

    public ProduktAnsicht(KuehlschrankVerwaltung v) { this.verwaltung = v; }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Theme.BG_SURFACE);

        // --- Tabelle ---
        model = new DefaultTableModel(
            new Object[]{"Name", "Kategorie", "Menge", "MHD", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setBackground(Theme.BG_CARD);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(Theme.ACCENT.darker());
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.setBorder(BorderFactory.createEmptyBorder());

        // Spaltenbreiten
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);

        // Status-Spalte farbig
        table.getColumnModel().getColumn(4).setCellRenderer(Theme.statusRenderer());

        // Standard-Renderer fuer alle anderen Spalten
        DefaultTableCellRenderer std = new DefaultTableCellRenderer();
        std.setBackground(Theme.BG_CARD);
        std.setForeground(Theme.TEXT_PRIMARY);
        std.setFont(Theme.FONT_BODY);
        std.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        for (int i = 0; i < 4; i++) table.getColumnModel().getColumn(i).setCellRenderer(std);

        // Header
        JTableHeader header = table.getTableHeader();
        header.setBackground(Theme.BG_INPUT);
        header.setForeground(Theme.TEXT_MUTED);
        header.setFont(Theme.FONT_SMALL);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
            .setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));

        JScrollPane tableScroll = Theme.scrollPane(table);
        tableScroll.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        // --- Formular unten (Karte) ---
        JPanel formCard = Theme.card();
        formCard.setLayout(new BorderLayout(16, 8));
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        formCard.setBackground(Theme.BG_SURFACE);
        formCard.setOpaque(true);

        JLabel formTitle = new JLabel("Produkt hinzufuegen / aktualisieren");
        formTitle.setFont(Theme.FONT_HEADING);
        formTitle.setForeground(Theme.TEXT_PRIMARY);

        JPanel fields = new JPanel(new GridLayout(2, 6, 10, 8));
        fields.setOpaque(false);

        JTextField name    = Theme.inputField("Name");
        JTextField menge   = Theme.inputField("Menge");
        JTextField einheit = Theme.inputField("Einheit");
        JTextField min     = Theme.inputField("Mindestmenge");
        min.setText("0");
        JTextField mhd     = Theme.inputField("YYYY-MM-DD");
        mhd.setText(LocalDate.now().plusDays(7).toString());
        JComboBox<Produktkategorie> kat = new JComboBox<>(Produktkategorie.values());
        kat.setBackground(Theme.BG_INPUT);
        kat.setForeground(Theme.TEXT_PRIMARY);
        kat.setFont(Theme.FONT_BODY);

        JLabel lName  = new JLabel("Name");      lName.setForeground(Theme.TEXT_MUTED);  lName.setFont(Theme.FONT_SMALL);
        JLabel lMenge = new JLabel("Menge");     lMenge.setForeground(Theme.TEXT_MUTED); lMenge.setFont(Theme.FONT_SMALL);
        JLabel lEinh  = new JLabel("Einheit");   lEinh.setForeground(Theme.TEXT_MUTED);  lEinh.setFont(Theme.FONT_SMALL);
        JLabel lMin   = new JLabel("Mindest");   lMin.setForeground(Theme.TEXT_MUTED);   lMin.setFont(Theme.FONT_SMALL);
        JLabel lMhd   = new JLabel("MHD");       lMhd.setForeground(Theme.TEXT_MUTED);   lMhd.setFont(Theme.FONT_SMALL);
        JLabel lKat   = new JLabel("Kategorie"); lKat.setForeground(Theme.TEXT_MUTED);   lKat.setFont(Theme.FONT_SMALL);

        fields.add(lName); fields.add(lMenge); fields.add(lEinh);
        fields.add(lMin);  fields.add(lMhd);   fields.add(lKat);
        fields.add(name);  fields.add(menge);  fields.add(einheit);
        fields.add(min);   fields.add(mhd);    fields.add(kat);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton add = Theme.primaryButton("+ Hinzufuegen");
        JButton del = Theme.dangerButton("Loeschen");

        add.addActionListener(e -> {
            try {
                double m  = Double.parseDouble(menge.getText().trim());
                double mn = Double.parseDouble(min.getText().trim());
                verwaltung.produktHinzufuegen(new Produkt(
                    name.getText().trim(),
                    (Produktkategorie) kat.getSelectedItem(),
                    m, einheit.getText().trim(),
                    LocalDate.parse(mhd.getText().trim()), mn));
                refresh();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });
        del.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                verwaltung.produktEntfernen((String) model.getValueAt(r, 0));
                refresh();
            }
        });
        btnRow.add(add);
        btnRow.add(del);

        formCard.add(formTitle, BorderLayout.NORTH);
        formCard.add(fields,   BorderLayout.CENTER);
        formCard.add(btnRow,   BorderLayout.SOUTH);

        root.add(tableScroll, BorderLayout.CENTER);
        root.add(formCard,    BorderLayout.SOUTH);

        refresh();
        return root;
    }

    public void refresh() {
        if (model == null) return;
        model.setRowCount(0);
        for (Produkt p : verwaltung.alleProdukte()) {
            String status = p.istAbgelaufen() ? "ABGELAUFEN"
                : (p.laeuftBaldAb(3) ? "Bald abgelaufen"
                : (p.brauchtNachkauf() ? "Nachkauf noetig" : "OK"));
            model.addRow(new Object[]{
                p.getName(),
                p.getKategorie(),
                p.getMenge() + " " + p.getEinheit(),
                p.getAblaufdatum(),
                status
            });
        }
    }
}
