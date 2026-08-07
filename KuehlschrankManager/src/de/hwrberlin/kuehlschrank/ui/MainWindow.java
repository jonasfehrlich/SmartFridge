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
        {"\u26A0\uFE0F",  "Warnings",       SmartFridgeApp.ACCENT_DANGER},
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
                // Subtle gradient banner
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

        // Greeting
        int    h      = LocalTime.now().getHour();
        String greet;
        String greetIcon;
        if (h < 6)       { greet = "Good night";      greetIcon = "\uD83C\uDF19"; }
        else if (h < 12) { greet = "Good morning";    greetIcon = "\u2600\uFE0F"; }
        else if (h < 18) { greet = "Good afternoon";  greetIcon = "\uD83C\uDF05"; }
        else if (h < 22) { greet = "Good evening";    greetIcon = "\uD83C\uDF06"; }
        else             { greet = "Good night";      greetIcon = "\uD83C\uDF19"; }
        JLabel title  = new JLabel(greetIcon + "  " + greet);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(SmartFridgeApp.TEXT_PRIMARY);

        JLabel sub = new JLabel("Your SmartFridge at a glance");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(SmartFridgeApp.TEXT_SECONDARY);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(title);
        left.add(Box.createVerticalStrut(2));
        left.add(sub);

        // API status chip (top right)
        JLabel api = new JLabel("\uD83D\uDFE2  Online  •  TheMealDB");
        api.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        api.setForeground(SmartFridgeApp.ACCENT);
        api.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(SmartFridgeApp.ACCENT.getRed(),
                          SmartFridgeApp.ACCENT.getGreen(),
                          SmartFridgeApp.ACCENT.getBlue(), 80), 1, true),
            new EmptyBorder(4, 10, 4, 10)));

        header.add(left, BorderLayout.WEST);
        header.add(api,  BorderLayout.EAST);
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
        bar.add(buildKpiCardLive("\u26A0\uFE0F",  kpiExpiring, "Expiring soon (5d)",
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
                g2.fillRoundRect(0, 0, getWidth(), 6, 14, 14); // round top corners
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

        // Logo
        JLabel logo = new JLabel("\uD83C\uDF73  SmartFridge");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logo.setForeground(SmartFridgeApp.ACCENT);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(new EmptyBorder(24, 20, 6, 16));
        side.add(logo);

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

        JLabel version = new JLabel("v1.0  •  HWR Berlin 2026");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        version.setBorder(new EmptyBorder(10, 20, 16, 16));
        side.add(version);

        // Activate first tab
        btns[0].doClick();
        return side;
    }

    private JButton buildNavBtn(String icon, String label, Color accent) {
        JButton btn = new JButton(icon + "   " + label) {
            @Override protected void paintComponent(Graphics g) {
                // Active highlight painted by setNavActive via background
                Color bg = getBackground();
                if (bg != null && bg.getAlpha() > 10) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        RenderingHints.VALUE_ANTIALIAS_ON);
                    // Gradient highlight
                    GradientPaint gp = new GradientPaint(
                        0, 0, new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 120),
                        getWidth(), 0, new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 0));
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Float(
                            6, 2, getWidth()-12, getHeight()-4, 10, 10));
                    // Left accent bar
                    g2.setColor(accent);
                    g2.fillRoundRect(4, 6, 3, getHeight()-12, 3, 3);
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        btn.setBackground(new Color(0, 0, 0, 0));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(new EmptyBorder(11, 20, 11, 12));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            boolean wasActive;
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                wasActive = btn.getForeground().equals(accent);
                if (!wasActive) btn.setForeground(SmartFridgeApp.TEXT_PRIMARY);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (!wasActive) btn.setForeground(SmartFridgeApp.TEXT_SECONDARY);
            }
        });
        return btn;
    }

    private void setNavActive(JButton btn, boolean active, Color accent) {
        if (active) {
            btn.setForeground(accent);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setBackground(new Color(
                    accent.getRed(), accent.getGreen(), accent.getBlue(), 60));
        } else {
            btn.setForeground(SmartFridgeApp.TEXT_SECONDARY);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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
