package de.hwrberlin.kuehlschrank.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Reusable factory methods for modern Swing UI components.
 * Redesigned for the FlatLaf dark theme with richer visuals.
 */
public class UiHelper {

    // -------------------------------------------------------------------------
    // Buttons
    // -------------------------------------------------------------------------

    /** Filled green accent button with pill shape. */
    public static JButton accentButton(String text) {
        JButton btn = roundButton(text,
                SmartFridgeApp.ACCENT, new Color(15, 20, 25), true);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(new EmptyBorder(10, 22, 10, 22));
        return btn;
    }

    /** Outlined ghost button. */
    public static JButton ghostButton(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover() || getModel().isPressed()) {
                    g2.setColor(SmartFridgeApp.BG_HOVER);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                }
                g2.setColor(SmartFridgeApp.BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0.6f, 0.6f, getWidth()-1.2f, getHeight()-1.2f, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        return btn;
    }

    /** Red danger button. */
    public static JButton dangerButton(String text) {
        JButton btn = roundButton(text, SmartFridgeApp.ACCENT_DANGER, Color.WHITE, true);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(new EmptyBorder(10, 22, 10, 22));
        return btn;
    }

    /**
     * Large icon + label button for toolbar / action areas.
     * icon: large emoji or symbol, label: short description text.
     */
    public static JButton iconButton(String icon, String label, Color accentColor) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isRollover() || getModel().isPressed()
                        ? new Color(accentColor.getRed(), accentColor.getGreen(),
                                    accentColor.getBlue(), 35)
                        : new Color(accentColor.getRed(), accentColor.getGreen(),
                                    accentColor.getBlue(), 18);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(),
                                      accentColor.getBlue(), 80));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 16, 16));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setLayout(new BorderLayout(4, 2));
        JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        JLabel textLbl = new JLabel(label, SwingConstants.CENTER);
        textLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        textLbl.setForeground(accentColor);
        btn.add(iconLbl, BorderLayout.CENTER);
        btn.add(textLbl, BorderLayout.SOUTH);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 14, 10, 14));
        btn.setPreferredSize(new Dimension(90, 68));
        return btn;
    }

    // -------------------------------------------------------------------------
    // Cards & Containers
    // -------------------------------------------------------------------------

    /** Standard rounded card panel with subtle border. */
    public static JPanel card() {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SmartFridgeApp.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setColor(SmartFridgeApp.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));
        return card;
    }

    /**
     * KPI card: large number + icon + label, coloured accent line at top.
     * Used in the dashboard header bar.
     */
    public static JPanel kpiCard(String icon, String value, String label, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Card background
                g2.setColor(SmartFridgeApp.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                // Accent top bar
                g2.setColor(accent);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), 4, 4, 4));
                // Border
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(4, 2));
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLbl.setForeground(accent);

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelLbl.setForeground(SmartFridgeApp.TEXT_SECONDARY);

        JPanel right = new JPanel(new BorderLayout(0, 2));
        right.setOpaque(false);
        right.add(valueLbl, BorderLayout.CENTER);
        right.add(labelLbl, BorderLayout.SOUTH);

        card.add(iconLbl, BorderLayout.WEST);
        card.add(right,   BorderLayout.CENTER);
        return card;
    }

    // -------------------------------------------------------------------------
    // Labels & Text
    // -------------------------------------------------------------------------

    /** Bold section heading label. */
    public static JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));
        return lbl;
    }

    /** Coloured pill badge. */
    public static JLabel badge(String text, Color color) {
        JLabel lbl = new JLabel(" " + text + " ") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 35));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(),
                        getHeight(), getHeight()));
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1,
                        getHeight(), getHeight()));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(color);
        lbl.setBorder(new EmptyBorder(3, 8, 3, 8));
        lbl.setOpaque(false);
        return lbl;
    }

    // -------------------------------------------------------------------------
    // Inputs & Scroll
    // -------------------------------------------------------------------------

    /** Modern dark text field. */
    public static JTextField textField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setBackground(SmartFridgeApp.BG_CARD);
        tf.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        tf.setCaretColor(SmartFridgeApp.ACCENT);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SmartFridgeApp.BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)));
        return tf;
    }

    /** Thin dark scrollpane with slim rounded thumb (FlatLaf-compatible). */
    public static JScrollPane scrollPane(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(null);
        sp.setBackground(SmartFridgeApp.BG_DARK);
        sp.getViewport().setBackground(
                c instanceof JPanel ? ((JPanel) c).getBackground() : SmartFridgeApp.BG_DARK);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getHorizontalScrollBar().setUnitIncrement(16);
        return sp;
    }

    /** 1px horizontal divider line. */
    public static JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(SmartFridgeApp.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private static JButton roundButton(String text, Color bg, Color fg, boolean filled) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color paint = getModel().isPressed()  ? bg.darker()
                            : getModel().isRollover() ? bg.brighter()
                            : bg;
                g2.setColor(paint);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
