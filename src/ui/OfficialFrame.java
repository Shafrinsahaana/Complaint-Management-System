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

public class OfficialFrame extends JFrame {

    private JTable complaintTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField searchField;
    private JComboBox<String> statusFilterDropdown;
    private JComboBox<String> categoryFilterDropdown;

    // Detail Panel Components
    private JLabel detailIdLabel;
    private JTextArea detailDescArea;
    private JComboBox<String> editStatusDropdown;
    private JTextArea editRemarksArea;
    private JButton saveButton;

    private int selectedComplaintId = -1;

    public OfficialFrame() {
        setTitle("CivicDesk - Official Workspace");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
        setIconImage(Theme.getAppIcon());
        getContentPane().setBackground(Theme.PAGE_BACKGROUND);

        createUI();
        refreshComplaints();
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.PAGE_BACKGROUND);

        // Top Bar — built manually so we can add the Dashboard button
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

        JLabel userLabel = new JLabel(Session.getOfficial().getName());
        userLabel.setFont(Theme.FONT_BODY);
        userLabel.setForeground(Theme.TEXT_BODY);
        rightPanel.add(userLabel);

        JButton dashboardButton = Theme.secondaryButton("Dashboard");
        dashboardButton.addActionListener(e -> {
            dispose();
            new DashboardFrame().setVisible(true);
        });
        rightPanel.add(dashboardButton);

        JButton logoutButton = Theme.secondaryButton("Logout");
        logoutButton.addActionListener(e -> {
            Session.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });
        rightPanel.add(logoutButton);

        topBar.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // Content area with padding
        JPanel content = new JPanel(new BorderLayout(Theme.SPACING_16, 0));
        content.setBackground(Theme.PAGE_BACKGROUND);
        content.setBorder(new EmptyBorder(Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24));

        // Center split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createTablePanel(), createDetailPanel());
        splitPane.setDividerLocation(700);
        splitPane.setBorder(null);
        splitPane.setBackground(Theme.PAGE_BACKGROUND);
        content.add(splitPane, BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, Theme.SPACING_16));
        Theme.card(panel);

        // Filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Theme.SPACING_8, 0));
        filterPanel.setBackground(Theme.SURFACE);

        JLabel searchLabel = new JLabel("Search");
        searchLabel.setFont(Theme.FONT_BODY);
        searchLabel.setForeground(Theme.TEXT_MUTED);

        searchField = new JTextField(15);
        Theme.styledTextField(searchField);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
        });

        statusFilterDropdown = new JComboBox<>(new String[]{"All", "Pending", "In Progress", "Resolved"});
        statusFilterDropdown.setFont(Theme.FONT_INPUT);
        statusFilterDropdown.setBackground(Theme.SURFACE);
        statusFilterDropdown.addActionListener(e -> applyFilters());

        categoryFilterDropdown = new JComboBox<>(new String[]{"All", "Sanitation", "Electricity", "Roads", "Water Supply", "Other"});
        categoryFilterDropdown.setFont(Theme.FONT_INPUT);
        categoryFilterDropdown.setBackground(Theme.SURFACE);
        categoryFilterDropdown.addActionListener(e -> applyFilters());

        JButton refreshButton = Theme.secondaryButton("Refresh");
        refreshButton.addActionListener(e -> refreshComplaints());

        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(statusFilterDropdown);
        filterPanel.add(categoryFilterDropdown);
        filterPanel.add(refreshButton);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Category", "Description", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        complaintTable = new JTable(tableModel);
        Theme.styledTable(complaintTable);
        complaintTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        complaintTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateDetailPanel();
            }
        });

        // Wrapping renderer for the Description column
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

        // Column widths
        complaintTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        complaintTable.getColumnModel().getColumn(0).setMaxWidth(70);
        complaintTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        complaintTable.getColumnModel().getColumn(2).setPreferredWidth(300);
        complaintTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        complaintTable.getColumnModel().getColumn(4).setPreferredWidth(110);

        sorter = new TableRowSorter<>(tableModel);
        complaintTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(complaintTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scrollPane.getViewport().setBackground(Theme.SURFACE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        Theme.card(panel);

        JLabel title = Theme.sectionTitle("Complaint Details");
        title.setBorder(new EmptyBorder(0, 0, Theme.SPACING_16, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Theme.SURFACE);

        detailIdLabel = new JLabel("Select a complaint...");
        detailIdLabel.setFont(Theme.FONT_INPUT.deriveFont(Font.BOLD));
        detailIdLabel.setForeground(Theme.TEXT_MUTED);
        detailIdLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(detailIdLabel);
        form.add(Box.createVerticalStrut(Theme.SPACING_16));

        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(Theme.FONT_INPUT.deriveFont(Font.BOLD));
        descLabel.setForeground(Theme.TEXT_BODY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(descLabel);
        form.add(Box.createVerticalStrut(Theme.SPACING_8));

        detailDescArea = new JTextArea(4, 20);
        detailDescArea.setFont(Theme.FONT_INPUT);
        detailDescArea.setLineWrap(true);
        detailDescArea.setWrapStyleWord(true);
        detailDescArea.setEditable(false);
        detailDescArea.setBackground(Theme.PAGE_BACKGROUND);
        detailDescArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(Theme.SPACING_8, Theme.SPACING_8, Theme.SPACING_8, Theme.SPACING_8)
        ));
        JScrollPane descScroll = new JScrollPane(detailDescArea);
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(descScroll);
        form.add(Box.createVerticalStrut(Theme.SPACING_16));

        JLabel statusLabel = new JLabel("Update Status");
        statusLabel.setFont(Theme.FONT_INPUT.deriveFont(Font.BOLD));
        statusLabel.setForeground(Theme.TEXT_BODY);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(statusLabel);
        form.add(Box.createVerticalStrut(Theme.SPACING_8));

        editStatusDropdown = new JComboBox<>(new String[]{"Pending", "In Progress", "Resolved"});
        editStatusDropdown.setFont(Theme.FONT_INPUT);
        editStatusDropdown.setBackground(Theme.SURFACE);
        editStatusDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        editStatusDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
        editStatusDropdown.setEnabled(false);
        form.add(editStatusDropdown);
        form.add(Box.createVerticalStrut(Theme.SPACING_16));

        JLabel remarksLabel = new JLabel("Official Remarks");
        remarksLabel.setFont(Theme.FONT_INPUT.deriveFont(Font.BOLD));
        remarksLabel.setForeground(Theme.TEXT_BODY);
        remarksLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(remarksLabel);
        form.add(Box.createVerticalStrut(Theme.SPACING_8));

        editRemarksArea = new JTextArea(4, 20);
        editRemarksArea.setFont(Theme.FONT_INPUT);
        editRemarksArea.setLineWrap(true);
        editRemarksArea.setWrapStyleWord(true);
        editRemarksArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(Theme.SPACING_8, Theme.SPACING_8, Theme.SPACING_8, Theme.SPACING_8)
        ));
        editRemarksArea.setEnabled(false);
        JScrollPane remarksScroll = new JScrollPane(editRemarksArea);
        remarksScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(remarksScroll);
        form.add(Box.createVerticalStrut(Theme.SPACING_16));

        saveButton = Theme.primaryButton("Save Updates");
        saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> saveComplaintUpdates());
        form.add(saveButton);

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private void applyFilters() {
        RowFilter<DefaultTableModel, Object> rf = new RowFilter<>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String search = searchField.getText().toLowerCase();
                String status = (String) statusFilterDropdown.getSelectedItem();
                String category = (String) categoryFilterDropdown.getSelectedItem();

                boolean matchesSearch = false;
                for (int i = 0; i < entry.getValueCount(); i++) {
                    if (entry.getStringValue(i).toLowerCase().contains(search)) {
                        matchesSearch = true;
                        break;
                    }
                }

                String rowCategory = entry.getStringValue(1);
                String rowStatus = entry.getStringValue(4);

                boolean matchesCategory = "All".equals(category) || rowCategory.equals(category);
                boolean matchesStatus = "All".equals(status) || rowStatus.equals(status);

                return matchesSearch && matchesCategory && matchesStatus;
            }
        };
        sorter.setRowFilter(rf);
    }

    private void populateDetailPanel() {
        int selectedRow = complaintTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = complaintTable.convertRowIndexToModel(selectedRow);
            selectedComplaintId = (int) tableModel.getValueAt(modelRow, 0);

            detailIdLabel.setText("Complaint #" + selectedComplaintId);
            detailIdLabel.setForeground(Theme.TEXT_BODY);
            detailDescArea.setText((String) tableModel.getValueAt(modelRow, 2));
            detailDescArea.setCaretPosition(0);

            String status = (String) tableModel.getValueAt(modelRow, 4);
            editStatusDropdown.setSelectedItem(status);

            editRemarksArea.setText("");

            editStatusDropdown.setEnabled(true);
            editRemarksArea.setEnabled(true);
            saveButton.setEnabled(true);
        } else {
            selectedComplaintId = -1;
            detailIdLabel.setText("Select a complaint...");
            detailIdLabel.setForeground(Theme.TEXT_MUTED);
            detailDescArea.setText("");
            editStatusDropdown.setEnabled(false);
            editRemarksArea.setText("");
            editRemarksArea.setEnabled(false);
            saveButton.setEnabled(false);
        }
    }

    private void refreshComplaints() {
        Theme.setTableLoading(tableModel);

        SwingWorker<List<Complaint>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Complaint> doInBackground() throws Exception {
                ComplaintDAO dao = new ComplaintDAO();
                return dao.getAllComplaints();
            }

            @Override
            protected void done() {
                try {
                    List<Complaint> complaints = get();
                    tableModel.setRowCount(0);
                    if (complaints.isEmpty()) {
                        Theme.setTableEmpty(tableModel, "No complaints found.");
                        populateDetailPanel();
                        return;
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                    for (Complaint c : complaints) {
                        tableModel.addRow(new Object[]{
                                c.getComplaintId(),
                                c.getCategory(),
                                c.getDescription(),
                                c.getComplaintDate() != null ? sdf.format(c.getComplaintDate()) : "",
                                c.getStatus()
                        });
                    }
                    populateDetailPanel(); // clear details
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(OfficialFrame.this,
                            "Error fetching complaints: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void saveComplaintUpdates() {
        if (selectedComplaintId == -1) return;

        String status = (String) editStatusDropdown.getSelectedItem();
        String remarks = editRemarksArea.getText().trim();
        int officialId = Session.getOfficial().getOfficialId();

        saveButton.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                ComplaintDAO dao = new ComplaintDAO();
                dao.updateComplaintStatus(selectedComplaintId, officialId, status, remarks);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(OfficialFrame.this,
                            "Complaint updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshComplaints();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(OfficialFrame.this,
                            "Error saving updates: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    saveButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
