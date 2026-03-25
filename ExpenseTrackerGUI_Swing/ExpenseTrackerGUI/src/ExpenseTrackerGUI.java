import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ExpenseTrackerGUI extends JFrame {

    // ── Colors ──────────────────────────────────────────────
    private static final Color BG_DARK    = new Color(18, 18, 35);
    private static final Color BG_CARD    = new Color(30, 30, 55);
    private static final Color BG_FIELD   = new Color(40, 40, 70);
    private static final Color ACCENT     = new Color(99, 102, 241);
    private static final Color ACCENT2    = new Color(139, 92, 246);
    private static final Color SUCCESS    = new Color(34, 197, 94);
    private static final Color DANGER     = new Color(239, 68, 68);
    private static final Color WARNING    = new Color(251, 191, 36);
    private static final Color TEXT_WHITE = new Color(240, 240, 255);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);
    private static final Color TABLE_HDR  = new Color(49, 46, 129);
    private static final Color TABLE_ROW1 = new Color(30, 30, 55);
    private static final Color TABLE_ROW2 = new Color(36, 36, 65);

    // ── Data ────────────────────────────────────────────────
    private ArrayList<Expense> expenses = new ArrayList<>();
    private DefaultTableModel  tableModel;

    // ── Input Fields ────────────────────────────────────────
    private JTextField  nameField, amountField, dateField, searchField;
    private JComboBox<String> categoryBox;

    // ── Labels ──────────────────────────────────────────────
    private JLabel totalLabel, countLabel, statusLabel;

    // ── Table ───────────────────────────────────────────────
    private JTable table;

    // ─────────────────────────────────────────────────────────
    public ExpenseTrackerGUI() {
        setTitle("Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 680);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildLeftPanel(), BorderLayout.WEST);
        add(buildMainPanel(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        // Pre-load sample data
        loadSampleData();
        refreshTable();
        updateSummary();
    }

    // ══════════════════════════════════════════════════════════
    //  TOP BAR
    // ══════════════════════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD);
        bar.setBorder(new EmptyBorder(14, 24, 14, 24));

        // Title
        JLabel title = new JLabel("  Expense Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);

       

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setOpaque(false);
        left.add(title);
      

        // Summary pills
        totalLabel = makePill("Total: Rs. 0.00", ACCENT);
        countLabel = makePill("0 Expenses", ACCENT2);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(countLabel);
        right.add(totalLabel);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        // Bottom separator
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT),
            new EmptyBorder(14, 24, 12, 24)
        ));
        return bar;
    }

    private JLabel makePill(String text, Color color) {
        JLabel lbl = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(false);
        lbl.setBorder(new EmptyBorder(6, 16, 6, 16));
        lbl.setPreferredSize(new Dimension(170, 32));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    // ══════════════════════════════════════════════════════════
    //  LEFT PANEL — Input Form
    // ══════════════════════════════════════════════════════════
    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setPreferredSize(new Dimension(270, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 18, 20, 18));

        panel.add(sectionLabel("ADD / EDIT EXPENSE"));
        panel.add(Box.createVerticalStrut(10));

        // Name
        panel.add(fieldLabel("Expense Name"));
        nameField = styledField("e.g. Lunch, Books...");
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));

        // Amount
        panel.add(fieldLabel("Amount (Rs.)"));
        amountField = styledField("e.g. 150.00");
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(10));

        // Category
        panel.add(fieldLabel("Category"));
        String[] cats = {"Food","Travel","Education","Health","Shopping","Entertainment","Other"};
        categoryBox = new JComboBox<>(cats);
        styleCombo(categoryBox);
        panel.add(categoryBox);
        panel.add(Box.createVerticalStrut(10));

        // Date
        panel.add(fieldLabel("Date"));
        dateField = styledField("DD/MM/YYYY");
        panel.add(dateField);
        panel.add(Box.createVerticalStrut(18));

        // Buttons
        JButton addBtn = styledButton("+ Add Expense", SUCCESS);
        JButton clrBtn = styledButton("Clear Fields", BG_FIELD);
        addBtn.addActionListener(e -> addExpense());
        clrBtn.addActionListener(e -> clearFields());
        panel.add(addBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(clrBtn);

        // Divider
        panel.add(Box.createVerticalStrut(20));
        panel.add(makeDivider());
        panel.add(Box.createVerticalStrut(16));

        // Action buttons
        panel.add(sectionLabel("ACTIONS"));
        panel.add(Box.createVerticalStrut(10));

        JButton delBtn  = styledButton("Delete Selected", DANGER);
        JButton editBtn = styledButton("Edit Selected",   WARNING);
        JButton totBtn  = styledButton("Show Total",      ACCENT);

        delBtn.addActionListener(e  -> deleteSelected());
        editBtn.addActionListener(e -> editSelected());
        totBtn.addActionListener(e  -> showTotal());

        panel.add(delBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(editBtn);
        panel.add(Box.createVerticalStrut(8));
        panel.add(totBtn);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // ══════════════════════════════════════════════════════════
    //  MAIN PANEL — Table + Search
    // ══════════════════════════════════════════════════════════
    private JPanel buildMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(16, 16, 16, 20));

        // Search bar
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);

        searchField = styledField("Search by name or category...");
        JButton searchBtn = styledButton("Search", ACCENT);
        JButton showAllBtn = styledButton("Show All", BG_FIELD);

        searchBtn.setPreferredSize(new Dimension(90, 36));
        showAllBtn.setPreferredSize(new Dimension(90, 36));

        searchBtn.addActionListener(e  -> searchExpenses());
        showAllBtn.addActionListener(e -> { searchField.setText(""); refreshTable(); });

        // Also search on Enter key
        searchField.addActionListener(e -> searchExpenses());

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(searchBtn);
        btnGroup.add(showAllBtn);

        searchBar.add(searchField, BorderLayout.CENTER);
        searchBar.add(btnGroup,    BorderLayout.EAST);

        // Table
        String[] cols = {"#", "Name", "Category", "Amount (Rs.)", "Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (isRowSelected(row)) {
                    c.setBackground(ACCENT);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? TABLE_ROW1 : TABLE_ROW2);
                    c.setForeground(TEXT_WHITE);
                }
                if (c instanceof JLabel) ((JLabel)c).setBorder(new EmptyBorder(6, 10, 6, 10));
                return c;
            }
        };

        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 2));
        table.setBackground(TABLE_ROW1);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.WHITE);
        table.setFocusable(false);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HDR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Center align columns 0, 3, 4
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBackground(BG_DARK);
        scroll.getViewport().setBackground(TABLE_ROW1);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 100), 1));

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(scroll,    BorderLayout.CENTER);
        return panel;
    }

    
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(20, 20, 40));
        bar.setBorder(new EmptyBorder(6, 20, 6, 20));

        statusLabel = new JLabel("Ready — Add your first expense!");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_MUTED);

      

        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    
    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(ACCENT);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(TEXT_MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField styledField(String placeholder) {
        JTextField field = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(new Color(100, 100, 140));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    g2.drawString(placeholder, 10, getHeight() / 2 + 5);
                }
            }
        };
        field.setBackground(BG_FIELD);
        field.setForeground(TEXT_WHITE);
        field.setCaretColor(TEXT_WHITE);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 110), 1),
            new EmptyBorder(7, 10, 7, 10)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setAlignmentX(LEFT_ALIGNMENT);
        return field;
    }

    private void styleCombo(JComboBox<String> box) {
        box.setBackground(BG_FIELD);
        box.setForeground(TEXT_WHITE);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        box.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 110), 1));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        box.setAlignmentX(LEFT_ALIGNMENT);
        ((JLabel)box.getRenderer()).setBackground(BG_FIELD);
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() :
                             getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setPreferredSize(new Dimension(220, 38));
        return btn;
    }

    private JSeparator makeDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 100));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        return sep;
    }

    // ══════════════════════════════════════════════════════════
    //  LOGIC METHODS
    // ══════════════════════════════════════════════════════════
    private void addExpense() {
        String name   = nameField.getText().trim();
        String amt    = amountField.getText().trim();
        String cat    = (String) categoryBox.getSelectedItem();
        String date   = dateField.getText().trim();

        if (name.isEmpty()) { setStatus("Enter expense name!", DANGER); return; }
        if (amt.isEmpty())  { setStatus("Enter amount!",       DANGER); return; }

        double amount;
        try {
            amount = Double.parseDouble(amt);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            setStatus("Enter a valid positive amount!", DANGER);
            return;
        }

        if (date.isEmpty()) date = "N/A";

        expenses.add(new Expense(name, amount, cat, date));
        refreshTable();
        updateSummary();
        clearFields();
        setStatus("Added: " + name + " — Rs." + String.format("%.2f", amount), SUCCESS);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { setStatus("Select a row to delete!", WARNING); return; }
        int idx = Integer.parseInt((String) tableModel.getValueAt(row, 0)) - 1;
        String name = expenses.get(idx).getName();

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete \"" + name + "\"?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            expenses.remove(idx);
            refreshTable();
            updateSummary();
            setStatus("Deleted: " + name, DANGER);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { setStatus("Select a row to edit!", WARNING); return; }
        int idx = Integer.parseInt((String) tableModel.getValueAt(row, 0)) - 1;
        Expense e = expenses.get(idx);

        nameField.setText(e.getName());
        amountField.setText(String.valueOf(e.getAmount()));
        categoryBox.setSelectedItem(e.getCategory());
        dateField.setText(e.getDate());

        // Remove old, re-add on next Add click
        expenses.remove(idx);
        refreshTable();
        updateSummary();
        setStatus("Editing: " + e.getName() + " — modify fields and click Add", WARNING);
        nameField.requestFocus();
    }

    private void showTotal() {
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        JOptionPane.showMessageDialog(this,
            String.format("<html><div style='font-size:16px;padding:10px;'>"
                + "Total Expenses: <b>Rs. %.2f</b><br>"
                + "Number of entries: <b>%d</b></div></html>", total, expenses.size()),
            "Total Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchExpenses() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) { refreshTable(); return; }
        tableModel.setRowCount(0);
        int num = 1;
        for (Expense e : expenses) {
            if (e.getName().toLowerCase().contains(keyword) ||
                e.getCategory().toLowerCase().contains(keyword)) {
                tableModel.addRow(new Object[]{
                    String.valueOf(num++),
                    e.getName(),
                    e.getCategory(),
                    String.format("%.2f", e.getAmount()),
                    e.getDate()
                });
            }
        }
        int found = tableModel.getRowCount();
        setStatus("Search \"" + keyword + "\": " + found + " result(s) found", found > 0 ? SUCCESS : WARNING);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (int i = 0; i < expenses.size(); i++) {
            Expense e = expenses.get(i);
            tableModel.addRow(new Object[]{
                String.valueOf(i + 1),
                e.getName(),
                e.getCategory(),
                String.format("%.2f", e.getAmount()),
                e.getDate()
            });
        }
    }

    private void updateSummary() {
        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        totalLabel.setText("Total: Rs. " + String.format("%.2f", total));
        countLabel.setText(expenses.size() + " Expense" + (expenses.size() == 1 ? "" : "s"));
    }

    private void clearFields() {
        nameField.setText("");
        amountField.setText("");
        dateField.setText("");
        categoryBox.setSelectedIndex(0);
        nameField.requestFocus();
    }

    private void setStatus(String msg, Color color) {
        statusLabel.setText(msg);
        statusLabel.setForeground(color);
    }

    private void loadSampleData() {
        expenses.add(new Expense("Lunch",          120.00, "Food",          "25/03/2026"));
        expenses.add(new Expense("Java Textbook",  450.00, "Education",     "24/03/2026"));
        expenses.add(new Expense("Bus Fare",        40.00, "Travel",        "25/03/2026"));
        expenses.add(new Expense("Medicine",       200.00, "Health",        "23/03/2026"));
        expenses.add(new Expense("Notebook",        80.00, "Shopping",      "22/03/2026"));
        expenses.add(new Expense("Movie Ticket",   250.00, "Entertainment", "21/03/2026"));
        expenses.add(new Expense("Dinner",         180.00, "Food",          "20/03/2026"));
    }

    // ══════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            ExpenseTrackerGUI app = new ExpenseTrackerGUI();
            app.setVisible(true);
        });
    }
}
