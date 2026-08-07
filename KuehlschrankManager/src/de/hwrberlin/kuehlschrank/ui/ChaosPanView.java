package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Product;
import de.hwrberlin.kuehlschrank.model.Recipe;
import de.hwrberlin.kuehlschrank.service.FridgeManager;
import de.hwrberlin.kuehlschrank.service.RecipeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab "Chaos Pan": detects soon-to-expire products and suggests
 * matching recipes. Modern dark UI with colour-coded indicators.
 */
public class ChaosPanView {
    private static final int EXPIRING_SOON_DAYS = 5;

    private final FridgeManager fridgeManager;
    private final RecipeService recipeService;
    private final ShoppingListView shoppingView;

    private JPanel resultPanel;
    private JScrollPane scrollPane;

    public ChaosPanView(FridgeManager fm, RecipeService rs, ShoppingListView sv) {
        this.fridgeManager = fm;
        this.recipeService = rs;
        this.shoppingView  = sv;
    }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(SmartFridgeApp.BG_DARK);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Header
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        // Heading: emoji + text separated to avoid □ blocks
        JPanel headingRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        headingRow.setOpaque(false);
        JLabel headEmoji = new JLabel("\uD83C\uDF73");
        headEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        headEmoji.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        JLabel headText = new JLabel("  Chaos Pan");
        headText.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headText.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        headingRow.add(headEmoji);
        headingRow.add(headText);

        JLabel sub = new JLabel("Creates recipes from soon-to-expire products");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(SmartFridgeApp.TEXT_SECONDARY);

        titleBlock.add(headingRow);
        titleBlock.add(Box.createVerticalStrut(3));
        titleBlock.add(sub);

        JButton btn = UiHelper.accentButton("\uD83C\uDF73  Create Chaos Pan");
        btn.addActionListener(e -> createChaosPan());

        header.add(titleBlock, BorderLayout.WEST);
        header.add(btn, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(SmartFridgeApp.BG_DARK);

        // Placeholder: emoji in its own label with Emoji font
        JPanel placeholder = new JPanel();
        placeholder.setLayout(new BoxLayout(placeholder, BoxLayout.Y_AXIS));
        placeholder.setOpaque(false);
        placeholder.setBorder(new EmptyBorder(60, 0, 0, 0));
        placeholder.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel phEmoji = new JLabel("\uD83C\uDF73");
        phEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        phEmoji.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        phEmoji.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel phText = new JLabel("<html><center>Click <b>\"Create Chaos Pan\"</b><br>"
                + "to get recipe suggestions<br>for soon-to-expire products.</center></html>");
        phText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phText.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        phText.setAlignmentX(Component.CENTER_ALIGNMENT);
        phText.setBorder(new EmptyBorder(10, 0, 0, 0));

        placeholder.add(phEmoji);
        placeholder.add(phText);
        resultPanel.add(placeholder);

        scrollPane = UiHelper.scrollPane(resultPanel);
        scrollPane.setBorder(null);
        root.add(scrollPane, BorderLayout.CENTER);
        return root;
    }

    private void createChaosPan() {
        resultPanel.removeAll();

        List<Product> expiring = fridgeManager.getExpiringSoon(EXPIRING_SOON_DAYS);

        if (expiring.isEmpty()) {
            JPanel okRow = emojiTextRow("\u2705", "All products are still fresh \u2013 no chaos pan needed!",
                    SmartFridgeApp.ACCENT, Font.PLAIN, 14);
            okRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            okRow.setBorder(new EmptyBorder(8, 0, 0, 0));
            resultPanel.add(okRow);
            resultPanel.revalidate();
            resultPanel.repaint();
            return;
        }

        resultPanel.add(buildExpiryCard(expiring));
        resultPanel.add(Box.createVerticalStrut(12));

        List<Recipe> suggestions = recipeService.createChaosPan(fridgeManager);
        List<String> expiringNames = new ArrayList<>();
        for (Product p : expiring) expiringNames.add(p.getName());

        if (suggestions.isEmpty()) {
            JPanel noRow = emojiTextRow("\uD83D\uDCA1",
                    "No matching recipes found \u2013 just fry everything together!",
                    SmartFridgeApp.ACCENT_WARN, Font.ITALIC, 13);
            noRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultPanel.add(noRow);
        } else {
            JLabel recLabel = UiHelper.sectionLabel("Recipe suggestions:");
            recLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultPanel.add(recLabel);
            resultPanel.add(Box.createVerticalStrut(8));
            for (Recipe r : suggestions) {
                resultPanel.add(buildRecipeCard(r, expiringNames));
                resultPanel.add(Box.createVerticalStrut(10));
            }
        }

        resultPanel.revalidate();
        resultPanel.repaint();
        scrollPane.getVerticalScrollBar().setValue(0);
    }

    private JPanel buildExpiryCard(List<Product> expiring) {
        JPanel card = UiHelper.card();
        card.setLayout(new BorderLayout(0, 10));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height + 200));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Title row
        JPanel titleRow = emojiTextRow("\u26A0",
                "  Expiring soon (\u2264 " + EXPIRING_SOON_DAYS + " days)",
                SmartFridgeApp.ACCENT_WARN, Font.BOLD, 14);
        card.add(titleRow, BorderLayout.NORTH);

        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setOpaque(false);

        for (Product p : expiring) {
            long daysLeft = p.getExpiryDate() != null
                    ? ChronoUnit.DAYS.between(LocalDate.now(), p.getExpiryDate()) : -1;

            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(5, 0, 5, 0));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Bullet + name (no emoji = no risk of block)
            JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            nameRow.setOpaque(false);
            JLabel dot = new JLabel("\u2022");
            dot.setFont(new Font("Segoe UI", Font.BOLD, 13));
            dot.setForeground(SmartFridgeApp.ACCENT_WARN);
            JLabel nameLbl = new JLabel(p.getName());
            nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nameLbl.setForeground(SmartFridgeApp.ACCENT_WARN);
            nameRow.add(dot);
            nameRow.add(nameLbl);

            String dayText = daysLeft == 0 ? "expires TODAY!"
                    : daysLeft == 1 ? "1 day left"
                    : daysLeft + " days left";
            JLabel infoLbl = new JLabel(dayText + "  |  Expiry: " + p.getExpiryDate());
            infoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            infoLbl.setForeground(SmartFridgeApp.TEXT_SECONDARY);

            row.add(nameRow, BorderLayout.WEST);
            row.add(infoLbl, BorderLayout.EAST);
            rows.add(row);

            JSeparator sep = new JSeparator();
            sep.setForeground(SmartFridgeApp.BORDER);
            sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            rows.add(sep);
        }
        card.add(rows, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildRecipeCard(Recipe recipe, List<String> expiringNames) {
        JPanel card = UiHelper.card();
        card.setLayout(new BorderLayout(0, 8));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height + 200));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Title row: emoji + name
        JPanel titleRow = emojiTextRow("\uD83C\uDF73",
                " " + recipe.getName() + "  (" + recipe.getPreparationTime() + " | " + recipe.getSource() + ")",
                SmartFridgeApp.ACCENT, Font.BOLD, 14);
        card.add(titleRow, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);

        JLabel descLbl = new JLabel("<html>" + recipe.getDescription() + "</html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLbl.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(descLbl);
        body.add(Box.createVerticalStrut(8));

        JLabel ingLabel = new JLabel("Ingredients:");
        ingLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        ingLabel.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        ingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(ingLabel);

        JPanel ingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        ingPanel.setOpaque(false);
        for (String ingredient : recipe.getIngredients()) {
            boolean isExpiring = expiringNames.stream().anyMatch(n -> n.equalsIgnoreCase(ingredient));
            boolean inFridge   = fridgeManager.findProduct(ingredient) != null;
            JLabel badge = UiHelper.badge(ingredient,
                    isExpiring ? SmartFridgeApp.ACCENT_WARN
                    : (inFridge ? SmartFridgeApp.ACCENT
                    : SmartFridgeApp.ACCENT_BLUE));
            if (isExpiring)
                badge.setToolTipText("\u26A0 Expiring soon \u2013 please use first!");
            else if (!inFridge)
                badge.setToolTipText("Not in fridge \u2013 add to shopping list?");
            ingPanel.add(badge);
        }
        body.add(ingPanel);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    // -------------------------------------------------------------------------
    // Helper: emoji + text as two separate labels in a FlowLayout panel
    // Emoji uses "Segoe UI Emoji" font to prevent □ blocks
    // -------------------------------------------------------------------------
    private static JPanel emojiTextRow(String emoji, String text, Color color, int fontStyle, int fontSize) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        row.setOpaque(false);
        JLabel emojiLbl = new JLabel(emoji);
        emojiLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, fontSize));
        emojiLbl.setForeground(color);
        JLabel textLbl = new JLabel(text);
        textLbl.setFont(new Font("Segoe UI", fontStyle, fontSize));
        textLbl.setForeground(color);
        row.add(emojiLbl);
        row.add(textLbl);
        return row;
    }
}
