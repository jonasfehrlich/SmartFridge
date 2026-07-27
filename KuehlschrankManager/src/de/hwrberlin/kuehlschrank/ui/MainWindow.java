package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.service.ShoppingListService;
import de.hwrberlin.kuehlschrank.service.FridgeManager;
import de.hwrberlin.kuehlschrank.service.RecipeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow {
    private final FridgeManager fridgeManager;
    private final ShoppingListService shoppingListService;
    private final RecipeService recipeService;

    private static final String[] TAB_NAMES = {"Contents", "Shopping List", "Recipes", "Chaos Pan", "Warnings"};
    private static final String[] TAB_ICONS = {"\uD83D\uDCE6", "\uD83D\uDED2", "\uD83D\uDCD6", "\uD83C\uDF73", "\u26A0"};

    public MainWindow(FridgeManager fm, ShoppingListService sl, RecipeService rs) {
        this.fridgeManager = fm;
        this.shoppingListService = sl;
        this.recipeService = rs;
    }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(SmartFridgeApp.BG_DARK);

        root.add(buildHeader(), BorderLayout.NORTH);

        ShoppingListView shoppingView = new ShoppingListView(fridgeManager, shoppingListService);

        JTabbedPane tabs = buildTabbedPane();
        tabs.addTab(TAB_NAMES[0], new ProductView(fridgeManager).createPanel());
        tabs.addTab(TAB_NAMES[1], shoppingView.createPanel());
        tabs.addTab(TAB_NAMES[2],
                new RecipeView(fridgeManager, recipeService, shoppingListService, shoppingView).createPanel());
        tabs.addTab(TAB_NAMES[3],
                new ChaosPanView(fridgeManager, recipeService, shoppingView).createPanel());
        tabs.addTab(TAB_NAMES[4], new WarningsView(fridgeManager).createPanel());

        for (int i = 0; i < TAB_NAMES.length; i++) {
            JLabel lbl = new JLabel(TAB_ICONS[i] + "  " + TAB_NAMES[i]);
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(SmartFridgeApp.TEXT_SECONDARY);
            lbl.setBorder(new EmptyBorder(8, 14, 8, 14));
            tabs.setTabComponentAt(i, lbl);
        }
        tabs.addChangeListener(e -> {
            for (int i = 0; i < tabs.getTabCount(); i++) {
                Component c = tabs.getTabComponentAt(i);
                if (c instanceof JLabel lbl) {
                    if (i == tabs.getSelectedIndex()) {
                        lbl.setForeground(SmartFridgeApp.ACCENT);
                        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    } else {
                        lbl.setForeground(SmartFridgeApp.TEXT_SECONDARY);
                        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    }
                }
            }
        });
        if (tabs.getTabComponentAt(0) instanceof JLabel lbl) {
            lbl.setForeground(SmartFridgeApp.ACCENT);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        }

        root.add(tabs, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SmartFridgeApp.BG_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, SmartFridgeApp.BORDER),
            new EmptyBorder(14, 24, 14, 24)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel logo = new JLabel("\uD83D\uDC0B");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        JLabel title = new JLabel("SmartFridge");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Your intelligent fridge manager");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        titleBlock.add(title);
        titleBlock.add(subtitle);
        left.add(logo);
        left.add(titleBlock);
        header.add(left, BorderLayout.WEST);

        JLabel badge = new JLabel("v1.0  |  HWR Berlin");
        badge.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        badge.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        badge.setBorder(new EmptyBorder(0, 0, 0, 4));
        header.add(badge, BorderLayout.EAST);

        return header;
    }

    private JTabbedPane buildTabbedPane() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setBackground(SmartFridgeApp.BG_DARK);
        tabs.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, SmartFridgeApp.BORDER));
        UIManager.put("TabbedPane.tabInsets", new Insets(8, 16, 8, 16));
        return tabs;
    }
}
