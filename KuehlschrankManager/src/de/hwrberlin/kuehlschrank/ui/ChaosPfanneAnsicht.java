package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Produkt;
import de.hwrberlin.kuehlschrank.model.Rezept;
import de.hwrberlin.kuehlschrank.service.KuehlschrankVerwaltung;
import de.hwrberlin.kuehlschrank.service.RezeptService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab "Chaos-Pfanne": erkennt bald ablaufende Produkte und schlaegt
 * passende Rezepte vor. Zutaten werden farbig gekennzeichnet.
 */
public class ChaosPfanneAnsicht {
    private static final int BALD_ABLAUFEND_TAGE = 5;

    private final KuehlschrankVerwaltung verwaltung;
    private final RezeptService rezeptService;
    private final EinkaufslistenAnsicht einkaufsAnsicht;

    private JPanel ergebnisPanel;
    private JScrollPane scroll;

    public ChaosPfanneAnsicht(KuehlschrankVerwaltung v, RezeptService r,
                               EinkaufslistenAnsicht ea) {
        this.verwaltung = v;
        this.rezeptService = r;
        this.einkaufsAnsicht = ea;
    }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Theme.BG_SURFACE);

        // --- Hero-Banner ---
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(
                    0, 0, Theme.CHAOS_BG,
                    getWidth(), getHeight(), new Color(0x14, 0x0A, 0x28)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        JLabel heroTitle = new JLabel("\uD83C\uDF73  Chaos-Pfanne");
        heroTitle.setFont(Theme.FONT_TITLE);
        heroTitle.setForeground(Theme.CHAOS);

        JLabel heroSub = new JLabel(
            "Nutze bald ablaufende Produkte fuer ein spontanes Rezept.");
        heroSub.setFont(Theme.FONT_BODY);
        heroSub.setForeground(Theme.TEXT_MUTED);

        JButton btn = Theme.primaryButton("\uD83C\uDF73  Jetzt erstellen") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? Theme.CHAOS.brighter() : Theme.CHAOS;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        JPanel heroText = new JPanel(new GridLayout(2, 1, 0, 4));
        heroText.setOpaque(false);
        heroText.add(heroTitle);
        heroText.add(heroSub);
        hero.add(heroText, BorderLayout.CENTER);
        hero.add(btn,      BorderLayout.EAST);

        // --- Ergebnis-Scrollbereich ---
        ergebnisPanel = new JPanel();
        ergebnisPanel.setLayout(new BoxLayout(ergebnisPanel, BoxLayout.Y_AXIS));
        ergebnisPanel.setBackground(Theme.BG_SURFACE);
        ergebnisPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel hint = new JLabel(
            "Klicke auf \"Jetzt erstellen\", um Rezeptvorschlaege zu sehen.");
        hint.setFont(Theme.FONT_BODY);
        hint.setForeground(Theme.TEXT_MUTED);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        ergebnisPanel.add(hint);

        scroll = Theme.scrollPane(ergebnisPanel);

        btn.addActionListener(e -> erstelleChaosPfanne());

        root.add(hero,   BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        return root;
    }

    private void erstelleChaosPfanne() {
        ergebnisPanel.removeAll();

        List<Produkt> baldAblaufend =
            verwaltung.baldAblaufendeProdukte(BALD_ABLAUFEND_TAGE);

        if (baldAblaufend.isEmpty()) {
            JLabel ok = new JLabel(
                "\u2705  Alle Produkte sind noch gut haltbar – kein Chaos noetig!");
            ok.setFont(Theme.FONT_BODY);
            ok.setForeground(Theme.SUCCESS);
            ok.setAlignmentX(Component.LEFT_ALIGNMENT);
            ergebnisPanel.add(ok);
            ergebnisPanel.revalidate();
            ergebnisPanel.repaint();
            return;
        }

        // --- Ablaufende Produkte ---
        JLabel secTitle = new JLabel("Bald ablaufende Produkte:");
        secTitle.setFont(Theme.FONT_HEADING);
        secTitle.setForeground(Theme.WARNING);
        secTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        ergebnisPanel.add(secTitle);
        ergebnisPanel.add(Box.createVerticalStrut(8));

        LocalDate heute = LocalDate.now();
        JPanel badgeRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        badgeRow.setOpaque(false);
        badgeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Produkt p : baldAblaufend) {
            long tage = ChronoUnit.DAYS.between(heute, p.getAblaufdatum());
            String txt = p.getName() + (tage == 0 ? " (heute!)" : " (" + tage + "d)");
            badgeRow.add(Theme.badge(txt, Theme.WARNING, Theme.WARNING_BG));
        }
        ergebnisPanel.add(badgeRow);
        ergebnisPanel.add(Box.createVerticalStrut(20));

        // --- Rezeptvorschlaege ---
        List<String> zutatenNamen = new ArrayList<>();
        for (Produkt p : baldAblaufend) zutatenNamen.add(p.getName());

        List<Rezept> vorschlaege = rezeptService.rezepteVorschlagen(verwaltung);

        if (vorschlaege.isEmpty()) {
            JLabel noRez = new JLabel(
                "Keine passenden Rezepte. Tipp: Einfach alles zusammen anbraten!");
            noRez.setFont(Theme.FONT_BODY);
            noRez.setForeground(Theme.TEXT_MUTED);
            noRez.setAlignmentX(Component.LEFT_ALIGNMENT);
            ergebnisPanel.add(noRez);
        } else {
            JLabel rezTitle = new JLabel("Rezeptvorschlaege:");
            rezTitle.setFont(Theme.FONT_HEADING);
            rezTitle.setForeground(Theme.CHAOS);
            rezTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            ergebnisPanel.add(rezTitle);
            ergebnisPanel.add(Box.createVerticalStrut(10));

            for (Rezept r : vorschlaege) {
                ergebnisPanel.add(rezeptKarte(r, zutatenNamen));
                ergebnisPanel.add(Box.createVerticalStrut(10));
            }
        }

        ergebnisPanel.revalidate();
        ergebnisPanel.repaint();
    }

    private JPanel rezeptKarte(Rezept r, List<String> ablaufend) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(Theme.BG_CARD);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.CHAOS.darker().darker(), 1),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        // Titel-Zeile
        JPanel titleRow = new JPanel(new BorderLayout(8, 0));
        titleRow.setOpaque(false);
        JLabel name = new JLabel(r.getName());
        name.setFont(Theme.FONT_HEADING);
        name.setForeground(Theme.TEXT_PRIMARY);
        JLabel zeit = Theme.badge(r.getZubereitungszeit(), Theme.CHAOS, Theme.CHAOS_BG);
        titleRow.add(name, BorderLayout.WEST);
        titleRow.add(zeit, BorderLayout.EAST);
        card.add(titleRow, BorderLayout.NORTH);

        // Beschreibung
        JLabel desc = new JLabel("<html>" + r.getBeschreibung() + "</html>");
        desc.setFont(Theme.FONT_BODY);
        desc.setForeground(Theme.TEXT_MUTED);
        card.add(desc, BorderLayout.CENTER);

        // Zutaten mit Badges
        JPanel zutatenRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        zutatenRow.setOpaque(false);
        for (String z : r.getZutaten()) {
            boolean isBald = ablaufend.stream().anyMatch(n -> n.equalsIgnoreCase(z));
            boolean da     = verwaltung.produktSuchen(z) != null;
            JLabel badge;
            if (isBald)       badge = Theme.badge("\u26A0 " + z, Theme.WARNING, Theme.WARNING_BG);
            else if (da)      badge = Theme.badge("\u2713 " + z, Theme.SUCCESS, Theme.SUCCESS_BG);
            else              badge = Theme.badge("+ " + z, Theme.ACCENT, new Color(0x14, 0x25, 0x40));
            zutatenRow.add(badge);
        }
        card.add(zutatenRow, BorderLayout.SOUTH);
        return card;
    }
}
