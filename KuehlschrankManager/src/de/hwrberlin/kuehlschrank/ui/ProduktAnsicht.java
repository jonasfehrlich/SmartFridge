package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Produkt;
import de.hwrberlin.kuehlschrank.model.Produktkategorie;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
        root.setBackground(KuehlschrankApp.BG_DARK);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Tabelle
        model = new DefaultTableModel(
                new Object[]{"Name", "Kategorie", "Menge", "MHD", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        styleTable(table);
        refresh();

        JScrollPane tableScroll = UiHelper.scrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(KuehlschrankApp.BORDER, 1, true));

        // Formular
        JPanel form = buildForm();

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, form);
        split.setResizeWeight(0.65);
        split.setDividerSize(6);
        split.setBackground(KuehlschrankApp.BG_DARK);
        split.setBorder(null);

        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private void styleTable(JTable t) {
        t.setBackground(KuehlschrankApp.BG_CARD);
        t.setForeground(KuehlschrankApp.TEXT_PRIMARY);
        t.setSelectionBackground(new Color(99, 179, 122, 50));
        t.setSelectionForeground(KuehlschrankApp.ACCENT);
        t.setGridColor(KuehlschrankApp.BORDER);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(36);
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setFocusable(false);

        JTableHeader header = t.getTableHeader();
        header.setBackground(KuehlschrankApp.BG_DARK);
        header.setForeground(KuehlschrankApp.TEXT_SECONDARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, KuehlschrankApp.BORDER));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);

        // Status-Spalte farbig rendern
        t.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                String s = val == null ? "" : val.toString();
                setBackground(sel ? new Color(99, 179, 122, 50) : KuehlschrankApp.BG_CARD);
                setForeground(switch (s) {
                    case "ABGELAUFEN" -> KuehlschrankApp.ACCENT_DANGER;
                    case "Bald abgelaufen" -> KuehlschrankApp.ACCENT_WARN;
                    case "Nachkauf noetig" -> KuehlschrankApp.ACCENT_BLUE;
                    default -> KuehlschrankApp.ACCENT;
                });
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setOpaque(true);
                return this;
            }
        });
        // Standard-Renderer fuer restliche Spalten
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                setBackground(sel ? new Color(99, 179, 122, 50) : KuehlschrankApp.BG_CARD);
                setForeground(KuehlschrankApp.TEXT_PRIMARY);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setOpaque(true);
                return this;
            }
        };
        for (int i = 0; i < 4; i++) t.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
    }

    private JPanel buildForm() {
        JPanel card = UiHelper.card();
        card.setLayout(new BorderLayout(12, 12));
        card.setBackground(KuehlschrankApp.BG_CARD);

        JLabel heading = UiHelper.sectionLabel("\uD83D\uDCE6  Produkt hinzufuegen / aktualisieren");
        card.add(heading, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 4, 10, 10));
        grid.setOpaque(false);

        JTextField name    = UiHelper.textField("Name");
        JTextField menge   = UiHelper.textField("Menge");
        JTextField einheit = UiHelper.textField("Einheit");
        JTextField min     = UiHelper.textField("Mindestmenge");
        min.setText("0");
        JTextField mhd     = UiHelper.textField("MHD");
        mhd.setText(LocalDate.now().plusDays(7).toString());
        JComboBox<Produktkategorie> kat = new JComboBox<>(Produktkategorie.values());
        kat.setBackground(KuehlschrankApp.BG_CARD);
        kat.setForeground(KuehlschrankApp.TEXT_PRIMARY);
        kat.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        grid.add(styledLabel("Name"));     grid.add(name);
        grid.add(styledLabel("Menge"));    grid.add(menge);
        grid.add(styledLabel("Einheit"));  grid.add(einheit);
        grid.add(styledLabel("Kategorie"));grid.add(kat);
        grid.add(styledLabel("Mindestmenge")); grid.add(min);
        grid.add(styledLabel("MHD (YYYY-MM-DD)")); grid.add(mhd);

        card.add(grid, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        JButton add = UiHelper.accentButton("+ Hinzufuegen / Aktualisieren");
        JButton del = UiHelper.dangerButton("\uD83D\uDDD1  Ausgewaehlt loeschen");

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
                name.setText(""); menge.setText(""); einheit.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });
        del.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                String n = (String) model.getValueAt(r, 0);
                verwaltung.produktEntfernen(n);
                refresh();
            }
        });
        btnRow.add(add);
        btnRow.add(del);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(KuehlschrankApp.TEXT_SECONDARY);
        return l;
    }

    public void refresh() {
        if (model == null) return;
        model.setRowCount(0);
        for (Produkt p : verwaltung.alleProdukte()) {
            String status = p.istAbgelaufen() ? "ABGELAUFEN"
                    : (p.laeuftBaldAb(3) ? "Bald abgelaufen"
                    : (p.brauchtNachkauf() ? "Nachkauf noetig" : "OK"));
            model.addRow(new Object[]{
                    p.getName(), p.getKategorie(),
                    p.getMenge() + " " + p.getEinheit(),
                    p.getAblaufdatum(), status});
        }
    }
}
