package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Recipe;
import de.hwrberlin.kuehlschrank.model.ShoppingItem;
import de.hwrberlin.kuehlschrank.model.ProductCategory;
import de.hwrberlin.kuehlschrank.service.ShoppingListService;
import de.hwrberlin.kuehlschrank.service.FridgeManager;
import de.hwrberlin.kuehlschrank.service.RecipeService;
import de.hwrberlin.kuehlschrank.recipe.SpoonacularRecipeProvider;
import de.hwrberlin.kuehlschrank.recipe.LocalRecipeProvider;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tab "Recipes": suggests recipes from local DB or Spoonacular API.
 * Supports online/offline toggle, card-based recipe list, ingredient
 * availability indicators, and direct shopping list integration.
 *
 * Lecture 2.1.4: Runtime polymorphism – provider is swapped at runtime
 * without changing the RecipeService interface.
 */
public class RecipeView {

    private final FridgeManager        fridgeManager;
    private final RecipeService        recipeService;
    private final ShoppingListService  shoppingListService;
    private final ShoppingListView     shoppingView;

    // UI state
    private DefaultListModel<Recipe>   listModel;
    private JList<Recipe>              recipeList;
    private JEditorPane                detailArea;
    private JLabel                     statusLabel;
    private JLabel                     statusDot;
    private JToggleButton              onlineToggle;
    private List<Recipe>               currentRecipes = new ArrayList<>();
    private boolean                    onlineMode     = false;

    public RecipeView(FridgeManager fm, RecipeService rs,
                      ShoppingListService sl, ShoppingListView sv) {
        this.fridgeManager       = fm;
        this.recipeService       = rs;
        this.shoppingListService = sl;
        this.shoppingView        = sv;
    }

    // -------------------------------------------------------------------------
    // Public entry point
    // -------------------------------------------------------------------------

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(SmartFridgeApp.BG_DARK);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        root.add(buildToolbar(),   BorderLayout.NORTH);
        root.add(buildSplitArea(), BorderLayout.CENTER);
        return root;
    }

    // -------------------------------------------------------------------------
    // Toolbar
    // -------------------------------------------------------------------------

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        JButton searchBtn  = UiHelper.accentButton("\uD83D\uDD0D  Suggest Recipes");
        JButton addListBtn = UiHelper.ghostButton("\uD83D\uDED2  Add Missing to Shopping List");
        searchBtn .addActionListener(e -> searchRecipes());
        addListBtn.addActionListener(e -> addMissingToShoppingList());
        left.add(searchBtn);
        left.add(addListBtn);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        // Status: dot + text as separate labels to avoid emoji block
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        statusRow.setOpaque(false);
        statusDot = new JLabel("\uD83D\uDFE1");
        statusDot.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        statusDot.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        statusLabel = new JLabel("Offline mode");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        statusRow.add(statusDot);
        statusRow.add(statusLabel);

        onlineToggle = buildOnlineToggle();
        right.add(statusRow);
        right.add(onlineToggle);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JToggleButton buildOnlineToggle() {
        JToggleButton btn = new JToggleButton("Online") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = isSelected()
                        ? new Color(99, 179, 122, 200)
                        : SmartFridgeApp.BG_HOVER;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(isSelected() ? SmartFridgeApp.ACCENT : SmartFridgeApp.BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        btn.setSelected(false);
        btn.addActionListener(e -> toggleOnlineMode(btn.isSelected()));
        return btn;
    }

    private void toggleOnlineMode(boolean online) {
        onlineMode = online;
        if (online) {
            recipeService.setProvider(new SpoonacularRecipeProvider("33f9c011c2a14681b1bb71041e3f4081"));
            statusDot.setText("\uD83D\uDFE2");
            statusLabel.setText("Online \u2013 Spoonacular API");
            statusLabel.setForeground(SmartFridgeApp.ACCENT);
            statusDot.setForeground(SmartFridgeApp.ACCENT);
            onlineToggle.setText("Online  \u2713");
        } else {
            recipeService.setProvider(new LocalRecipeProvider());
            statusDot.setText("\uD83D\uDFE1");
            statusLabel.setText("Offline mode");
            statusLabel.setForeground(SmartFridgeApp.TEXT_SECONDARY);
            statusDot.setForeground(SmartFridgeApp.TEXT_SECONDARY);
            onlineToggle.setText("Online");
        }
        listModel.clear();
        detailArea.setText(htmlWrap("Provider switched. Click &ldquo;Suggest Recipes&rdquo; to search."));
    }

    // -------------------------------------------------------------------------
    // Split area
    // -------------------------------------------------------------------------

    private JSplitPane buildSplitArea() {
        listModel  = new DefaultListModel<>();
        recipeList = new JList<>(listModel);
        recipeList.setBackground(SmartFridgeApp.BG_DARK);
        recipeList.setFixedCellHeight(72);
        recipeList.setCellRenderer(new RecipeCardRenderer());
        recipeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showDetails();
        });

        JScrollPane listScroll = UiHelper.scrollPane(recipeList);
        listScroll.setBorder(BorderFactory.createLineBorder(SmartFridgeApp.BORDER, 1, true));

        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.add(UiHelper.sectionLabel("\uD83D\uDCD6  Recipes"), BorderLayout.NORTH);
        leftPanel.add(listScroll, BorderLayout.CENTER);

        JPanel rightPanel = buildDetailPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(300);
        split.setDividerSize(6);
        split.setBackground(SmartFridgeApp.BG_DARK);
        split.setBorder(null);
        return split;
    }

    private JPanel buildDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 12, 0, 0));

        detailArea = new JEditorPane("text/html", htmlWrap(
                "Click &ldquo;Suggest Recipes&rdquo; to find matching recipes "
                + "for your current fridge contents."));
        detailArea.setEditable(false);
        detailArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        detailArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailArea.setBackground(SmartFridgeApp.BG_CARD);
        detailArea.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        detailArea.setBorder(new EmptyBorder(16, 16, 16, 16));

        JScrollPane detailScroll = UiHelper.scrollPane(detailArea);
        detailScroll.setBorder(BorderFactory.createLineBorder(SmartFridgeApp.BORDER, 1, true));

        panel.add(UiHelper.sectionLabel("\uD83D\uDCC4  Details"), BorderLayout.NORTH);
        panel.add(detailScroll, BorderLayout.CENTER);
        return panel;
    }

    // -------------------------------------------------------------------------
    // Actions
    // -------------------------------------------------------------------------

    private void searchRecipes() {
        detailArea.setText(htmlWrap("Searching"
                + (onlineMode ? " (Spoonacular API)" : " (local database)") + "..."));
        listModel.clear();

        SwingWorker<List<Recipe>, Void> worker = new SwingWorker<>() {
            @Override protected List<Recipe> doInBackground() {
                return recipeService.suggestRecipes(fridgeManager);
            }
            @Override protected void done() {
                try {
                    currentRecipes = get();
                    for (Recipe r : currentRecipes) listModel.addElement(r);
                    detailArea.setText(htmlWrap(currentRecipes.isEmpty()
                            ? "No matching recipes found. Try adding more products to your fridge."
                            : "Select a recipe on the left to see details."));
                } catch (Exception ex) {
                    detailArea.setText(htmlWrap("&#10060; Error while loading recipes: " + ex.getMessage()));
                }
            }
        };
        worker.execute();
    }

    private void showDetails() {
        int idx = recipeList.getSelectedIndex();
        if (idx < 0 || currentRecipes == null || idx >= currentRecipes.size()) return;
        Recipe r = currentRecipes.get(idx);

        String accent  = colorHex(SmartFridgeApp.ACCENT);
        String warn    = colorHex(SmartFridgeApp.ACCENT_WARN);
        String muted   = colorHex(SmartFridgeApp.TEXT_SECONDARY);
        String primary = colorHex(SmartFridgeApp.TEXT_PRIMARY);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Segoe UI,sans-serif;font-size:13px;padding:4px;color:")
          .append(primary).append("'>")
          .append("<p><span style='font-size:16px'>\uD83C\uDF73</span> <b style='font-size:15px;color:")
          .append(accent).append("'>").append(esc(r.getName())).append("</b></p>")
          .append("<p style='color:").append(muted).append("'>&#9200; ").append(esc(r.getPreparationTime()));
        if (r.getSource() != null && !r.getSource().isBlank())
            sb.append("&nbsp;&nbsp;&#128279; ").append(esc(r.getSource()));
        sb.append("</p>");

        if (!r.getDescription().isBlank())
            sb.append("<p><b>Instructions:</b><br>").append(esc(r.getDescription())).append("</p>");

        sb.append("<p><b>Ingredients:</b></p><ul style='margin:4px 0 0 16px;padding:0'>")
          .append("<style>li{margin-bottom:3px}</style>");
        for (String ingredient : r.getIngredients()) {
            boolean have = fridgeManager.containsIngredient(ingredient);
            String icon  = have ? "&#9989;" : "&#128722;";
            String hint  = have ? "" : " <span style='color:" + muted + "'>(not in fridge)</span>";
            sb.append("<li>").append(icon).append(" ").append(esc(ingredient)).append(hint).append("</li>");
        }
        sb.append("</ul></body></html>");

        detailArea.setText(sb.toString());
        detailArea.setCaretPosition(0);
    }

    private void addMissingToShoppingList() {
        int idx = recipeList.getSelectedIndex();
        if (idx < 0 || currentRecipes == null || idx >= currentRecipes.size()) {
            JOptionPane.showMessageDialog(null,
                    "Please select a recipe first.",
                    "No recipe selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Recipe r = currentRecipes.get(idx);
        List<String> missing = recipeService.getMissingIngredients(r, fridgeManager);
        if (missing.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "\u2705 You already have all ingredients for this recipe!",
                    "Nothing missing", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (String ingredient : missing) {
            shoppingListService.addItem(
                    new ShoppingItem(ingredient, 1, "pcs", ProductCategory.OTHER));
        }
        if (shoppingView != null) shoppingView.refresh();
        JOptionPane.showMessageDialog(null,
                "\uD83D\uDED2  " + missing.size() + " ingredient(s) added to shopping list:"
                + "\n" + String.join(", ", missing),
                "Shopping list updated", JOptionPane.INFORMATION_MESSAGE);
    }

    // -------------------------------------------------------------------------
    // Custom cell renderer – recipe cards
    // -------------------------------------------------------------------------

    private class RecipeCardRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            Recipe r = (Recipe) value;
            JPanel card = new JPanel(new BorderLayout(8, 2));
            card.setOpaque(true);
            card.setBackground(isSelected ? SmartFridgeApp.BG_HOVER : SmartFridgeApp.BG_CARD);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, isSelected ? 3 : 0, 0, 0,
                            isSelected ? SmartFridgeApp.ACCENT : SmartFridgeApp.BG_CARD),
                    new EmptyBorder(10, 14, 10, 14)));

            // Name row: emoji + text in separate labels
            JPanel nameRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            nameRow.setOpaque(false);
            JLabel nameEmoji = new JLabel("\uD83C\uDF73");
            nameEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            nameEmoji.setForeground(isSelected ? SmartFridgeApp.ACCENT : SmartFridgeApp.TEXT_PRIMARY);
            JLabel nameLbl = new JLabel(r.getName());
            nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nameLbl.setForeground(isSelected ? SmartFridgeApp.ACCENT : SmartFridgeApp.TEXT_PRIMARY);
            nameRow.add(nameEmoji);
            nameRow.add(nameLbl);

            // Ingredient ratio
            long have  = r.getIngredients().stream().filter(fridgeManager::containsIngredient).count();
            int  total = r.getIngredients().size();
            String ratio = have + "/" + total + " ingredients";
            Color ratioColor = have == total ? SmartFridgeApp.ACCENT
                             : have > 0      ? SmartFridgeApp.ACCENT_WARN
                             : SmartFridgeApp.ACCENT_DANGER;

            // Meta row: emoji + text in separate labels
            JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
            metaRow.setOpaque(false);
            JLabel timeEmoji = new JLabel("\u23F1");
            timeEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
            timeEmoji.setForeground(ratioColor);
            JLabel timeText = new JLabel(" " + r.getPreparationTime() + "   ");
            timeText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            timeText.setForeground(ratioColor);
            JLabel cartEmoji = new JLabel("\uD83D\uDED2");
            cartEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
            cartEmoji.setForeground(ratioColor);
            JLabel ratioText = new JLabel(" " + ratio);
            ratioText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            ratioText.setForeground(ratioColor);
            metaRow.add(timeEmoji);
            metaRow.add(timeText);
            metaRow.add(cartEmoji);
            metaRow.add(ratioText);

            card.add(nameRow, BorderLayout.CENTER);
            card.add(metaRow, BorderLayout.SOUTH);
            return card;
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String htmlWrap(String body) {
        return "<html><body style='font-family:Segoe UI,sans-serif;font-size:13px;padding:4px'>" + body + "</body></html>";
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String colorHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}
