package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.FridgeProduct;
import de.hwrberlin.kuehlschrank.model.Product;
import de.hwrberlin.kuehlschrank.model.ProductCategory;
import de.hwrberlin.kuehlschrank.service.FridgeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;

public class ProductView {
    private final FridgeManager fridgeManager;
    private DefaultTableModel model;
    private JTable table;

    public ProductView(FridgeManager fm) { this.fridgeManager = fm; }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(SmartFridgeApp.BG_DARK);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        model = new DefaultTableModel(
                new Object[]{"\uD83D\uDCE6  Name", "\uD83C\uDFF7  Category", "\uD83D\uDD22  Quantity", "\uD83D\uDCC5  Expiry", "\u2139  Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        styleTable(table);
        refresh();

        JScrollPane tableScroll = UiHelper.scrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(SmartFridgeApp.BORDER, 1, true));

        JPanel form = buildForm();

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, form);
        split.setResizeWeight(0.65);
        split.setDividerSize(6);
        split.setBackground(SmartFridgeApp.BG_DARK);
        split.setBorder(null);

        root.add(split, BorderLayout.CENTER);
        return root;
    }

    private void styleTable(JTable t) {
        t.setBackground(SmartFridgeApp.BG_CARD);
        t.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        t.setSelectionBackground(new Color(99, 179, 122, 50));
        t.setSelectionForeground(SmartFridgeApp.ACCENT);
        t.setGridColor(SmartFridgeApp.BORDER);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(36);
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setFocusable(false);

        JTableHeader header = t.getTableHeader();
        header.setBackground(SmartFridgeApp.BG_DARK);
        header.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SmartFridgeApp.BORDER));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);

        t.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                String s = val == null ? "" : val.toString();
                String display = switch (s) {
                    case "EXPIRED"        -> "\u274C  EXPIRED";
                    case "Expiring soon"  -> "\u23F0  Expiring soon";
                    case "Restock needed" -> "\uD83D\uDED2  Restock needed";
                    default               -> "\u2705  OK";
                };
                setText(display);
                setBackground(sel ? new Color(99, 179, 122, 50) : SmartFridgeApp.BG_CARD);
                setForeground(switch (s) {
                    case "EXPIRED"        -> SmartFridgeApp.ACCENT_DANGER;
                    case "Expiring soon"  -> SmartFridgeApp.ACCENT_WARN;
                    case "Restock needed" -> SmartFridgeApp.ACCENT_BLUE;
                    default               -> SmartFridgeApp.ACCENT;
                });
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setOpaque(true);
                return this;
            }
        });
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                setBackground(sel ? new Color(99, 179, 122, 50) : SmartFridgeApp.BG_CARD);
                setForeground(SmartFridgeApp.TEXT_PRIMARY);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setOpaque(true);
                return this;
            }
        };
        for (int i = 0; i < 4; i++) t.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
    }

    private JPanel buildForm() {
        JPanel card = UiHelper.card();
        card.setLayout(new BorderLayout(12, 12));
        card.setBackground(SmartFridgeApp.BG_CARD);

        JLabel heading = UiHelper.sectionLabel("\uD83D\uDCE6  Add / Update Product");
        card.add(heading, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 4, 10, 10));
        grid.setOpaque(false);

        JTextField nameField    = UiHelper.textField("Name");
        JTextField quantityField = UiHelper.textField("Quantity");
        JTextField unitField    = UiHelper.textField("Unit");
        JTextField minField     = UiHelper.textField("Min. quantity");
        minField.setText("0");
        JTextField expiryField  = UiHelper.textField("Expiry date");
        expiryField.setText(LocalDate.now().plusDays(7).toString());
        JComboBox<ProductCategory> catBox = new JComboBox<>(ProductCategory.values());
        catBox.setBackground(SmartFridgeApp.BG_CARD);
        catBox.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        catBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        grid.add(styledLabel("\uD83D\uDCDD  Name"));                grid.add(nameField);
        grid.add(styledLabel("\uD83D\uDD22  Quantity"));             grid.add(quantityField);
        grid.add(styledLabel("\uD83D\uDCCF  Unit"));                grid.add(unitField);
        grid.add(styledLabel("\uD83C\uDFF7  Category"));            grid.add(catBox);
        grid.add(styledLabel("\u26A0  Min. Quantity"));             grid.add(minField);
        grid.add(styledLabel("\uD83D\uDCC5  Expiry (YYYY-MM-DD)")); grid.add(expiryField);

        card.add(grid, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);
        JButton addBtn = UiHelper.accentButton("\u2795  Add / Update");
        JButton delBtn = UiHelper.dangerButton("\uD83D\uDDD1  Delete selected");

        addBtn.addActionListener(e -> {
            try {
                double qty = Double.parseDouble(quantityField.getText().trim());
                double min = Double.parseDouble(minField.getText().trim());
                fridgeManager.addProduct(new Product(
                        nameField.getText().trim(),
                        (ProductCategory) catBox.getSelectedItem(),
                        qty, unitField.getText().trim(),
                        LocalDate.parse(expiryField.getText().trim()), min));
                refresh();
                nameField.setText(""); quantityField.setText(""); unitField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        delBtn.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                String n = (String) model.getValueAt(r, 0);
                fridgeManager.removeProduct(n);
                refresh();
            }
        });
        btnRow.add(addBtn);
        btnRow.add(delBtn);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(SmartFridgeApp.TEXT_SECONDARY);
        return l;
    }

    public void refresh() {
        if (model == null) return;
        model.setRowCount(0);
        for (Product p : fridgeManager.getAllProducts()) {
            String status = p.isExpired() ? "EXPIRED"
                    : (p.expiresSoon(3) ? "Expiring soon"
                    : (p.needsRestock() ? "Restock needed" : "OK"));
            model.addRow(new Object[]{
                    p.getName(), p.getCategory(),
                    p.getQuantity() + " " + p.getUnit(),
                    p.getExpiryDate(), status});
        }
    }
}
