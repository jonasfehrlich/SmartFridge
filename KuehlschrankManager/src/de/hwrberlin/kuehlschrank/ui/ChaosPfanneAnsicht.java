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
 * passende Rezepte vor. Ablaufende Zutaten werden besonders
 * gekennzeichnet; zusaetzlich benoetigte Artikel werden angezeigt.
 */
public class ChaosPfanneAnsicht {
    private static final int BALD_ABLAUFEND_TAGE = 5;

    private final KuehlschrankVerwaltung verwaltung;
    private final RezeptService rezeptService;
    private final EinkaufslistenAnsicht einkaufsAnsicht;

    private JTextArea ausgabe;

    public ChaosPfanneAnsicht(KuehlschrankVerwaltung v, RezeptService r,
                               EinkaufslistenAnsicht ea) {
        this.verwaltung = v;
        this.rezeptService = r;
        this.einkaufsAnsicht = ea;
    }

    public JPanel createPanel() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        ausgabe = new JTextArea();
        ausgabe.setEditable(false);
        ausgabe.setLineWrap(true);
        ausgabe.setWrapStyleWord(true);
        ausgabe.setFont(new Font("Monospaced", Font.PLAIN, 13));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btn = new JButton("Chaos-Pfanne erstellen");
        btn.addActionListener(e -> erstelleChaosPfanne());
        top.add(btn);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(ausgabe), BorderLayout.CENTER);

        ausgabe.setText("Klicke auf \"Chaos-Pfanne erstellen\", um Rezeptvorschlaege\n"
                + "fuer bald ablaufende Produkte zu erhalten.");
        return p;
    }

    private void erstelleChaosPfanne() {
        List<Produkt> baldAblaufend = verwaltung.baldAblaufendeProdukte(BALD_ABLAUFEND_TAGE);

        if (baldAblaufend.isEmpty()) {
            ausgabe.setText("Alle Produkte sind noch gut haltbar – keine Chaos-Pfanne noetig!");
            return;
        }

        List<String> zutatenNamen = new ArrayList<>();
        for (Produkt p : baldAblaufend) zutatenNamen.add(p.getName());
        for (Produkt p : verwaltung.alleProdukte())
            if (!zutatenNamen.contains(p.getName())) zutatenNamen.add(p.getName());

        List<Rezept> vorschlaege = rezeptService.erstelleChaosPfanne(verwaltung);

        StringBuilder sb = new StringBuilder();
        sb.append("=== CHAOS-PFANNE ===").append("\n");
        sb.append("Bald ablaufende Produkte (in ").append(BALD_ABLAUFEND_TAGE)
          .append(" Tagen):" ).append("\n");

        LocalDate heute = LocalDate.now();
        for (Produkt p : baldAblaufend) {
            long tage = ChronoUnit.DAYS.between(heute, p.getAblaufdatum());
            sb.append("  [!] ").append(p.getName());
            if (tage == 0) sb.append(" -- HEUTE noch haltbar!");
            else sb.append(" -- noch ").append(tage).append(" Tag(e)");
            sb.append("\n");
        }

        sb.append("\n");

        if (vorschlaege.isEmpty()) {
            sb.append("Keine passenden Rezepte gefunden.\n");
            sb.append("Tipp: Einfach alles zusammen in die Pfanne und anbraten!\n");
        } else {
            sb.append("Rezeptvorschlaege:\n");
            for (Rezept r : vorschlaege) {
                sb.append("\n  ").append(r.getName())
                  .append(" (").append(r.getZubereitungszeit()).append(")\n");
                sb.append("  ").append(r.getBeschreibung()).append("\n");
                sb.append("  Zutaten:\n");
                for (String z : r.getZutaten()) {
                    boolean ablaufend = zutatenNamen.subList(0, baldAblaufend.size())
                                                    .stream()
                                                    .anyMatch(n -> n.equalsIgnoreCase(z));
                    boolean vorhanden = verwaltung.produktSuchen(z) != null;
                    sb.append("    - ").append(z);
                    if (ablaufend) sb.append(" [bald ablaufend!]");
                    else if (vorhanden) sb.append(" (vorhanden)");
                    else sb.append(" [+ einkaufen empfohlen]");
                    sb.append("\n");
                }
            }
        }

        ausgabe.setText(sb.toString());
        ausgabe.setCaretPosition(0);
    }
}
