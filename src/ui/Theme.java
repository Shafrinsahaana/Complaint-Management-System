package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Theme {

    public static final Color ACCENT = Color.decode("#1F4E79");
    public static final Color PAGE_BACKGROUND = Color.decode("#F6F7F9");
    public static final Color SURFACE = Color.WHITE;
    public static final Color BORDER = Color.decode("#DADDE3");
    public static final Color TEXT_BODY = Color.decode("#1F2933");
    public static final Color TEXT_MUTED = Color.decode("#667085");
    public static final Color VALIDATION_RED = new Color(220, 38, 38);

    public static final Color STATUS_PENDING = Color.decode("#B45309"); // Muted amber
    public static final Color STATUS_IN_PROGRESS = Color.decode("#1D4ED8"); // Muted blue
    public static final Color STATUS_RESOLVED = Color.decode("#15803D"); // Muted green

    // Badge pill backgrounds
    public static final Color STATUS_PENDING_BG = new Color(254, 243, 199);
    public static final Color STATUS_IN_PROGRESS_BG = new Color(219, 234, 254);
    public static final Color STATUS_RESOLVED_BG = new Color(220, 252, 231);

    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_TITLE_SECTION = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_TITLE_PAGE = new Font("Segoe UI", Font.BOLD, 22);

    // Spacing
    public static final int SPACING_8 = 8;
    public static final int SPACING_16 = 16;
    public static final int SPACING_24 = 24;
    public static final int SPACING_32 = 32;

    private static Image appIcon;

    public static void setupGlobal() {
        UIManager.put("Button.arc", 6);
        UIManager.put("Component.arc", 6);
        UIManager.put("TextComponent.arc", 6);
        UIManager.put("TabbedPane.selectedBackground", SURFACE);
        UIManager.put("TabbedPane.focusColor", ACCENT);
    }

    /** Creates a 32x32 rounded-square icon in the accent colour with "C" in white. */
    public static Image getAppIcon() {
        if (appIcon == null) {
            BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(ACCENT);
            g2.fillRoundRect(0, 0, 32, 32, 8, 8);
            g2.setColor(SURFACE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
            FontMetrics fm = g2.getFontMetrics();
            int x = (32 - fm.stringWidth("C")) / 2;
            int y = (32 - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString("C", x, y);
            g2.dispose();
            appIcon = img;
        }
        return appIcon;
    }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_INPUT.deriveFont(Font.BOLD));
        button.setBackground(ACCENT);
        button.setForeground(SURFACE);
        button.setFocusPainted(true);
        button.setBorder(BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(FONT_INPUT.deriveFont(Font.BOLD));
        button.setBackground(SURFACE);
        button.setForeground(TEXT_BODY);
        button.setFocusPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(SPACING_8, SPACING_16, SPACING_8, SPACING_16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static void styledTextField(JTextField field) {
        field.setFont(FONT_INPUT);
        field.setForeground(TEXT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(SPACING_8, SPACING_8, SPACING_8, SPACING_8)
        ));
    }

    public static void styledTable(JTable table) {
        table.setFont(FONT_INPUT);
        table.setForeground(TEXT_BODY);
        table.setRowHeight(36);
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(PAGE_BACKGROUND);
        table.setSelectionForeground(TEXT_BODY);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_INPUT.deriveFont(Font.BOLD));
        header.setBackground(SURFACE);
        header.setForeground(TEXT_MUTED);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        // Default cell renderer: wrap text + use Theme font
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return c;
            }
        };
        defaultRenderer.setVerticalAlignment(SwingConstants.TOP);
        table.setDefaultRenderer(Object.class, defaultRenderer);
    }

    /** Wrapping cell renderer for long-text columns like Description. */
    public static void setWrappingRenderer(JTable table, int columnIndex) {
        table.getColumnModel().getColumn(columnIndex).setCellRenderer(new javax.swing.table.TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JTextArea area = new JTextArea(value != null ? value.toString() : "");
                area.setFont(FONT_INPUT);
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
                area.setBorder(new EmptyBorder(4, 8, 4, 8));
                if (isSelected) {
                    area.setBackground(t.getSelectionBackground());
                    area.setForeground(t.getSelectionForeground());
                } else {
                    area.setBackground(t.getBackground());
                    area.setForeground(t.getForeground());
                }
                // Adjust row height if needed
                int width = t.getColumnModel().getColumn(column).getWidth();
                area.setSize(width, Short.MAX_VALUE);
                int prefHeight = area.getPreferredSize().height + 8;
                if (prefHeight > t.getRowHeight(row) && prefHeight > 36) {
                    t.setRowHeight(row, prefHeight);
                }
                return area;
            }
        });
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE_SECTION);
        label.setForeground(TEXT_BODY);
        return label;
    }

    public static JPanel card(JPanel panel) {
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(SPACING_16, SPACING_16, SPACING_16, SPACING_16)
        ));
        return panel;
    }

    public static JLabel statusBadge(String status) {
        JLabel badge = new JLabel(status, SwingConstants.CENTER);
        badge.setFont(FONT_BODY.deriveFont(Font.BOLD));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(4, 8, 4, 8));

        if ("Pending".equalsIgnoreCase(status)) {
            badge.setBackground(STATUS_PENDING_BG);
            badge.setForeground(STATUS_PENDING);
        } else if ("In Progress".equalsIgnoreCase(status)) {
            badge.setBackground(STATUS_IN_PROGRESS_BG);
            badge.setForeground(STATUS_IN_PROGRESS);
        } else if ("Resolved".equalsIgnoreCase(status)) {
            badge.setBackground(STATUS_RESOLVED_BG);
            badge.setForeground(STATUS_RESOLVED);
        } else {
            badge.setBackground(PAGE_BACKGROUND);
            badge.setForeground(TEXT_MUTED);
        }
        return badge;
    }

    public static JLabel validationLabel() {
        JLabel label = new JLabel(" "); // space so it takes up height from the start
        label.setFont(FONT_BODY);
        label.setForeground(VALIDATION_RED);
        return label;
    }

    /** Shows a "Loading..." message in a table by adding a single row. */
    public static void setTableLoading(javax.swing.table.DefaultTableModel model) {
        model.setRowCount(0);
        Object[] row = new Object[model.getColumnCount()];
        row[0] = "Loading...";
        model.addRow(row);
    }

    /** Shows an empty state message in a table. */
    public static void setTableEmpty(javax.swing.table.DefaultTableModel model, String message) {
        model.setRowCount(0);
        Object[] row = new Object[model.getColumnCount()];
        row[0] = message;
        model.addRow(row);
    }

    public static JPanel topBar(String userName, java.awt.event.ActionListener logoutAction) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SURFACE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(SPACING_16, SPACING_24, SPACING_16, SPACING_24)
        ));

        JLabel appName = new JLabel("CivicDesk");
        appName.setFont(FONT_TITLE_SECTION);
        appName.setForeground(ACCENT);
        bar.add(appName, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, SPACING_16, 0));
        rightPanel.setBackground(SURFACE);

        if (userName != null && !userName.isEmpty()) {
            JLabel userLabel = new JLabel(userName);
            userLabel.setFont(FONT_BODY);
            userLabel.setForeground(TEXT_BODY);
            rightPanel.add(userLabel);
        }

        JButton logoutButton = secondaryButton("Logout");
        logoutButton.addActionListener(logoutAction);
        rightPanel.add(logoutButton);

        bar.add(rightPanel, BorderLayout.EAST);
        return bar;
    }

}
