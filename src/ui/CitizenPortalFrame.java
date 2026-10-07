package ui;

import dao.ComplaintDAO;
import model.Complaint;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class CitizenPortalFrame extends JFrame {

    private JLabel validationLabel;

    private JComboBox<String> categoryDropdown;
    private JTextArea descriptionArea;
    private JLabel charCountLabel;

    private JTable complaintsTable;
    private DefaultTableModel tableModel;

    public CitizenPortalFrame() {
        setTitle("CivicDesk - Citizen Portal");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));
        setIconImage(Theme.getAppIcon());
        getContentPane().setBackground(Theme.PAGE_BACKGROUND);

        createUI();
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.PAGE_BACKGROUND);

        JPanel headerPanel = Theme.topBar(Session.getCitizen().getName(), e -> {
            Session.logout();
            dispose();
            new LoginFrame().setVisible(true);
        });
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.FONT_INPUT);
        tabbedPane.setBackground(Theme.SURFACE);

        tabbedPane.addTab("Lodge Complaint", createLodgeComplaintTab());
        tabbedPane.addTab("My Complaints", createMyComplaintsTab());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createLodgeComplaintTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.PAGE_BACKGROUND);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        Theme.card(form);
        form.setPreferredSize(new Dimension(600, 500));

        JLabel heading = Theme.sectionTitle("Lodge a New Complaint");
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(heading);
        form.add(Box.createVerticalStrut(Theme.SPACING_24));

        JLabel catLabel = new JLabel("Category");
        catLabel.setFont(Theme.FONT_INPUT.deriveFont(Font.BOLD));
        catLabel.setForeground(Theme.TEXT_BODY);
        catLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(catLabel);
        form.add(Box.createVerticalStrut(Theme.SPACING_8));

        categoryDropdown = new JComboBox<>(new String[]{"Sanitation", "Electricity", "Roads", "Water Supply", "Other"});
        categoryDropdown.setFont(Theme.FONT_INPUT);
        categoryDropdown.setBackground(Theme.SURFACE);
        categoryDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        categoryDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(categoryDropdown);
        form.add(Box.createVerticalStrut(Theme.SPACING_16));

        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(Theme.FONT_INPUT.deriveFont(Font.BOLD));
        descLabel.setForeground(Theme.TEXT_BODY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(descLabel);
        form.add(Box.createVerticalStrut(Theme.SPACING_8));

        descriptionArea = new JTextArea(8, 40);
        descriptionArea.setFont(Theme.FONT_INPUT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(Theme.SPACING_8, Theme.SPACING_8, Theme.SPACING_8, Theme.SPACING_8)
        ));

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(scrollPane);

        charCountLabel = new JLabel("0 / 500");
        charCountLabel.setFont(Theme.FONT_BODY);
        charCountLabel.setForeground(Theme.TEXT_MUTED);
        charCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        descriptionArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCount(); }
            public void removeUpdate(DocumentEvent e) { updateCount(); }
            public void changedUpdate(DocumentEvent e) { updateCount(); }
            private void updateCount() {
                int length = descriptionArea.getText().length();
                charCountLabel.setText(length + " / 500");
                charCountLabel.setForeground(length > 500 ? Theme.VALIDATION_RED : Theme.TEXT_MUTED);
            }
        });

        form.add(Box.createVerticalStrut(Theme.SPACING_8));
        form.add(charCountLabel);
        form.add(Box.createVerticalStrut(Theme.SPACING_16));

        validationLabel = Theme.validationLabel();
        validationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(validationLabel);
        form.add(Box.createVerticalStrut(Theme.SPACING_8));

        JButton submitButton = Theme.primaryButton("Submit Complaint");
        submitButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        submitButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        submitButton.addActionListener(e -> submitComplaint());
        form.add(submitButton);

        panel.add(form);
        return panel;
    }

    private void submitComplaint() {
        validationLabel.setText(" ");
        String category = (String) categoryDropdown.getSelectedItem();
        String description = descriptionArea.getText().trim();

        if (description.isEmpty()) {
            validationLabel.setText("Description cannot be empty.");
            return;
        }
        if (description.length() > 500) {
            validationLabel.setText("Description must be under 500 characters.");
            return;
        }

        Complaint complaint = new Complaint();
        complaint.setCitizenId(Session.getCitizen().getCitizenId());
        complaint.setCategory(category);
        complaint.setDescription(description);

        // Submit via SwingWorker
        SwingWorker<Integer, Void> worker = new SwingWorker<>() {
            @Override
            protected Integer doInBackground() throws Exception {
                ComplaintDAO dao = new ComplaintDAO();
                return dao.insertComplaint(complaint);
            }

            @Override
            protected void done() {
                try {
                    int newId = get();
                    if (newId != -1) {
                        JOptionPane.showMessageDialog(CitizenPortalFrame.this,
                                "Complaint lodged successfully! ID: #" + newId,
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        descriptionArea.setText("");
                        categoryDropdown.setSelectedIndex(0);
                        refreshComplaints(); // Auto refresh the table
                    } else {
                        JOptionPane.showMessageDialog(CitizenPortalFrame.this,
                                "Failed to lodge complaint.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CitizenPortalFrame.this,
                            "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private JPanel createMyComplaintsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, Theme.SPACING_16));
        panel.setBackground(Theme.PAGE_BACKGROUND);
        panel.setBorder(new EmptyBorder(Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24, Theme.SPACING_24));

        // Top bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setBackground(Theme.PAGE_BACKGROUND);

        JButton refreshButton = Theme.secondaryButton("Refresh");
        refreshButton.addActionListener(e -> refreshComplaints());
        topBar.add(refreshButton);
        panel.add(topBar, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Category", "Date", "Status", "Official Remarks"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        complaintsTable = new JTable(tableModel);
        Theme.styledTable(complaintsTable);

        complaintsTable.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value == null) return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
                p.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                p.add(Theme.statusBadge(value.toString()));
                return p;
            }
        });

        complaintsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        complaintsTable.getColumnModel().getColumn(0).setMaxWidth(70);
        complaintsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        complaintsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        complaintsTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        complaintsTable.getColumnModel().getColumn(4).setPreferredWidth(300);

        JScrollPane tableScroll = new JScrollPane(complaintsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        tableScroll.getViewport().setBackground(Theme.SURFACE);
        panel.add(tableScroll, BorderLayout.CENTER);

        // Initial Load
        refreshComplaints();

        return panel;
    }

    private void refreshComplaints() {
        Theme.setTableLoading(tableModel);

        SwingWorker<List<Complaint>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Complaint> doInBackground() throws Exception {
                ComplaintDAO dao = new ComplaintDAO();
                return dao.getComplaintsByCitizen(Session.getCitizen().getCitizenId());
            }

            @Override
            protected void done() {
                try {
                    List<Complaint> complaints = get();
                    tableModel.setRowCount(0);
                    if (complaints.isEmpty()) {
                        Theme.setTableEmpty(tableModel, "No complaints yet.");
                        return;
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                    for (Complaint c : complaints) {
                        tableModel.addRow(new Object[]{
                                c.getComplaintId(),
                                c.getCategory(),
                                c.getComplaintDate() != null ? sdf.format(c.getComplaintDate()) : "",
                                c.getStatus(),
                                c.getOfficialRemarks() == null ? "" : c.getOfficialRemarks()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CitizenPortalFrame.this,
                            "Error fetching complaints: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
