package ui;

import dao.CitizenDAO;
import dao.OfficialDAO;
import model.Citizen;
import model.Official;
import util.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleDropdown;
    private JLabel validationLabel;

    public LoginFrame() {
        setTitle("CivicDesk - Login");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setMinimumSize(new Dimension(600, 400));
        setIconImage(Theme.getAppIcon());

        createUI();
        // Focus email field after window is visible
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                emailField.requestFocusInWindow();
            }
        });
    }

    private void createUI() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Left Panel (Accent)
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Theme.ACCENT);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32, Theme.SPACING_32));

        leftPanel.add(Box.createVerticalGlue());
        JLabel titleLabel = new JLabel("CivicDesk");
        titleLabel.setFont(Theme.FONT_TITLE_PAGE);
        titleLabel.setForeground(Theme.SURFACE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Manage civic issues efficiently.");
        subtitleLabel.setFont(Theme.FONT_INPUT);
        subtitleLabel.setForeground(Theme.SURFACE);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(Theme.SPACING_8));
        leftPanel.add(subtitleLabel);
        leftPanel.add(Box.createVerticalGlue());

        // Right Panel (Form)
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Theme.PAGE_BACKGROUND);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        Theme.card(form);
        form.setPreferredSize(new Dimension(320, 380));

        JLabel loginHeading = Theme.sectionTitle("Sign In");
        loginHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(loginHeading);
        form.add(Box.createVerticalStrut(Theme.SPACING_24));

        roleDropdown = new JComboBox<>(new String[]{"Citizen", "Official"});
        roleDropdown.setFont(Theme.FONT_INPUT);
        roleDropdown.setBackground(Theme.SURFACE);
        roleDropdown.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        emailField = new JTextField();
        Theme.styledTextField(emailField);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        passwordField = new JPasswordField();
        Theme.styledTextField(passwordField);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        addField(form, "Login As", roleDropdown);
        addField(form, "Email", emailField);
        addField(form, "Password", passwordField);

        validationLabel = Theme.validationLabel();
        validationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(validationLabel);
        form.add(Box.createVerticalStrut(Theme.SPACING_8));

        JButton loginButton = Theme.primaryButton("Sign in");
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginButton.addActionListener(e -> attemptLogin());
        getRootPane().setDefaultButton(loginButton);
        form.add(loginButton);

        form.add(Box.createVerticalStrut(Theme.SPACING_16));

        JLabel registerLabel = new JLabel("Create an account");
        registerLabel.setFont(Theme.FONT_BODY);
        registerLabel.setForeground(Theme.ACCENT);
        registerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                new CitizenFrame().setVisible(true);
            }
        });
        form.add(registerLabel);

        rightPanel.add(form);
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);
    }

    private void addField(JPanel form, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_INPUT.deriveFont(Font.BOLD));
        l.setForeground(Theme.TEXT_BODY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(l);
        form.add(Box.createVerticalStrut(Theme.SPACING_8));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(field);
        form.add(Box.createVerticalStrut(Theme.SPACING_16));
    }

    private void attemptLogin() {
        validationLabel.setText(" ");
        String role = (String) roleDropdown.getSelectedItem();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            validationLabel.setText("Please fill in all fields.");
            return;
        }

        getRootPane().getDefaultButton().setEnabled(false);
        validationLabel.setText("Signing in...");
        validationLabel.setForeground(Theme.TEXT_MUTED);

        SwingWorker<Object, Void> worker = new SwingWorker<>() {
            @Override
            protected Object doInBackground() throws Exception {
                if ("Citizen".equals(role)) {
                    return new CitizenDAO().authenticate(email, password);
                } else if ("Official".equals(role)) {
                    return new OfficialDAO().authenticate(email, password);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    Object result = get();
                    if (result instanceof Citizen) {
                        Session.setCitizen((Citizen) result);
                        dispose();
                        new CitizenPortalFrame().setVisible(true);
                    } else if (result instanceof Official) {
                        Session.setOfficial((Official) result);
                        dispose();
                        new OfficialFrame().setVisible(true);
                    } else {
                        validationLabel.setForeground(Theme.VALIDATION_RED);
                        validationLabel.setText("Invalid email or password.");
                    }
                } catch (Exception e) {
                    validationLabel.setForeground(Theme.VALIDATION_RED);
                    Throwable cause = e.getCause();
                    if (cause instanceof java.sql.SQLException) {
                        validationLabel.setText(util.DBErrorHandler.getMessage((java.sql.SQLException) cause));
                    } else {
                        validationLabel.setText("Error: " + e.getMessage());
                    }
                } finally {
                    getRootPane().getDefaultButton().setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}
