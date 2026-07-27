package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.service.ShoppingListService;
import de.hwrberlin.kuehlschrank.service.FridgeManager;
import de.hwrberlin.kuehlschrank.service.RecipeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main application shell.
 * Uses a left-side navigation panel (icon + label) instead of the default
 * JTabbedPane to give a modern sidebar feel with FlatLaf.
 */
public class MainWindow {

    private final FridgeManager       fridgeManager;
    private final ShoppingListService shoppingListService;
    private final RecipeService       recipeService;

    // Navigation entries: {icon emoji, display name}
    private static final String[][] NAV = {
        {"\uD83D\uDCE6", "Contents"},
        {"\uD83D\uDED2", "Shopping List"},
        {"\uD83D\uDCD6", "Recipes"},
        {"\uD83C\uDF73", "Chaos Pan"},
        {"\u26A0\uFE0F",  "Warnings"}
    };

    public MainWindow(FridgeManager fm, ShoppingListService sl, RecipeService rs) {
        this.fridgeManager       = fm;
        this.shoppingListService = sl;
        this.recipeService       = rs;
    }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(SmartFridgeApp.BG_DARK);

        // ── Sidebar ────────────────────────────────────────────────────────────
        JPanel sidebar = buildSidebar();

        // ── Content area (CardLayout) ──────────────────────────────────────────
        JPanel content = new JPanel(new CardLayout());
        content.setBackground(SmartFridgeApp.BG_DARK);

        ShoppingListView shoppingView = new ShoppingListView(fridgeManager, shoppingListService);

        JPanel[] pages = {
            new ProductView(fridgeManager).createPanel(),
            shoppingView.createPanel(),
            new RecipeView(fridgeManager, recipeService, shoppingListService, shoppingView).createPanel(),
            new ChaosPanView(fridgeManager, recipeService, shoppingView).createPanel(),
            new WarningsView(fridgeManager).createPanel()
        };
        for (int i = 0; i < NAV.length; i++) {
            content.add(pages[i], NAV[i][1]);
        }

        // ── Nav buttons wired to CardLayout ───────────────────────────────────
        CardLayout cl = (CardLayout) content.getLayout();
        Component[] navBtns = sidebar.getComponents();
        int btnIdx = 0;
        for (Component c : navBtns) {
            if (c instanceof JButton btn) {
                final String pageName = NAV[btnIdx][1];
                final int    idx      = btnIdx;
                btn.addActionListener(e -> {
                    cl.show(content, pageName);
                    // highlight active
                    int b2 = 0;
                    for (Component c2 : sidebar.getComponents()) {
                        if (c2 instanceof JButton b) {
                            boolean active = b2 == idx;
                            b.setBackground(active
                                    ? new Color(SmartFridgeApp.ACCENT.getRed(),
                                                SmartFridgeApp.ACCENT.getGreen(),
                                                SmartFridgeApp.ACCENT.getBlue(), 40)
                                    : SmartFridgeApp.BG_DARK);
                            b.setForeground(active
                                    ? SmartFridgeApp.ACCENT
                                    : SmartFridgeApp.TEXT_SECONDARY);
                        }
                        b2++;
                    }
                });
                btnIdx++;
            }
        }
        // Activate first page
        if (navBtns.length > 0 && navBtns[0] instanceof JButton first) {
            first.doClick();
        }

        root.add(sidebar, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);
        return root;
    }

    // ── Sidebar builder ────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(SmartFridgeApp.BG_CARD);
        side.setPreferredSize(new Dimension(190, 0));
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, SmartFridgeApp.BORDER));

        // Logo / app title
        JLabel logo = new JLabel("\uD83C\uDF73  SmartFridge");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(SmartFridgeApp.ACCENT);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(new EmptyBorder(22, 20, 18, 16));
        side.add(logo);

        JSeparator sep = new JSeparator();
        sep.setForeground(SmartFridgeApp.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        side.add(sep);
        side.add(Box.createVerticalStrut(8));

        // Nav buttons
        for (String[] entry : NAV) {
            JButton btn = buildNavButton(entry[0], entry[1]);
            side.add(btn);
        }

        side.add(Box.createVerticalGlue());

        // Version label at bottom
        JLabel version = new JLabel("v1.0  •  HWR Berlin");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        version.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        version.setBorder(new EmptyBorder(12, 20, 16, 12));
        side.add(version);

        return side;
    }

    private JButton buildNavButton(String icon, String label) {
        JButton btn = new JButton(icon + "   " + label) {
            @Override protected void paintComponent(Graphics g) {
                if (getBackground().getAlpha() > 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(6, 2, getWidth() - 12, getHeight() - 4, 10, 10);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        btn.setBackground(SmartFridgeApp.BG_DARK);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(new EmptyBorder(10, 20, 10, 12));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn.getForeground() != SmartFridgeApp.ACCENT)
                    btn.setForeground(SmartFridgeApp.TEXT_PRIMARY);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn.getForeground() != SmartFridgeApp.ACCENT)
                    btn.setForeground(SmartFridgeApp.TEXT_SECONDARY);
            }
        });
        return btn;
    }
}
