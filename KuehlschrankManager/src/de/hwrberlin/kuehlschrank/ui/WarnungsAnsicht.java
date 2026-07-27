package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Produkt;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WarnungsAnsicht {
    private final KuehlschrankVerwaltung verwaltung;

    public WarnungsAnsicht(KuehlschrankVerwaltung v) { this.verwaltung = v; }

    public JScrollPane createPanel() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(Theme.BG_SURFACE);
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        root.add(section("\uD83D\uDD34  ABGELAUFEN",
            verwaltung.abgelaufeneProdukte(),   Theme.DANGER,  Theme.DANGER_BG));
        root.add(Box.createVerticalStrut(12));
        root.add(section("\uD83D\uDFE0  Laeuft bald ab",
            verwaltung.baldAblaufendeProdukte(5), Theme.WARNING, Theme.WARNING_BG));
        root.add(Box.createVerticalStrut(12));
        root.add(section("\uD83D\uDD35  Nachkauf noetig",
            verwaltung.produkteMitNachkaufbedarf(), Theme.ACCENT, new Color(0x14, 0x25, 0x40)));
        root.add(Box.createVerticalGlue());

        return Theme.scrollPane(root);
    }

    private JPanel section(String title, List<Produkt> list, Color accent, Color accentBg) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Theme.BG_CARD);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent.darker().darker(), 1),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        // Header-Zeile
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        JLabel lbl = new JLabel(title);
        lbl.setFont(Theme.FONT_HEADING);
        lbl.setForeground(accent);
        JLabel cnt = Theme.badge(list.size() + " Eintraege", accent, accentBg);
        headerRow.add(lbl, BorderLayout.WEST);
        headerRow.add(cnt, BorderLayout.EAST);
        card.add(headerRow, BorderLayout.NORTH);

        if (list.isEmpty()) {
            JLabel ok = new JLabel("Keine Eintraege – alles in Ordnung!");
            ok.setFont(Theme.FONT_BODY);
            ok.setForeground(Theme.TEXT_MUTED);
            card.add(ok, BorderLayout.CENTER);
        } else {
            JPanel rows = new JPanel(new GridLayout(list.size(), 1, 0, 4));
            rows.setOpaque(false);
            for (Produkt p : list) {
                JPanel row = new JPanel(new BorderLayout(12, 0));
                row.setBackground(accentBg);
                row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accent.darker().darker(), 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));

                JLabel pName = new JLabel(p.getName());
                pName.setFont(Theme.FONT_BODY);
                pName.setForeground(Theme.TEXT_PRIMARY);

                JLabel pInfo = new JLabel("MHD: " + p.getAblaufdatum()
                    + "  |  " + p.getMenge() + " " + p.getEinheit());
                pInfo.setFont(Theme.FONT_SMALL);
                pInfo.setForeground(Theme.TEXT_MUTED);

                row.add(pName, BorderLayout.WEST);
                row.add(pInfo, BorderLayout.EAST);
                rows.add(row);
            }
            card.add(rows, BorderLayout.CENTER);
        }
        return card;
    }
}
