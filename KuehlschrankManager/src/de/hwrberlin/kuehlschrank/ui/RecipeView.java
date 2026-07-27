package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.Recipe;
import de.hwrberlin.kuehlschrank.service.ShoppingListService;
import de.hwrberlin.kuehlschrank.service.FridgeManager;
import de.hwrberlin.kuehlschrank.service.RecipeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Tab "Recipes": suggests recipes and transfers missing ingredients
 * to the shopping list. Bug-fix: shopping list updates immediately.
 */
public class RecipeView {
    private final FridgeManager fridgeManager;
    private final RecipeService recipeService;
    private final ShoppingListService shoppingListService;
    private final ShoppingListView shoppingView;

    private DefaultListModel<String> listModel;
    private JList<String> list;
    private JTextArea details;
    private List<Recipe> currentRecipes;

    public RecipeView(FridgeManager fm, RecipeService rs,
                      ShoppingListService sl, ShoppingListView sv) {
        this.fridgeManager = fm;
        this.recipeService = rs;
        this.shoppingListService = sl;
        this.shoppingView = sv;
    }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(SmartFridgeApp.BG_DARK);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        listModel = new DefaultListModel<>();
        list  = new JList<>(listModel);
        list.setBackground(SmartFridgeApp.BG_CARD);
        list.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        list.setFixedCellHeight(40);
        list.setSelectionBackground(new Color(99, 179, 122, 50));
        list.setSelectionForeground(SmartFridgeApp.ACCENT);
        list.setCellRenderer(new RecipeListRenderer());
        list.addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) showDetails(); });

        JScrollPane listScroll = UiHelper.scrollPane(list);
        listScroll.setBorder(BorderFactory.createLineBorder(SmartFridgeApp.BORDER, 1, true));

        JPanel leftPanel = new JPanel(new BorderLayout(0, 8));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(270, 0));
        leftPanel.add(UiHelper.sectionLabel("\uD83D\uDCD6  Suggestions"), BorderLayout.NORTH);
        leftPanel.add(listScroll, BorderLayout.CENTER);

        details = new JTextArea();
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        details.setBackground(SmartFridgeApp.BG_CARD);
        details.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        details.setCaretColor(SmartFridgeApp.ACCENT);
        details.setBorder(new EmptyBorder(16, 16, 16, 16));
        details.setText("Click \"Suggest recipes\" to find matching recipes\nfor your fridge contents.");

        JScrollPane detailScroll = UiHelper.scrollPane(details);
        detailScroll.setBorder(BorderFactory.createLineBorder(SmartFridgeApp.BORDER, 1, true));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, detailScroll);
        split.setDividerLocation(270);
        split.setDividerSize(6);
        split.setBackground(SmartFridgeApp.BG_DARK);
        split.setBorder(null);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        JButton searchBtn = UiHelper.accentButton("\uD83D\uDD0D  Suggest recipes");
        JButton toListBtn = UiHelper.ghostButton("\uD83D\uDED2  Add missing ingredients to shopping list");
        searchBtn.addActionListener(e -> searchRecipes());
        toListBtn.addActionListener(e -> addMissingToShoppingList());
        btnRow.add(searchBtn);
        btnRow.add(toListBtn);

        root.add(btnRow, BorderLayout.NORTH);
        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private void searchRecipes() {
        currentRecipes = recipeService.suggestRecipes(fridgeManager);
        listModel.clear();
        for (Recipe r : currentRecipes)
            listModel.addElement(r.getName() + " (" + r.getPreparationTime() + ")");
        details.setText(currentRecipes.isEmpty()
                ? "No matching recipes found."
                : "Please select a recipe on the left.");
    }

    private void showDetails() {
        int idx = list.getSelectedIndex();
        if (idx < 0 || currentRecipes == null || idx >= currentRecipes.size()) return;
        Recipe r = currentRecipes.get(idx);
        StringBuilder sb = new StringBuilder();
        sb.append("Recipe: ").append(r.getName()).append("\n\n");
        sb.append("Description:\n").append(r.getDescription()).append("\n\n");
        sb.append("Ingredients:\n");
        for (String ingredient : r.getIngredients()) {
            boolean available = fridgeManager.findProduct(ingredient) != null;
            sb.append(available ? "  \u2713 " : "  \u2717 ").append(ingredient)
              .append(available ? "  (available)" : "  (missing)").append("\n");
        }
        details.setText(sb.toString());
        details.setCaretPosition(0);
    }

    /** Bug-fix: adds missing ingredients to the shopping list and refreshes the UI immediately. */
    private void addMissingToShoppingList() {
        int idx = list.getSelectedIndex();
        if (idx < 0 || currentRecipes == null || idx >= currentRecipes.size()) {
            JOptionPane.showMessageDialog(null, "Please select a recipe first.");
            return;
        }
        Recipe r = currentRecipes.get(idx);
        List<String> missing = recipeService.getMissingIngredients(r, fridgeManager);
        for (String ingredient : missing) {
            shoppingListService.addItem(
                new de.hwrberlin.kuehlschrank.model.ShoppingItem(
                    ingredient, 1, "pcs",
                    de.hwrberlin.kuehlschrank.model.ProductCategory.OTHER));
        }
        // Bug-fix: refresh shopping list view immediately
        if (shoppingView != null) shoppingView.refresh();
        JOptionPane.showMessageDialog(null,
                missing.size() + " ingredient(s) added to the shopping list.");
    }

    private static class RecipeListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            lbl.setBackground(isSelected
                    ? new Color(99, 179, 122, 50)
                    : (index % 2 == 0 ? SmartFridgeApp.BG_CARD : SmartFridgeApp.BG_HOVER));
            lbl.setForeground(isSelected ? SmartFridgeApp.ACCENT : SmartFridgeApp.TEXT_PRIMARY);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setBorder(new EmptyBorder(0, 16, 0, 16));
            lbl.setOpaque(true);
            return lbl;
        }
    }
}
