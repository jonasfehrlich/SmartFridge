package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Produkt;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class WarnungsAnsicht {
    private final KuehlschrankVerwaltung verwaltung;

    public WarnungsAnsicht(KuehlschrankVerwaltung v) {
        this.verwaltung = v;
    }

    public JScrollPane createPanel() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(KuehlschrankApp.BG_DARK);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        root.add(buildSection(
                "\u274C  Abgelaufen",
                verwaltung.abgelaufeneProdukte(),
                KuehlschrankApp.ACCENT_DANGER));
        root.add(Box.createVerticalStrut(12));
        root.add(buildSection(
                "\u26A0  Laeuft bald ab",
                verwaltung.baldAblaufendeProdukte(5),
                KuehlschrankApp.ACCENT_WARN));
        root.add(Box.createVerticalStrut(12));
        root.add(buildSection(
                "\uD83D\uDECD  Nachkauf noetig",
                verwaltung.produkteMitNachkaufbedarf(),
                KuehlschrankApp.ACCENT_BLUE));
        root.add(Box.createVerticalGlue());

        return UiHelper.scrollPane(root);
    }

    private JPanel buildSection(String title, List<Produkt> list, Color accentColor) {
        JPanel card = UiHelper.card();
        card.setLayout(new BorderLayout(0, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height + 300));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header mit Badge
        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerRow.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(accentColor);
        JLabel countBadge = UiHelper.badge(String.valueOf(list.size()), accentColor);
        headerRow.add(titleLbl);
        headerRow.add(countBadge);
        card.add(headerRow, BorderLayout.NORTH);

        // Eintraege
        if (list.isEmpty()) {
            JLabel ok = new JLabel("\u2705  Keine Eintraege – alles in Ordnung!");
            ok.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            ok.setForeground(KuehlschrankApp.TEXT_SECONDARY);
            card.add(ok, BorderLayout.CENTER);
        } else {
            JPanel rows = new JPanel();
            rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
            rows.setOpaque(false);

            for (Produkt p : list) {
                JPanel row = new JPanel(new BorderLayout(12, 0));
                row.setOpaque(false);
                row.setBorder(new EmptyBorder(5, 0, 5, 0));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel nameLbl = new JLabel(p.getName());
                nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                nameLbl.setForeground(KuehlschrankApp.TEXT_PRIMARY);

                JLabel infoLbl = new JLabel("MHD: " + p.getAblaufdatum()
                        + "  |  " + p.getMenge() + " " + p.getEinheit());
                infoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                infoLbl.setForeground(KuehlschrankApp.TEXT_SECONDARY);

                row.add(nameLbl, BorderLayout.WEST);
                row.add(infoLbl, BorderLayout.EAST);

                rows.add(row);
                // Trennlinie
                JSeparator sep = new JSeparator();
                sep.setForeground(KuehlschrankApp.BORDER);
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                rows.add(sep);
            }
            card.add(rows, BorderLayout.CENTER);
        }
        return card;
    }
}
