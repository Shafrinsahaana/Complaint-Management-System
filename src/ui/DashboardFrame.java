package ui;

import dao.ComplaintDAO;
import model.Complaint;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class DashboardFrame extends JFrame {

    private JTable complaintTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField searchField;
    private JComboBox<String> statusDropdown;

    private JLabel totalCount;
    private JLabel pendingCount;
    private JLabel progressCount;
    private JLabel resolvedCount;

    private StatusChartPanel chartPanel;

    public DashboardFrame() {
        setTitle("CivicDesk - Analytics Dashboard");
        setSize(1150, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 650));
        setIconImage(Theme.getAppIcon());
        getContentPane().setBackground(Theme.PAGE_BACKGROUND);

        createDashboard();
        loadDashboardData();
    }

    private void createDashboard() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.PAGE_BACKGROUND);

        // Top Bar — built manually so we can add Back button
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Theme.SURFACE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                new EmptyBorder(Theme.SPACING_16, Theme.SPACING_24, Theme.SPACING_16, Theme.SPACING_24)
        ));

        JLabel appName = new JLabel("CivicDesk");
        appName.setFont(Theme.FONT_TITLE_SECTION);
        appName.setForeground(Theme.ACCENT);
        topBar.add(appName, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Theme.SPACING_16, 0));
        rightPanel.setBackground(Theme.SURFACE);

        String userName = Session.getOfficial() != null ? Session.getOfficial().getName() : "";
        if (!userName.isEmpty()) {
            JLabel userLabel = new JLabel(userName);
            userLabel.setFont(Theme.FONT_BODY);
            userLabel.setForeground(Theme.TEXT_BODY);
            rightPanel.add(userLabel);
        }

        JButton backButton = Theme.secondaryButton("Back to Workspace");
        backButton.addActionListener(e -> {
            dispose();
            new OfficialFrame().setVisible(true);
        });
        rightPanel.add(backButton);

        JButton logoutButton = Theme.secondaryButton("Logout");
        logoutButton.addActionListener(e -> {
            Session.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });
        rightPanel.add(logoutButton);

        topBar.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new BorderLayout(0, Theme.SPACING_16));
        content.setBackground(Theme.PAGE_BACKGROUND);
        content.setBorder(new EmptyBorder(Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24));

        // Top metrics area (cards + chart)
        JPanel topMetricsPanel = new JPanel(new BorderLayout(0, Theme.SPACING_16));
        topMetricsPanel.setBackground(Theme.PAGE_BACKGROUND);
        topMetricsPanel.add(createCountCards(), BorderLayout.NORTH);

        chartPanel = new StatusChartPanel();
        chartPanel.setPreferredSize(new Dimension(0, 180));
        topMetricsPanel.add(chartPanel, BorderLayout.CENTER);

        content.add(topMetricsPanel, BorderLayout.NORTH);
        content.add(createTableSection(), BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createCountCards() {
        JPanel panel = new JPanel(new GridLayout(1, 4, Theme.SPACING_16, 0));
        panel.setBackground(Theme.PAGE_BACKGROUND);

        totalCount = new JLabel("--");
        pendingCount = new JLabel("--");
        progressCount = new JLabel("--");
        resolvedCount = new JLabel("--");

        panel.add(createCard("Total Complaints", totalCount));
        panel.add(createCard("Pending", pendingCount));
        panel.add(createCard("In Progress", progressCount));
        panel.add(createCard("Resolved", resolvedCount));

        return panel;
    }

    private JPanel createCard(String title, JLabel count) {
        JPanel card = new JPanel(new BorderLayout(0, Theme.SPACING_8));
        card.setBackground(Theme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(Theme.SPACING_16, Theme.SPACING_16, Theme.SPACING_16, Theme.SPACING_16)
        ));

        count.setFont(Theme.FONT_TITLE_PAGE.deriveFont(Font.BOLD, 30f));
        count.setForeground(Theme.TEXT_BODY);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_BODY);
        titleLabel.setForeground(Theme.TEXT_MUTED);

        card.add(count, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createTableSection() {
        JPanel section = new JPanel(new BorderLayout(0, Theme.SPACING_16));
        Theme.card(section);

        // FILTER BAR
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 0));
        filterPanel.setBackground(Theme.SURFACE);

        JLabel searchLabel = new JLabel("Search");
        searchLabel.setFont(Theme.FONT_BODY);
        searchLabel.setForeground(Theme.TEXT_MUTED);

        searchField = new JTextField(20);
        Theme.styledTextField(searchField);

        JLabel statusLabel = new JLabel("Status");
        statusLabel.setFont(Theme.FONT_BODY);
        statusLabel.setForeground(Theme.TEXT_MUTED);

        statusDropdown = new JComboBox<>(new String[]{"All", "Pending", "In Progress", "Resolved"});
        statusDropdown.setFont(Theme.FONT_INPUT);
        statusDropdown.setBackground(Theme.SURFACE);

        JButton refreshButton = Theme.secondaryButton("Refresh");

        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(Box.createHorizontalStrut(Theme.SPACING_8));
        filterPanel.add(statusLabel);
        filterPanel.add(statusDropdown);
        filterPanel.add(Box.createHorizontalStrut(Theme.SPACING_8));
        filterPanel.add(refreshButton);

        section.add(filterPanel, BorderLayout.NORTH);

        // TABLE
        String[] columns = {"ID", "Category", "Description", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        complaintTable = new JTable(tableModel);
        Theme.styledTable(complaintTable);

        // Wrapping renderer for Description
        Theme.setWrappingRenderer(complaintTable, 2);

        // Status badge renderer
        complaintTable.getColumnModel().getColumn(4).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value == null) return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
                p.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                p.add(Theme.statusBadge(value.toString()));
                return p;
            }
        });

        complaintTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        complaintTable.getColumnModel().getColumn(0).setMaxWidth(80);
        complaintTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        complaintTable.getColumnModel().getColumn(2).setPreferredWidth(400);
        complaintTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        complaintTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        sorter = new TableRowSorter<>(tableModel);
        complaintTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(complaintTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scrollPane.getViewport().setBackground(Theme.SURFACE);
        section.add(scrollPane, BorderLayout.CENTER);

        // EVENTS
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
        });

        statusDropdown.addActionListener(e -> applyFilters());

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            statusDropdown.setSelectedItem("All");
            loadDashboardData();
        });

        return section;
    }

    private void applyFilters() {
        RowFilter<DefaultTableModel, Object> rf = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String search = searchField.getText().toLowerCase();
                String statusFilter = (String) statusDropdown.getSelectedItem();

                boolean matchesSearch = false;
                for (int i = 0; i < entry.getValueCount(); i++) {
                    if (entry.getStringValue(i).toLowerCase().contains(search)) {
                        matchesSearch = true;
                        break;
                    }
                }

                String rowStatus = entry.getStringValue(4);
                boolean matchesStatus = "All".equals(statusFilter) || rowStatus.equals(statusFilter);

                return matchesSearch && matchesStatus;
            }
        };
        sorter.setRowFilter(rf);
    }

    private void loadDashboardData() {
        Theme.setTableLoading(tableModel);

        SwingWorker<DashboardData, Void> worker = new SwingWorker<>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                ComplaintDAO dao = new ComplaintDAO();
                int total = dao.countByStatus(null);
                int pending = dao.countByStatus("Pending");
                int inProgress = dao.countByStatus("In Progress");
                int resolved = dao.countByStatus("Resolved");
                List<Complaint> complaints = dao.getAllComplaints();
                return new DashboardData(total, pending, inProgress, resolved, complaints);
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    totalCount.setText(String.valueOf(data.total));
                    pendingCount.setText(String.valueOf(data.pending));
                    progressCount.setText(String.valueOf(data.inProgress));
                    resolvedCount.setText(String.valueOf(data.resolved));

                    chartPanel.updateData(data.pending, data.inProgress, data.resolved);

                    tableModel.setRowCount(0);
                    if (data.complaints.isEmpty()) {
                        Theme.setTableEmpty(tableModel, "No complaints found.");
                        return;
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                    for (Complaint c : data.complaints) {
                        tableModel.addRow(new Object[]{
                                c.getComplaintId(),
                                c.getCategory(),
                                c.getDescription(),
                                c.getComplaintDate() != null ? sdf.format(c.getComplaintDate()) : "",
                                c.getStatus()
                        });
                    }
                    applyFilters();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(DashboardFrame.this,
                            "Error loading dashboard data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private static class DashboardData {
        int total, pending, inProgress, resolved;
        List<Complaint> complaints;
        DashboardData(int t, int p, int i, int r, List<Complaint> c) {
            total = t; pending = p; inProgress = i; resolved = r; complaints = c;
        }
    }

    private class StatusChartPanel extends JPanel {
        private int pending = 0, inProgress = 0, resolved = 0;

        public StatusChartPanel() {
            setBackground(Theme.SURFACE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(Theme.SPACING_8, Theme.SPACING_8, Theme.SPACING_8, Theme.SPACING_8)
            ));
        }

        public void updateData(int p, int ip, int r) {
            this.pending = p;
            this.inProgress = ip;
            this.resolved = r;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int total = pending + inProgress + resolved;
            if (total == 0) {
                g2.setColor(Theme.TEXT_MUTED);
                g2.setFont(Theme.FONT_BODY);
                String msg = "No data available";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
                return;
            }

            int minDimension = Math.min(getWidth(), getHeight()) - 40;
            int x = (getWidth() - minDimension) / 2;
            int y = (getHeight() - minDimension) / 2;

            int startAngle = 90;
            int arcAngle;

            // Pending (Amber)
            arcAngle = (int) Math.round((double) pending / total * 360);
            g2.setColor(Theme.STATUS_PENDING);
            g2.fillArc(x, y, minDimension, minDimension, startAngle, -arcAngle);
            startAngle -= arcAngle;

            // In Progress (Blue)
            arcAngle = (int) Math.round((double) inProgress / total * 360);
            g2.setColor(Theme.STATUS_IN_PROGRESS);
            g2.fillArc(x, y, minDimension, minDimension, startAngle, -arcAngle);
            startAngle -= arcAngle;

            // Resolved (Green)
            arcAngle = 360 - (90 - startAngle); // remainder
            g2.setColor(Theme.STATUS_RESOLVED);
            g2.fillArc(x, y, minDimension, minDimension, startAngle, -arcAngle);

            // Legend
            g2.setFont(Theme.FONT_BODY.deriveFont(Font.BOLD));
            int legendX = getWidth() - 150;
            int legendY = 20;

            g2.setColor(Theme.STATUS_PENDING);
            g2.fillRect(legendX, legendY, 12, 12);
            g2.setColor(Theme.TEXT_BODY);
            g2.drawString("Pending", legendX + 20, legendY + 11);

            g2.setColor(Theme.STATUS_IN_PROGRESS);
            g2.fillRect(legendX, legendY + 22, 12, 12);
            g2.setColor(Theme.TEXT_BODY);
            g2.drawString("In Progress", legendX + 20, legendY + 33);

            g2.setColor(Theme.STATUS_RESOLVED);
            g2.fillRect(legendX, legendY + 44, 12, 12);
            g2.setColor(Theme.TEXT_BODY);
            g2.drawString("Resolved", legendX + 20, legendY + 55);
        }
    }
}