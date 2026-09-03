/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import model.User;
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    
     // Card name constants
    public static final String CARD_DASHBOARD      = "Dashboard";
    public static final String CARD_NEW_APPOINTMENT = "New Appointment";
    public static final String CARD_SEARCH          = "Search Appointment";
    public static final String CARD_BILLING         = "Billing";
    public static final String CARD_REPORTS         = "Reports";
    public static final String CARD_USER_MGMT       = "User Management";
    public static final String CARD_HELP            = "Help";
 
    // Colors
    private static final Color SIDEBAR_BG     = new Color(31, 78, 121);
    private static final Color SIDEBAR_HOVER  = new Color(41, 98, 151);
    private static final Color SIDEBAR_ACTIVE = new Color(52, 120, 180);
    private static final Color HEADER_BG      = Color.WHITE;
    private static final Color ACCENT_BLUE    = new Color(46, 117, 182);
    private static final Color ACCENT_GOLD    = new Color(214, 182, 86);
 
    private final User loggedInUser;
    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private JButton activeButton = null;
    private JLabel clockLabel;
 
    public MainFrame(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.cardLayout = new CardLayout();
        this.contentPanel = new JPanel(cardLayout);
 
        setTitle("Sunrise Dental Clinic — Appointment & Patient Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLayout(new BorderLayout());
 
        add(buildHeader(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
 
        registerPanels();
        startClock();
 
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
 
        // Show dashboard by default
        switchTo(CARD_DASHBOARD);
    }
 
    // ======================== HEADER ========================
 
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        header.setPreferredSize(new Dimension(0, 54));
 
        // Left: clinic name
        JLabel clinicLabel = new JLabel("Sunrise Dental Clinic");
        clinicLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        clinicLabel.setForeground(ACCENT_BLUE);
        header.add(clinicLabel, BorderLayout.WEST);
 
        // Right: user info + clock + logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);
 
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clockLabel.setForeground(Color.GRAY);
        rightPanel.add(clockLabel);
 
        rightPanel.add(createSeparator());
 
        JLabel userLabel = new JLabel(loggedInUser.getUsername());
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        rightPanel.add(userLabel);
 
        JLabel roleBadge = new JLabel(" " + loggedInUser.getRole().toUpperCase() + " ");
        roleBadge.setFont(new Font("SansSerif", Font.BOLD, 10));
        roleBadge.setOpaque(true);
        boolean isAdmin = "Admin".equals(loggedInUser.getRole());
        roleBadge.setBackground(isAdmin ? ACCENT_GOLD : ACCENT_BLUE);
        roleBadge.setForeground(Color.WHITE);
        roleBadge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        rightPanel.add(roleBadge);
 
        rightPanel.add(createSeparator());
 
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        logoutBtn.setForeground(new Color(192, 57, 43));
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        rightPanel.add(logoutBtn);
 
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }
 
    // ======================== SIDEBAR ========================
 
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(210, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
 
        // Sidebar title
        JLabel navTitle = new JLabel("  NAVIGATION");
        navTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        navTitle.setForeground(new Color(150, 180, 210));
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        navTitle.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 0));
        sidebar.add(navTitle);
 
        // Shared (Staff) items — both Receptionist and Admin
        addNavButton(sidebar, CARD_DASHBOARD,       "\u2302  Dashboard");
        addNavButton(sidebar, CARD_NEW_APPOINTMENT,  "\u2795  New Appointment");
        addNavButton(sidebar, CARD_SEARCH,           "\uD83D\uDD0D  Search Appointment");
        addNavButton(sidebar, CARD_BILLING,          "\uD83D\uDCB0  Billing");
 
        // Admin-only items
        if ("Admin".equals(loggedInUser.getRole())) {
            sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
 
            JLabel adminLabel = new JLabel("  ADMIN");
            adminLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
            adminLabel.setForeground(ACCENT_GOLD);
            adminLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            adminLabel.setBorder(BorderFactory.createEmptyBorder(4, 16, 8, 0));
            sidebar.add(adminLabel);
 
            addNavButton(sidebar, CARD_REPORTS,    "\uD83D\uDCCA  Reports");
            addNavButton(sidebar, CARD_USER_MGMT,  "\uD83D\uDC65  User Management");
        }
 
        sidebar.add(Box.createVerticalGlue());
 
        // Bottom items
        addNavButton(sidebar, CARD_HELP, "\u2753  Help");
 
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
 
        JButton exitBtn = createNavButton("\u274C  Exit System");
        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleExit();
            }
        });
        sidebar.add(exitBtn);
 
        return sidebar;
    }
 
    private void addNavButton(JPanel sidebar, String cardName, String label) {
        JButton btn = createNavButton(label);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchTo(cardName);
            }
        });
        navButtons.put(cardName, btn);
        sidebar.add(btn);
    }
 
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(SIDEBAR_BG);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 10));
 
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (btn != activeButton) {
                    btn.setBackground(SIDEBAR_HOVER);
                }
            }
 
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (btn != activeButton) {
                    btn.setBackground(SIDEBAR_BG);
                }
            }
        });
 
        return btn;
    }
 
    // ======================== CONTENT PANELS ========================
 
    private void registerPanels() {
        contentPanel.add(new DashboardPanel(loggedInUser, this), CARD_DASHBOARD);
        contentPanel.add(new AppointmentBookingPanel(loggedInUser), CARD_NEW_APPOINTMENT);
        contentPanel.add(new SearchAppointmentPanel(loggedInUser), CARD_SEARCH);
        contentPanel.add(new BillingPanel(loggedInUser), CARD_BILLING);
        contentPanel.add(new HelpPanel(), CARD_HELP);
 
        if ("Admin".equals(loggedInUser.getRole())) {
            contentPanel.add(new ReportPanel(), CARD_REPORTS);
            contentPanel.add(new UserManagementPanel(), CARD_USER_MGMT);
        }
    }
 
    // ======================== NAVIGATION ========================
 
    /**
     * Switches the visible content panel and updates the active
     * sidebar button highlight. Called by sidebar buttons and
     * DashboardPanel quick-action buttons.
     */
    public void switchTo(String cardName) {
        cardLayout.show(contentPanel, cardName);
 
        // Update sidebar highlights
        if (activeButton != null) {
            activeButton.setBackground(SIDEBAR_BG);
            activeButton.setFont(activeButton.getFont().deriveFont(Font.PLAIN));
        }
        JButton btn = navButtons.get(cardName);
        if (btn != null) {
            btn.setBackground(SIDEBAR_ACTIVE);
            btn.setFont(btn.getFont().deriveFont(Font.BOLD));
            activeButton = btn;
        }
    }
 
    // ======================== ACTIONS ========================
 
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
 
        if (confirm == JOptionPane.YES_OPTION) {
            loggedInUser.logout();
            dispose(); // Destroy MainFrame (a new one is created on next login)
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // Reuse the Singleton — getInstance() resets all fields automatically
                    LoginForm.getInstance().setVisible(true);
                }
            });
        }
    }
 
    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit the system?", "Confirm Exit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
 
        if (confirm == JOptionPane.YES_OPTION) {
            loggedInUser.logout();
            config.DBConnection.getInstance().closeConnection();
            System.exit(0);
        }
    }
 
    // ======================== HELPERS ========================
 
    private void startClock() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clockLabel.setText(LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy  |  hh:mm:ss a")));
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }
 
    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 24));
        sep.setForeground(new Color(220, 220, 220));
        return sep;
    }
    
}
