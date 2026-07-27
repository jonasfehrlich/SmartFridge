package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Produkt;
import de.hwrberlin.kuehlschrank.model.Rezept;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;
import de.hwrberlin.kuehlschrank.service.RezeptService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab "Chaos-Pfanne": erkennt bald ablaufende Produkte und schlaegt
 * passende Rezepte vor. Modernes Dark-UI mit farblichen Markierungen.
 */
public class ChaosPfanneAnsicht {
    private static final int BALD_ABLAUFEND_TAGE = 5;

    private final KuehlschrankVerwaltung verwaltung;
    private final RezeptService rezeptService;
    private final EinkaufslistenAnsicht einkaufsAnsicht;

    private JPanel ergebnisPanel;
    private JScrollPane scrollPane;

    public ChaosPfanneAnsicht(KuehlschrankVerwaltung v, RezeptService r,
                               EinkaufslistenAnsicht ea) {
        this.verwaltung   = v;
        this.rezeptService = r;
        this.einkaufsAnsicht = ea;
    }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(KuehlschrankApp.BG_DARK);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Header-Bereich
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JLabel heading = new JLabel("\uD83C\uDF73  Chaos-Pfanne");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 20));
        heading.setForeground(KuehlschrankApp.TEXT_PRIMARY);

        JLabel sub = new JLabel("Erstellt Rezepte aus bald ablaufenden Produkten");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(KuehlschrankApp.TEXT_SECONDARY);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        titleBlock.add(heading);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(sub);

        JButton btn = UiHelper.accentButton("\uD83C\uDF73  Chaos-Pfanne erstellen");
        btn.addActionListener(e -> erstelleChaosPfanne());

        header.add(titleBlock, BorderLayout.WEST);
        header.add(btn, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Ergebnis-Bereich
        ergebnisPanel = new JPanel();
        ergebnisPanel.setLayout(new BoxLayout(ergebnisPanel, BoxLayout.Y_AXIS));
        ergebnisPanel.setBackground(KuehlschrankApp.BG_DARK);

        JLabel placeholder = new JLabel(
                "<html><center>Klicke auf \"Chaos-Pfanne erstellen\"<br>"
                + "um Rezeptvorschlaege fuer bald ablaufende Produkte zu erhalten.</center></html>");
        placeholder.setForeground(KuehlschrankApp.TEXT_SECONDARY);
        placeholder.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);
        placeholder.setBorder(new EmptyBorder(60, 0, 0, 0));
        ergebnisPanel.add(placeholder);

        scrollPane = UiHelper.scrollPane(ergebnisPanel);
        scrollPane.setBorder(null);
        root.add(scrollPane, BorderLayout.CENTER);

        return root;
    }

    private void erstelleChaosPfanne() {
        ergebnisPanel.removeAll();

        List<Produkt> baldAblaufend = verwaltung.baldAblaufendeProdukte(BALD_ABLAUFEND_TAGE);

        if (baldAblaufend.isEmpty()) {
            JLabel ok = new JLabel("\u2705  Alle Produkte sind noch gut haltbar – keine Chaos-Pfanne noetig!");
            ok.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            ok.setForeground(KuehlschrankApp.ACCENT);
            ok.setAlignmentX(Component.LEFT_ALIGNMENT);
            ok.setBorder(new EmptyBorder(8, 0, 0, 0));
            ergebnisPanel.add(ok);
            ergebnisPanel.revalidate();
            ergebnisPanel.repaint();
            return;
        }

        // --- Karte: Bald ablaufende Produkte ---
        ergebnisPanel.add(buildAblaufKarte(baldAblaufend));
        ergebnisPanel.add(Box.createVerticalStrut(12));

        // --- Rezeptvorschlaege ---
        List<Rezept> vorschlaege = rezeptService.erstelleChaosPfanne(verwaltung);
        List<String> ablaufNamen = new ArrayList<>();
        for (Produkt p : baldAblaufend) ablaufNamen.add(p.getName());

        if (vorschlaege.isEmpty()) {
            JLabel noRec = new JLabel("\uD83D\uDCA1  Keine passenden Rezepte gefunden – einfach alles zusammen anbraten!");
            noRec.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            noRec.setForeground(KuehlschrankApp.ACCENT_WARN);
            noRec.setAlignmentX(Component.LEFT_ALIGNMENT);
            ergebnisPanel.add(noRec);
        } else {
            JLabel rezLabel = UiHelper.sectionLabel("Rezeptvorschlaege:");
            rezLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            ergebnisPanel.add(rezLabel);
            ergebnisPanel.add(Box.createVerticalStrut(8));
            for (Rezept r : vorschlaege) {
                ergebnisPanel.add(buildRezeptKarte(r, ablaufNamen));
                ergebnisPanel.add(Box.createVerticalStrut(10));
            }
        }

        ergebnisPanel.revalidate();
        ergebnisPanel.repaint();
        scrollPane.getVerticalScrollBar().setValue(0);
    }

    private JPanel buildAblaufKarte(List<Produkt> baldAblaufend) {
        JPanel card = UiHelper.card();
        card.setLayout(new BorderLayout(0, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height + 200));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("\u26A0  Bald ablaufende Produkte (\u2264 " + BALD_ABLAUFEND_TAGE + " Tage)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(KuehlschrankApp.ACCENT_WARN);
        card.add(title, BorderLayout.NORTH);

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        LocalDate heute = LocalDate.now();
        for (Produkt p : baldAblaufend) {
            long tage = ChronoUnit.DAYS.between(heute, p.getAblaufdatum());
            String tagText = tage == 0 ? " — HEUTE noch haltbar!" : " — noch " + tage + " Tag(e)";
            Color c = tage == 0 ? KuehlschrankApp.ACCENT_DANGER
                    : tage <= 2 ? KuehlschrankApp.ACCENT_WARN
                    : KuehlschrankApp.TEXT_PRIMARY;

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
            row.setOpaque(false);
            JLabel dot  = new JLabel("●");
            dot.setForeground(c);
            dot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            JLabel name = new JLabel(p.getName());
            name.setFont(new Font("Segoe UI", Font.BOLD, 13));
            name.setForeground(KuehlschrankApp.TEXT_PRIMARY);
            JLabel info = new JLabel(tagText);
            info.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            info.setForeground(c);
            row.add(dot); row.add(name); row.add(info);
            list.add(row);
        }
        card.add(list, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildRezeptKarte(Rezept r, List<String> ablaufNamen) {
        JPanel card = UiHelper.card();
        card.setLayout(new BorderLayout(0, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height + 300));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Titel-Zeile
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titleRow.setOpaque(false);
        JLabel name = new JLabel(r.getName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 15));
        name.setForeground(KuehlschrankApp.TEXT_PRIMARY);
        JLabel zeit = UiHelper.badge(r.getZubereitungszeit(), KuehlschrankApp.ACCENT_BLUE);
        titleRow.add(name);
        titleRow.add(zeit);
        card.add(titleRow, BorderLayout.NORTH);

        // Beschreibung + Zutaten
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        JLabel beschr = new JLabel("<html>" + r.getBeschreibung() + "</html>");
        beschr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        beschr.setForeground(KuehlschrankApp.TEXT_SECONDARY);
        beschr.setBorder(new EmptyBorder(4, 0, 10, 0));
        beschr.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(beschr);

        JLabel zLabel = new JLabel("Zutaten:");
        zLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        zLabel.setForeground(KuehlschrankApp.TEXT_SECONDARY);
        zLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(zLabel);

        for (String z : r.getZutaten()) {
            boolean ablaufend = ablaufNamen.stream().anyMatch(n -> n.equalsIgnoreCase(z));
            boolean vorhanden = verwaltung.produktSuchen(z) != null;

            JPanel zRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 1));
            zRow.setOpaque(false);
            zRow.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel icon = new JLabel(ablaufend ? "\u26A0" : vorhanden ? "\u2713" : "+");
            Color col   = ablaufend ? KuehlschrankApp.ACCENT_WARN
                        : vorhanden ? KuehlschrankApp.ACCENT
                        : KuehlschrankApp.ACCENT_BLUE;
            icon.setForeground(col);
            icon.setFont(new Font("Segoe UI", Font.BOLD, 12));

            JLabel zName = new JLabel(z);
            zName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            zName.setForeground(KuehlschrankApp.TEXT_PRIMARY);

            String tagText = ablaufend ? "bald ablaufend!"
                           : vorhanden ? "vorhanden"
                           : "einkaufen empfohlen";
            JLabel tag = UiHelper.badge(tagText, col);

            zRow.add(icon); zRow.add(zName); zRow.add(tag);
            body.add(zRow);
        }
        card.add(body, BorderLayout.CENTER);
        return card;
    }
}
