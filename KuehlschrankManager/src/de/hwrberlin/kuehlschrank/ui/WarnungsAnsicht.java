package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Produkt;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WarnungsAnsicht {
    private final KuehlschrankVerwaltung verwaltung;

    public WarnungsAnsicht(KuehlschrankVerwaltung v) {
        this.verwaltung = v;
    }

    public JScrollPane createPanel() {
        JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
        p.add(section("ABGELAUFEN", verwaltung.abgelaufeneProdukte()));
        p.add(section("Laeuft bald ab", verwaltung.baldAblaufendeProdukte(5)));
        p.add(section("Nachkauf noetig", verwaltung.produkteMitNachkaufbedarf()));

        return new JScrollPane(p);
    }

    private JPanel section(String title, List<Produkt> list) {
        JPanel s = new JPanel(new BorderLayout());
        s.setBorder(BorderFactory.createTitledBorder(title + " (" + list.size() + ")"));

        JTextArea a = new JTextArea();
        a.setEditable(false);

        if (list.isEmpty()) {
            a.setText("Keine Eintraege.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Produkt p : list) {
                sb.append("- ")
                  .append(p.getName())
                  .append(" | MHD: ")
                  .append(p.getAblaufdatum())
                  .append(" | ")
                  .append(p.getMenge())
                  .append(" ")
                  .append(p.getEinheit())
                  .append("\n");
            }
            a.setText(sb.toString());
        }

        s.add(a, BorderLayout.CENTER);
        return s;
    }
}