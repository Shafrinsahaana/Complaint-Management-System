package ui;

import dao.CitizenDAO;
import model.Citizen;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.regex.Pattern;

/** Citizen account registration screen. */
public class CitizenFrame extends JFrame {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final CitizenDAO citizenDAO = new CitizenDAO();

    private final JTextField nameField = new JTextField(24);
    private final JTextField emailField = new JTextField(24);
    private final JPasswordField passwordField = new JPasswordField(24);
    private final JPasswordField confirmPasswordField = new JPasswordField(24);

    private JLabel validationLabel;

    public CitizenFrame() {
        setTitle("CivicDesk - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(500, 600));
        setIconImage(Theme.getAppIcon());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.PAGE_BACKGROUND);

        JButton backButton = Theme.secondaryButton("Back to Login");
        backButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

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
        topBar.add(backButton, BorderLayout.EAST);
        mainPanel.add(topBar, BorderLayout.NORTH);

        mainPanel.add(createForm(), BorderLayout.CENTER);
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowOpened(java.awt.event.WindowEvent e) {
                nameField.requestFocusInWindow();
            }
        });
    }

    private JPanel createForm() {
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(Theme.PAGE_BACKGROUND);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        Theme.card(form);
        form.setPreferredSize(new Dimension(400, 500));

        JLabel heading = Theme.sectionTitle("Create an account");
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(heading);
        form.add(Box.createVerticalStrut(Theme.SPACING_24));

        Theme.styledTextField(nameField);
        Theme.styledTextField(emailField);
        Theme.styledTextField(passwordField);
        Theme.styledTextField(confirmPasswordField);

        addField(form, "Name", nameField);
        addField(form, "Email", emailField);
        addField(form, "Password", passwordField);
        addField(form, "Confirm Password", confirmPasswordField);

        validationLabel = Theme.validationLabel();
        validationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(validationLabel);
        form.add(Box.createVerticalStrut(Theme.SPACING_16));

        JButton registerButton = Theme.primaryButton("Register");
        registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        registerButton.addActionListener(event -> registerCitizen());
        form.add(registerButton);
        getRootPane().setDefaultButton(registerButton);

        formContainer.add(form);
        return formContainer;
    }

    private void addField(JPanel form, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_INPUT.deriveFont(Font.BOLD));
        l.setForeground(Theme.TEXT_BODY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(l);
        form.add(Box.createVerticalStrut(Theme.SPACING_8));
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        form.add(field);
        form.add(Box.createVerticalStrut(Theme.SPACING_16));
    }

    private void registerCitizen() {
        validationLabel.setText("");
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            validationLabel.setText("Please fill in all fields.");
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            validationLabel.setText("Please enter a valid email address.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            validationLabel.setText("Passwords do not match.");
            return;
        }

        Citizen citizen = new Citizen();
        citizen.setName(name);
        citizen.setEmail(email);
        citizen.setPassword(password);
            
        getRootPane().getDefaultButton().setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                citizenDAO.registerCitizen(citizen);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(CitizenFrame.this, "Registration successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new LoginFrame().setVisible(true);
                } catch (Exception e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof SQLException) {
                        validationLabel.setText(util.DBErrorHandler.getMessage((SQLException) cause));
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
