package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Produkt;
import de.hwrberlin.kuehlschrank.model.Produktkategorie;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class ProduktAnsicht {
    private final KuehlschrankVerwaltung verwaltung;
    private DefaultTableModel model;
    private JTable table;

    public ProduktAnsicht(KuehlschrankVerwaltung v) { this.verwaltung = v; }

    public JPanel createPanel() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        model = new DefaultTableModel(new Object[]{"Name","Kategorie","Menge","MHD","Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        refresh();
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(formPanel(), BorderLayout.SOUTH);
        return p;
    }

    private JPanel formPanel() {
        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        JTextField name = new JTextField();
        JTextField menge = new JTextField();
        JTextField einheit = new JTextField();
        JTextField min = new JTextField("0");
        JTextField mhd = new JTextField(LocalDate.now().plusDays(7).toString());
        JComboBox<Produktkategorie> kat = new JComboBox<>(Produktkategorie.values());
        JButton add = new JButton("Produkt hinzufuegen / aktualisieren");
        add.addActionListener(e -> {
            try {
                double m = Double.parseDouble(menge.getText().trim());
                double mn = Double.parseDouble(min.getText().trim());
                verwaltung.produktHinzufuegen(new Produkt(name.getText().trim(), (Produktkategorie)kat.getSelectedItem(), m, einheit.getText().trim(), LocalDate.parse(mhd.getText().trim()), mn));
                refresh();
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE); }
        });
        form.add(new JLabel("Name:")); form.add(name);
        form.add(new JLabel("Menge:")); form.add(menge);
        form.add(new JLabel("Einheit:")); form.add(einheit);
        form.add(new JLabel("Kategorie:")); form.add(kat);
        form.add(new JLabel("Mindestmenge:")); form.add(min);
        form.add(new JLabel("MHD (YYYY-MM-DD):")); form.add(mhd);
        form.add(new JLabel("")); form.add(add);

        JButton del = new JButton("Ausgewaehltes loeschen");
        del.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                String n = (String) model.getValueAt(r, 0);
                verwaltung.produktEntfernen(n);
                refresh();
            }
        });
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(form, BorderLayout.CENTER);
        bottom.add(del, BorderLayout.SOUTH);
        return bottom;
    }

    public void refresh() {
        if (model == null) return;
        model.setRowCount(0);
        for (Produkt p : verwaltung.alleProdukte()) {
            String status = p.istAbgelaufen() ? "ABGELAUFEN" : (p.laeufBaldAb(3) ? "Bald abgelaufen" : (p.brauchtnachkauf() ? "Nachkauf noetig" : "OK"));
            model.addRow(new Object[]{p.getName(), p.getKategorie(), p.getMenge() + " " + p.getEinheit(), p.getAblaufdatum(), status});
        }
    }
}
