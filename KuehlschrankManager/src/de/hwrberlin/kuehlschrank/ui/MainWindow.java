package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.service.ShoppingListService;
import de.hwrberlin.kuehlschrank.service.FridgeManager;
import de.hwrberlin.kuehlschrank.service.RecipeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalTime;

/**
 * Main application shell with:
 *  • Left sidebar navigation (icon + label, gradient active highlight)
 *  • Top KPI status bar (live product/expiry/shopping counts)
 *  • CardLayout content area
 */
public class MainWindow {

    private final FridgeManager       fridgeManager;
    private final ShoppingListService shoppingListService;
    private final RecipeService       recipeService;

    // Navigation: {emoji icon, display name, accent colour hex}
    private static final Object[][] NAV = {
        {"\uD83D\uDCE6", "Contents",      SmartFridgeApp.ACCENT},
        {"\uD83D\uDED2", "Shopping List",  SmartFridgeApp.ACCENT_BLUE},
        {"\uD83D\uDCD6", "Recipes",        SmartFridgeApp.ACCENT_LIGHT},
        {"\uD83C\uDF73", "Chaos Pan",      SmartFridgeApp.ACCENT_WARN},
        {"\u26A0",        "Warnings",       SmartFridgeApp.ACCENT_DANGER},
    };

    // KPI label refs so we can refresh them
    private JLabel kpiProducts, kpiExpiring, kpiShopping;

    public MainWindow(FridgeManager fm, ShoppingListService sl, RecipeService rs) {
        this.fridgeManager       = fm;
        this.shoppingListService = sl;
        this.recipeService       = rs;
    }

    // -------------------------------------------------------------------------
    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(SmartFridgeApp.BG_DARK);

        // Content area
        JPanel content = new JPanel(new CardLayout());
        content.setBackground(SmartFridgeApp.BG_DARK);

        ShoppingListView shoppingView =
                new ShoppingListView(fridgeManager, shoppingListService);

        JPanel[] pages = {
            new ProductView(fridgeManager).createPanel(),
            shoppingView.createPanel(),
            new RecipeView(fridgeManager, recipeService,
                           shoppingListService, shoppingView).createPanel(),
            new ChaosPanView(fridgeManager, recipeService, shoppingView).createPanel(),
            new WarningsView(fridgeManager).createPanel()
        };
        for (int i = 0; i < NAV.length; i++)
            content.add(pages[i], (String) NAV[i][1]);

        CardLayout cl  = (CardLayout) content.getLayout();
        JPanel sidebar = buildSidebar(cl, content);

        // Top area: header banner + KPI bar
        JPanel topArea = new JPanel(new BorderLayout());
        topArea.setOpaque(false);
        topArea.add(buildHeader(),  BorderLayout.NORTH);
        topArea.add(buildKpiBar(),  BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(topArea,  BorderLayout.NORTH);
        center.add(content,  BorderLayout.CENTER);

        root.add(sidebar, BorderLayout.WEST);
        root.add(center,  BorderLayout.CENTER);
        return root;
    }

    // -------------------------------------------------------------------------
    // Header banner
    // -------------------------------------------------------------------------
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(0x1A, 0x2E, 0x22),
                    getWidth(), 0, SmartFridgeApp.BG_DARK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Greeting — split emoji + text to avoid □ blocks
        int    h = LocalTime.now().getHour();
        String greetIcon, greetText;
        if      (h <  6) { greetIcon = "\uD83C\uDF19"; greetText = "Good night"; }
        else if (h < 12) { greetIcon = "\u2600";        greetText = "Good morning"; }
        else if (h < 18) { greetIcon = "\uD83C\uDF05"; greetText = "Good afternoon"; }
        else if (h < 22) { greetIcon = "\uD83C\uDF06"; greetText = "Good evening"; }
        else             { greetIcon = "\uD83C\uDF19"; greetText = "Good night"; }

        JPanel greetRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        greetRow.setOpaque(false);
        JLabel greetEmoji = new JLabel(greetIcon);
        greetEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        greetEmoji.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        JLabel greetLbl = new JLabel("  " + greetText);
        greetLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        greetLbl.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        greetRow.add(greetEmoji);
        greetRow.add(greetLbl);

        JLabel sub = new JLabel("Your SmartFridge at a glance");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(SmartFridgeApp.TEXT_SECONDARY);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(greetRow);
        left.add(Box.createVerticalStrut(2));
        left.add(sub);

        // Mode chip — split emoji + text
        JPanel modeChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        modeChip.setOpaque(false);
        modeChip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(SmartFridgeApp.TEXT_SECONDARY.getRed(),
                          SmartFridgeApp.TEXT_SECONDARY.getGreen(),
                          SmartFridgeApp.TEXT_SECONDARY.getBlue(), 80), 1, true),
            new EmptyBorder(4, 10, 4, 10)));
        JLabel modeEmoji = new JLabel("\uD83D\uDFE1");
        modeEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 11));
        modeEmoji.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        JLabel modeText = new JLabel(" Offline mode");
        modeText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        modeText.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        modeChip.add(modeEmoji);
        modeChip.add(modeText);

        header.add(left,     BorderLayout.WEST);
        header.add(modeChip, BorderLayout.EAST);
        return header;
    }

    // -------------------------------------------------------------------------
    // KPI bar
    // -------------------------------------------------------------------------
    private JPanel buildKpiBar() {
        JPanel bar = new JPanel(new GridLayout(1, 3, 10, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 20, 14, 20));

        int products  = fridgeManager.getProductCount();
        int expiring  = fridgeManager.getExpiringSoon(5).size();
        int shopping  = shoppingListService.getItems().size();

        kpiProducts = new JLabel(String.valueOf(products));
        kpiExpiring = new JLabel(String.valueOf(expiring));
        kpiShopping = new JLabel(String.valueOf(shopping));

        bar.add(buildKpiCardLive("\uD83D\uDCE6", kpiProducts, "Items in fridge",
                SmartFridgeApp.ACCENT));
        bar.add(buildKpiCardLive("\u26A0",        kpiExpiring, "Expiring soon (5d)",
                expiring > 0 ? SmartFridgeApp.ACCENT_WARN : SmartFridgeApp.ACCENT));
        bar.add(buildKpiCardLive("\uD83D\uDED2", kpiShopping, "On shopping list",
                SmartFridgeApp.ACCENT_BLUE));
        return bar;
    }

    private JPanel buildKpiCardLive(String icon, JLabel valueLabel,
                                    String label, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SmartFridgeApp.BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                // Accent top strip
                g2.setColor(accent);
                g2.fillRect(0, 0, getWidth(), 3);
                g2.fillRoundRect(0, 0, getWidth(), 6, 14, 14);
                // Border
                g2.setColor(new Color(accent.getRed(),
                        accent.getGreen(), accent.getBlue(), 50));
                g2.setStroke(new java.awt.BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f,
                        getWidth()-1, getHeight()-1, 14, 14));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Icon with dedicated Emoji font
        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconLbl.setBorder(new EmptyBorder(0, 0, 0, 8));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accent);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(SmartFridgeApp.TEXT_SECONDARY);

        JPanel right = new JPanel(new BorderLayout(0, 2));
        right.setOpaque(false);
        right.add(valueLabel, BorderLayout.CENTER);
        right.add(lbl,        BorderLayout.SOUTH);

        card.add(iconLbl, BorderLayout.WEST);
        card.add(right,   BorderLayout.CENTER);
        return card;
    }

    // -------------------------------------------------------------------------
    // Sidebar
    // -------------------------------------------------------------------------
    private JPanel buildSidebar(CardLayout cl, JPanel content) {
        JPanel side = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(0x22, 0x24, 0x28),
                    0, getHeight(), new Color(0x1A, 0x1C, 0x1F));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(200, 0));
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, SmartFridgeApp.BORDER));

        // Logo: emoji + text separated
        JPanel logoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        logoRow.setOpaque(false);
        logoRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoRow.setBorder(new EmptyBorder(24, 20, 6, 16));
        JLabel logoEmoji = new JLabel("\uD83C\uDF73");
        logoEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
        logoEmoji.setForeground(SmartFridgeApp.ACCENT);
        JLabel logoText = new JLabel("  SmartFridge");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logoText.setForeground(SmartFridgeApp.ACCENT);
        logoRow.add(logoEmoji);
        logoRow.add(logoText);
        side.add(logoRow);

        JLabel tagline = new JLabel("Smart kitchen, less waste");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        tagline.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);
        tagline.setBorder(new EmptyBorder(0, 20, 16, 16));
        side.add(tagline);
        side.add(UiHelper.divider());
        side.add(Box.createVerticalStrut(10));

        // Nav buttons
        JButton[] btns = new JButton[NAV.length];
        for (int i = 0; i < NAV.length; i++) {
            final int idx      = i;
            final String icon  = (String) NAV[i][0];
            final String name  = (String) NAV[i][1];
            final Color accent = (Color)  NAV[i][2];
            btns[i] = buildNavBtn(icon, name, accent);
            btns[i].addActionListener(e -> {
                cl.show(content, name);
                for (int j = 0; j < btns.length; j++)
                    setNavActive(btns[j], j == idx,
                            (Color) NAV[j][2]);
            });
            side.add(btns[i]);
        }

        side.add(Box.createVerticalGlue());
        side.add(UiHelper.divider());

        JLabel version = new JLabel("v1.0  \u2022  HWR Berlin 2026");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        version.setBorder(new EmptyBorder(10, 20, 16, 16));
        side.add(version);

        // Activate first tab
        btns[0].doClick();
        return side;
    }

    /**
     * Builds a nav button with emoji and label as SEPARATE components
     * inside a JPanel, wrapped in a transparent JButton overlay.
     * This prevents □ blocks caused by mixing emoji codepoints
     * into a single JLabel with a non-emoji font.
     */
    private JButton buildNavBtn(String icon, String label, Color accent) {
        // Inner panel: emoji label + text label side by side
        JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        inner.setOpaque(false);

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        iconLbl.setForeground(SmartFridgeApp.TEXT_SECONDARY);

        JLabel textLbl = new JLabel(label);
        textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLbl.setForeground(SmartFridgeApp.TEXT_SECONDARY);

        inner.add(iconLbl);
        inner.add(textLbl);

        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Color bg = getBackground();
                if (bg != null && bg.getAlpha() > 10) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(
                        0, 0, new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 120),
                        getWidth(), 0, new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 0));
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Float(
                            6, 2, getWidth()-12, getHeight()-4, 10, 10));
                    g2.setColor(accent);
                    g2.fillRoundRect(4, 6, 3, getHeight()-12, 3, 3);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
            @Override public Dimension getPreferredSize() {
                return new Dimension(200, 44);
            }
        };
        btn.setLayout(new BorderLayout());
        btn.add(inner, BorderLayout.CENTER);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(new EmptyBorder(0, 14, 0, 12));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            boolean wasActive;
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                wasActive = textLbl.getForeground().equals(accent);
                if (!wasActive) {
                    iconLbl.setForeground(SmartFridgeApp.TEXT_PRIMARY);
                    textLbl.setForeground(SmartFridgeApp.TEXT_PRIMARY);
                }
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (!wasActive) {
                    iconLbl.setForeground(SmartFridgeApp.TEXT_SECONDARY);
                    textLbl.setForeground(SmartFridgeApp.TEXT_SECONDARY);
                }
            }
        });

        // Store refs for setNavActive
        btn.putClientProperty("iconLbl", iconLbl);
        btn.putClientProperty("textLbl", textLbl);
        return btn;
    }

    private void setNavActive(JButton btn, boolean active, Color accent) {
        JLabel iconLbl = (JLabel) btn.getClientProperty("iconLbl");
        JLabel textLbl = (JLabel) btn.getClientProperty("textLbl");
        if (active) {
            if (iconLbl != null) { iconLbl.setForeground(accent); iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13)); }
            if (textLbl != null) { textLbl.setForeground(accent); textLbl.setFont(new Font("Segoe UI", Font.BOLD, 13)); }
            btn.setBackground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
        } else {
            if (iconLbl != null) { iconLbl.setForeground(SmartFridgeApp.TEXT_SECONDARY); iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13)); }
            if (textLbl != null) { textLbl.setForeground(SmartFridgeApp.TEXT_SECONDARY); textLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13)); }
            btn.setBackground(new Color(0, 0, 0, 0));
        }
        btn.repaint();
    }

    public boolean saveData() {
        try {
            fridgeManager.save();
            shoppingListService.save();
            return true;
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "The data could not be saved:\n" + e.getMessage(),
                    "Save error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }
}
