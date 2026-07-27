package de.hwrberlin.kuehlschrank.ui;

import de.hwrberlin.kuehlschrank.model.ShoppingItem;
import de.hwrberlin.kuehlschrank.model.ProductCategory;
import de.hwrberlin.kuehlschrank.service.ShoppingListService;
import de.hwrberlin.kuehlschrank.service.FridgeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ShoppingListView {
    private final FridgeManager fridgeManager;
    private final ShoppingListService service;
    private DefaultListModel<String> listModel;
    private JList<String> list;

    public ShoppingListView(FridgeManager fm, ShoppingListService s) {
        this.fridgeManager = fm;
        this.service = s;
    }

    public JPanel createPanel() {
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setBackground(SmartFridgeApp.BG_DARK);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel heading = UiHelper.sectionLabel("\uD83D\uDED2  Shopping List");
        root.add(heading, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        list  = new JList<>(listModel);
        list.setBackground(SmartFridgeApp.BG_CARD);
        list.setForeground(SmartFridgeApp.TEXT_PRIMARY);
        list.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        list.setFixedCellHeight(42);
        list.setSelectionBackground(new Color(99, 179, 122, 50));
        list.setSelectionForeground(SmartFridgeApp.ACCENT);
        list.setCellRenderer(new ShoppingListRenderer());
        list.setBorder(null);

        JScrollPane scroll = UiHelper.scrollPane(list);
        scroll.setBorder(BorderFactory.createLineBorder(SmartFridgeApp.BORDER, 1, true));
        root.add(scroll, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnRow.setOpaque(false);

        JButton genBtn     = UiHelper.accentButton("\uD83D\uDD04  Regenerate list");
        JButton buyBtn     = UiHelper.ghostButton("\u2713  Mark as purchased");
        JButton cleanBtn   = UiHelper.ghostButton("\uD83E\uDDF9  Remove purchased");
        JButton addBtn     = UiHelper.ghostButton("+ Add manually");

        genBtn.addActionListener(e -> { service.generateList(fridgeManager); refresh(); });
        buyBtn.addActionListener(e -> {
            String sel = list.getSelectedValue();
            if (sel != null) {
                service.markAsPurchased(
                        sel.replace("[ ] ", "").replace("[x] ", "").split("  ")[0]);
                refresh();
            }
        });
        cleanBtn.addActionListener(e -> { service.removePurchased(); refresh(); });
        addBtn.addActionListener(e -> {
            String n = JOptionPane.showInputDialog(null, "Name:");
            if (n != null && !n.isBlank()) {
                String m   = JOptionPane.showInputDialog(null, "Quantity:", "1");
                String u   = JOptionPane.showInputDialog(null, "Unit:", "pcs");
                double qty = 1;
                try { qty = Double.parseDouble(m); } catch (Exception ignored) {}
                service.addItem(new ShoppingItem(
                        n.trim(), qty,
                        u == null ? "pcs" : u.trim(),
                        ProductCategory.OTHER));
                refresh();
            }
        });

        btnRow.add(genBtn);
        btnRow.add(buyBtn);
        btnRow.add(cleanBtn);
        btnRow.add(addBtn);
        root.add(btnRow, BorderLayout.SOUTH);

        service.generateList(fridgeManager);
        refresh();
        return root;
    }

    /** Refreshes the list UI from the current service state. */
    public void refresh() {
        if (listModel == null) return;
        listModel.clear();
        for (ShoppingItem item : service.getItems())
            listModel.addElement(item.toString());
    }

    private static class ShoppingListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            String text = value == null ? "" : value.toString();
            boolean done = text.startsWith("[x]");
            lbl.setBackground(isSelected
                    ? new Color(99, 179, 122, 50)
                    : (index % 2 == 0 ? SmartFridgeApp.BG_CARD : SmartFridgeApp.BG_HOVER));
            lbl.setForeground(done ? SmartFridgeApp.TEXT_SECONDARY : SmartFridgeApp.TEXT_PRIMARY);
            lbl.setFont(done
                    ? new Font("Segoe UI", Font.ITALIC, 13)
                    : new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setBorder(new EmptyBorder(0, 16, 0, 16));
            lbl.setOpaque(true);
            return lbl;
        }
    }
}
