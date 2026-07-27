package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Product;
import de.hwrberlin.kuehlschrank.service.FridgeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class WarningsView {
    private final FridgeManager fridgeManager;

    public WarningsView(FridgeManager fm) {
        this.fridgeManager = fm;
    }

    /** Returns a JPanel (required by MainWindow's CardLayout). */
    public JPanel createPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(SmartFridgeApp.BG_DARK);
        content.setBorder(new EmptyBorder(16, 16, 16, 16));

        content.add(buildSection(
                "\u274C  Expired",
                fridgeManager.getExpiredProducts(),
                SmartFridgeApp.ACCENT_DANGER));
        content.add(Box.createVerticalStrut(12));
        content.add(buildSection(
                "\u26A0  Expiring soon",
                fridgeManager.getExpiringSoon(5),
                SmartFridgeApp.ACCENT_WARN));
        content.add(Box.createVerticalStrut(12));
        content.add(buildSection(
                "\uD83D\uDECD  Restock needed",
                fridgeManager.getProductsNeedingRestock(),
                SmartFridgeApp.ACCENT_BLUE));
        content.add(Box.createVerticalGlue());

        // Wrap in a scrollable JPanel so the return type is JPanel
        JScrollPane scroll = UiHelper.scrollPane(content);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(SmartFridgeApp.BG_DARK);
        root.add(scroll, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildSection(String title, List<Product> list, Color accentColor) {
        JPanel card = UiHelper.card();
        card.setLayout(new BorderLayout(0, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height + 300));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerRow.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(accentColor);
        JLabel countBadge = UiHelper.badge(String.valueOf(list.size()), accentColor);
        headerRow.add(titleLbl);
        headerRow.add(countBadge);
        card.add(headerRow, BorderLayout.NORTH);

        if (list.isEmpty()) {
            JLabel ok = new JLabel("\u2705  No entries \u2013 everything looks good!");
            ok.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            ok.setForeground(SmartFridgeApp.TEXT_SECONDARY);
            card.add(ok, BorderLayout.CENTER);
        } else {
            JPanel rows = new JPanel();
            rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
            rows.setOpaque(false);

            for (Product p : list) {
                JPanel row = new JPanel(new BorderLayout(12, 0));
                row.setOpaque(false);
                row.setBorder(new EmptyBorder(5, 0, 5, 0));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel nameLbl = new JLabel(p.getName());
                nameLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                nameLbl.setForeground(SmartFridgeApp.TEXT_PRIMARY);

                JLabel infoLbl = new JLabel("Expiry: " + p.getExpiryDate()
                        + "  |  " + p.getQuantity() + " " + p.getUnit());
                infoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                infoLbl.setForeground(SmartFridgeApp.TEXT_SECONDARY);

                row.add(nameLbl, BorderLayout.WEST);
                row.add(infoLbl, BorderLayout.EAST);
                rows.add(row);
                JSeparator sep = new JSeparator();
                sep.setForeground(SmartFridgeApp.BORDER);
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                rows.add(sep);
            }
            card.add(rows, BorderLayout.CENTER);
        }
        return card;
    }
}
