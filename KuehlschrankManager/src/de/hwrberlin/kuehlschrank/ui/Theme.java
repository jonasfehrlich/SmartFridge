package de.hwrberlin.kuehlschrank.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Zentrales Design-System fuer den Kuehlschrank-Manager.
 * Definiert Farben, Schriften, wiederverwendbare Komponenten.
 */
public final class Theme {

    // --- Farbpalette (Dark Mode) ---
    public static final Color BG_BASE      = new Color(0x12, 0x14, 0x18);
    public static final Color BG_SURFACE   = new Color(0x1C, 0x1F, 0x26);
    public static final Color BG_CARD      = new Color(0x24, 0x28, 0x32);
    public static final Color BG_INPUT     = new Color(0x2A, 0x2E, 0x3A);
    public static final Color BORDER       = new Color(0x35, 0x3A, 0x48);

    public static final Color TEXT_PRIMARY  = new Color(0xE8, 0xEA, 0xF0);
    public static final Color TEXT_MUTED    = new Color(0x8A, 0x8F, 0xA8);
    public static final Color TEXT_FAINT    = new Color(0x55, 0x5A, 0x6E);

    public static final Color ACCENT        = new Color(0x4F, 0x8E, 0xF0);  // Blau
    public static final Color ACCENT_HOVER  = new Color(0x6A, 0xA3, 0xFF);
    public static final Color SUCCESS       = new Color(0x3D, 0xC, 0x7A);
    public static final Color SUCCESS_BG    = new Color(0x1A, 0x3D, 0x2A);
    public static final Color WARNING       = new Color(0xF0, 0xA5, 0x0A);
    public static final Color WARNING_BG    = new Color(0x3D, 0x2E, 0x08);
    public static final Color DANGER        = new Color(0xE8, 0x4A, 0x4A);
    public static final Color DANGER_BG     = new Color(0x3D, 0x14, 0x14);
    public static final Color CHAOS         = new Color(0xB0, 0x6C, 0xF0);  // Lila fuer Chaos-Pfanne
    public static final Color CHAOS_BG      = new Color(0x28, 0x18, 0x3D);

    // --- Schriften ---
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO    = new Font("Consolas",  Font.PLAIN, 13);

    private Theme() {}

    /** Setzt globale UIManager-Defaults fuer ein dunkles Look-and-Feel. */
    public static void apply() {
        UIManager.put("Panel.background",           BG_SURFACE);
        UIManager.put("ScrollPane.background",      BG_SURFACE);
        UIManager.put("Viewport.background",        BG_SURFACE);
        UIManager.put("Table.background",           BG_CARD);
        UIManager.put("Table.foreground",           TEXT_PRIMARY);
        UIManager.put("Table.gridColor",            BORDER);
        UIManager.put("Table.selectionBackground",  ACCENT.darker());
        UIManager.put("Table.selectionForeground",  TEXT_PRIMARY);
        UIManager.put("TableHeader.background",     BG_INPUT);
        UIManager.put("TableHeader.foreground",     TEXT_MUTED);
        UIManager.put("TableHeader.font",           FONT_SMALL);
        UIManager.put("List.background",            BG_CARD);
        UIManager.put("List.foreground",            TEXT_PRIMARY);
        UIManager.put("List.selectionBackground",   ACCENT.darker());
        UIManager.put("List.selectionForeground",   TEXT_PRIMARY);
        UIManager.put("TextArea.background",        BG_CARD);
        UIManager.put("TextArea.foreground",        TEXT_PRIMARY);
        UIManager.put("TextArea.caretForeground",   ACCENT);
        UIManager.put("TextField.background",       BG_INPUT);
        UIManager.put("TextField.foreground",       TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",  ACCENT);
        UIManager.put("TextField.border",           BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        UIManager.put("ComboBox.background",        BG_INPUT);
        UIManager.put("ComboBox.foreground",        TEXT_PRIMARY);
        UIManager.put("ComboBox.selectionBackground", ACCENT.darker());
        UIManager.put("Label.foreground",           TEXT_PRIMARY);
        UIManager.put("TabbedPane.background",      BG_BASE);
        UIManager.put("TabbedPane.foreground",      TEXT_MUTED);
        UIManager.put("TabbedPane.selected",        BG_SURFACE);
        UIManager.put("TabbedPane.selectedForeground", TEXT_PRIMARY);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.put("TabbedPane.tabInsets",       new Insets(8, 16, 8, 16));
        UIManager.put("TabbedPane.font",            FONT_BODY);
        UIManager.put("ScrollBar.background",       BG_BASE);
        UIManager.put("ScrollBar.thumb",            BORDER);
        UIManager.put("ScrollBar.track",            BG_BASE);
        UIManager.put("OptionPane.background",      BG_SURFACE);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
    }

    // --- Wiederverwendbare Komponenten ---

    /** Moderner Primaer-Button (gefuellt, Akzentfarbe). */
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? ACCENT_HOVER : ACCENT;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(Color.WHITE);
        b.setFont(FONT_BODY);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return b;
    }

    /** Sekundaer-Button (nur Rahmen, kein Fill). */
    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() ? BORDER : BG_CARD;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setForeground(TEXT_PRIMARY);
        b.setFont(FONT_BODY);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return b;
    }

    /** Danger-Button (rot). */
    public static JButton dangerButton(String text) {
        JButton b = secondaryButton(text);
        b.setForeground(DANGER);
        return b;
    }

    /** Karten-Panel mit abgerundeten Ecken und Schatten-Rand. */
    public static JPanel card() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        return p;
    }

    /** Kleines farbiges Badge-Label. */
    public static JLabel badge(String text, Color fg, Color bg) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        l.setForeground(fg);
        l.setFont(FONT_SMALL);
        l.setOpaque(false);
        l.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return l;
    }

    /** Trennlinie in der Oberflaechen-Farbe. */
    public static JSeparator separator() {
        JSeparator s = new JSeparator();
        s.setForeground(BORDER);
        s.setBackground(BG_SURFACE);
        return s;
    }

    /** Styled JScrollPane (unsichtbare Raender). */
    public static JScrollPane scrollPane(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setBackground(BG_SURFACE);
        sp.getViewport().setBackground(BG_SURFACE);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    /** Styled JTextField mit Placeholder-Logik. */
    public static JTextField inputField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(FONT_BODY);
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(BORDER, 8),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setToolTipText(placeholder);
        return f;
    }

    /** Gibt StatusBadge-Text und -Farben basierend auf Produkt-Status zurueck. */
    public static JLabel statusBadge(String status) {
        switch (status) {
            case "ABGELAUFEN":      return badge("ABGELAUFEN",  DANGER,  DANGER_BG);
            case "Bald abgelaufen": return badge("Bald abgelaufen", WARNING, WARNING_BG);
            case "Nachkauf noetig": return badge("Nachkauf",    ACCENT,  new Color(0x14, 0x25, 0x40));
            default:               return badge("OK",           SUCCESS, SUCCESS_BG);
        }
    }

    /** Tabellen-CellRenderer fuer die Status-Spalte. */
    public static DefaultTableCellRenderer statusRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, col);
                String v = value == null ? "" : value.toString();
                switch (v) {
                    case "ABGELAUFEN":       l.setForeground(DANGER);   break;
                    case "Bald abgelaufen":  l.setForeground(WARNING);  break;
                    case "Nachkauf noetig": l.setForeground(ACCENT);   break;
                    default:                l.setForeground(SUCCESS);  break;
                }
                l.setBackground(isSelected ? ACCENT.darker() : BG_CARD);
                l.setFont(FONT_BODY);
                l.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return l;
            }
        };
    }

    /** Einfacher abgerundeter Border fuer Inputs. */
    public static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        public RoundBorder(Color c, int r) { this.color = c; this.radius = r; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w-1, h-1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4,4,4,4); }
        @Override public Insets getBorderInsets(Component c, Insets i) {
            i.left = i.right = i.top = i.bottom = 4; return i;
        }
    }
}
