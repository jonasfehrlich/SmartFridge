package de.hwrberlin.kuehlschrank.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;

import de.hwrberlin.kuehlschrank.service.ShoppingListService;
import de.hwrberlin.kuehlschrank.service.FridgeManager;
import de.hwrberlin.kuehlschrank.service.RecipeService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Entry point of the SmartFridge application.
 * Uses FlatLaf Dark theme for a modern, professional look.
 * To use FlatLaf, add flatlaf-3.4.jar to your project libraries:
 *   IntelliJ: File → Project Structure → Libraries → + → flatlaf-3.4.jar
 */
public class SmartFridgeApp {

    public static FridgeManager       fridgeManager;
    public static ShoppingListService shoppingListService;
    public static RecipeService       recipeService;

    // ── Colour palette (FlatLaf dark, green accent) ──────────────────────────
    public static final Color BG_DARK        = new Color(0x1E, 0x1F, 0x22);   // FlatLaf editor bg
    public static final Color BG_CARD        = new Color(0x2B, 0x2D, 0x30);   // FlatLaf tool window
    public static final Color BG_HOVER       = new Color(0x35, 0x38, 0x3D);   // hover surface
    public static final Color ACCENT         = new Color(0x4C, 0xAF, 0x7A);   // green – primary
    public static final Color ACCENT_LIGHT   = new Color(0x6F, 0xC9, 0x97);   // green – light
    public static final Color ACCENT_WARN    = new Color(0xFF, 0xB7, 0x4D);   // amber
    public static final Color ACCENT_DANGER  = new Color(0xE5, 0x5C, 0x5C);   // red
    public static final Color ACCENT_BLUE    = new Color(0x6A, 0xA8, 0xE8);   // blue
    public static final Color TEXT_PRIMARY   = new Color(0xD4, 0xD4, 0xD4);   // main text
    public static final Color TEXT_SECONDARY = new Color(0x8A, 0x8A, 0x8A);   // muted text
    public static final Color BORDER         = new Color(0x43, 0x45, 0x4A);   // subtle border

    // ── FlatLaf property tweaks ───────────────────────────────────────────────
    public static void applyGlobalStyle() {
        // ── FlatLaf dark theme ────────────────────────────────────────────────
        FlatDarkLaf.setup();

        // Override key FlatLaf colours to match our green accent
        UIManager.put("Component.focusColor",           ACCENT);
        UIManager.put("Component.focusedBorderColor",   ACCENT);
        UIManager.put("Button.default.background",      ACCENT);
        UIManager.put("Button.default.foreground",      Color.WHITE);
        UIManager.put("Button.default.hoverBackground", ACCENT_LIGHT);
        UIManager.put("CheckBox.icon.selectedColor",    ACCENT);
        UIManager.put("ToggleButton.selectedBackground",new Color(0x4C, 0xAF, 0x7A, 80));
        UIManager.put("ProgressBar.foreground",         ACCENT);
        UIManager.put("Slider.thumbColor",              ACCENT);
        UIManager.put("TabbedPane.underlineColor",      ACCENT);
        UIManager.put("TabbedPane.hoverColor",          BG_HOVER);
        UIManager.put("TabbedPane.focusColor",          BG_HOVER);
        UIManager.put("List.selectionBackground",       new Color(0x4C, 0xAF, 0x7A, 50));
        UIManager.put("Table.selectionBackground",      new Color(0x4C, 0xAF, 0x7A, 50));
        UIManager.put("TextField.caretForeground",      ACCENT);
        UIManager.put("TextArea.caretForeground",       ACCENT);

        // Larger, rounder default border-radius for buttons / inputs
        UIManager.put("Button.arc",          999);   // pill buttons
        UIManager.put("Component.arc",        8);
        UIManager.put("TextComponent.arc",    8);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.thumbArc",    999);
        UIManager.put("ScrollBar.width",        8);

        // Fonts – use a crisp, modern sans-serif
        Font baseFont = new Font("Segoe UI", Font.PLAIN, 13);
        UIManager.put("defaultFont", baseFont);
    }

    // ── Application entry point ────────────────────────────────────────────────
    public static void main(String[] args) {
        // Must be called BEFORE any Swing component is created
        applyGlobalStyle();

        SwingUtilities.invokeLater(() -> {
            fridgeManager       = FridgeManager.load();
            shoppingListService = ShoppingListService.load();

            // Online mode: fetch recipes from the Spoonacular API.
            // Falls back to local recipes automatically if the network is unavailable.
            recipeService = new RecipeService(true);

            if (fridgeManager.getProductCount() == 0) {
                SampleDataLoader.load(fridgeManager);
            }

            JFrame frame = new JFrame("SmartFridge 🍳");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // App icon (green fridge emoji rendered as icon)
            frame.setIconImage(createAppIcon());

            MainWindow mainWindow = new MainWindow(
                    fridgeManager,
                    shoppingListService,
                    recipeService);

            frame.setContentPane(mainWindow.createPanel());
            frame.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {

                    boolean savedSuccessfully = mainWindow.saveData();

                    if (savedSuccessfully) {
                        frame.dispose();
                    }
                }
            });
            frame.setSize(1200, 820);
            frame.setMinimumSize(new Dimension(960, 640));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /** Creates a simple coloured square as the window icon (no image file needed). */
    private static Image createAppIcon() {
        int size = 64;
        java.awt.image.BufferedImage img =
                new java.awt.image.BufferedImage(size, size,
                        java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Background circle
        g.setColor(ACCENT);
        g.fillRoundRect(0, 0, size, size, 18, 18);
        // Fridge rectangle
        g.setColor(Color.WHITE);
        g.fillRoundRect(14, 10, 36, 46, 8, 8);
        // Freezer divider
        g.setColor(ACCENT_LIGHT);
        g.fillRect(14, 26, 36, 3);
        // Handle
        g.setColor(new Color(0x2B, 0x2D, 0x30));
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(28, 14, 28, 22);
        g.drawLine(28, 30, 28, 38);
        g.dispose();
        return img;
    }
}
