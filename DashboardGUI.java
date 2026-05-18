// -------------------------------------------------------------
// DashboardGUI.java
// Main entry point and graphical user interface for the
// File Encryption Dashboard application
// -------------------------------------------------------------

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.crypto.SecretKey;

public class DashboardGUI extends JFrame {

    // Colour Palette
    private static final Color BG_BASE    = new Color(10,  12,  20 );
    private static final Color BG_CARD    = new Color(16,  20,  32 );
    private static final Color BG_INPUT   = new Color(22,  27,  44 );
    private static final Color BG_ROW_ALT = new Color(14,  17,  28 );
    private static final Color ACCENT     = new Color(94,  234, 212);
    private static final Color ACCENT2    = new Color(139, 92,  246);
    private static final Color SUCCESS    = new Color(52,  211, 153);
    private static final Color WARNING    = new Color(251, 191, 36 );
    private static final Color DANGER     = new Color(248, 113, 113);
    private static final Color TEXT_PRI   = new Color(241, 245, 249);
    private static final Color TEXT_SEC   = new Color(100, 116, 139);
    private static final Color BORDER_CLR = new Color(30,  38,  60 );

    // Service layer fields
    private AuthenticationService authService;
    private FileHandler           fileHandler;
    private EncryptionService     encryptionService;
    private KeyManager            keyManager;
    private AuditLogger           auditLogger;

    // Session state
    private User         currentUser;
    private FileMetadata selectedFile;

    // GUI component fields
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         statusLabel;
    private JLabel         fileInfoLabel;
    private JTextArea      logArea;
    private JPanel         mainPanel;
    private JPanel         loginPanel;

    // Stats bar value labels
    private JLabel statTotalVal;
    private JLabel statEncVal;
    private JLabel statUnencVal;
    private JLabel statKeysVal;

    // File library table
    private DefaultTableModel tableModel;
    private JTable            fileTable;

    // Constructor
    public DashboardGUI() {
        auditLogger       = new AuditLogger();
        authService       = new AuthenticationService(auditLogger);
        fileHandler       = new FileHandler(auditLogger);
        keyManager        = new KeyManager(auditLogger);
        encryptionService = new EncryptionService(null, auditLogger);

        // Apply Nimbus look and feel for cleaner rendering
        try {
            for (UIManager.LookAndFeelInfo info :
                    UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        setTitle("Cipherloop  |  File Encryption Dashboard");
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_BASE);
        setLayout(new CardLayout());

        buildLoginPanel();
        buildMainPanel();

        add(loginPanel, "LOGIN");
        add(mainPanel,  "MAIN");

        showPanel("LOGIN");
        setVisible(true);
    }

    // ===============
    //  LOGIN PANEL 
    // ===============
    private void buildLoginPanel() {
        // Full-window split layout: teal left panel + dark right panel
        loginPanel = new JPanel(new GridLayout(1, 2, 0, 0));

        // LEFT PANEL: branding
        Color TEAL_DARK   = new Color(4,  80,  60);
        Color TEAL_MID    = new Color(6,  110, 82);
        Color TEAL_ACCENT = new Color(52, 211, 153);

        JPanel left = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0,            TEAL_DARK,
                    0, getHeight(),  TEAL_MID);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        left.setLayout(new GridBagLayout());

        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx  = 0; lc.fill = GridBagConstraints.HORIZONTAL;
        lc.insets = new Insets(10, 40, 10, 40);

        // Top spacer to push content to vertical centre
        lc.gridy = 0; lc.weighty = 0.2;
        left.add(new JLabel(""), lc);

        // Cipherloop name displayed 
        JLabel logoName = new JLabel("Cipherloop",
            SwingConstants.CENTER);
        logoName.setFont(new Font("Georgia", Font.BOLD, 28));
        logoName.setForeground(Color.WHITE);
        lc.gridy = 1; lc.weighty = 0;
        lc.anchor = GridBagConstraints.CENTER;
        lc.insets = new Insets(0, 40, 4, 40);
        left.add(logoName, lc);

        JLabel logoSub = new JLabel("File Encryption Dashboard",
            SwingConstants.CENTER);
        logoSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoSub.setForeground(new Color(180, 230, 210));
        lc.gridy = 2; lc.insets = new Insets(0, 40, 16, 40);
        left.add(logoSub, lc);

        // Centre illustration: shield monitor graphic
        JPanel illustration = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

                int cx = getWidth() / 2;
                int cy = getHeight() / 2;

                // Monitor body
                g2.setColor(new Color(10, 120, 90, 120));
                g2.fillRoundRect(cx - 80, cy - 65,
                                 160, 110, 12, 12);
                g2.setColor(TEAL_ACCENT);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(cx - 80, cy - 65,
                                 160, 110, 12, 12);

                // Screen lines 
                g2.setColor(new Color(52, 211, 153, 180));
                g2.setStroke(new BasicStroke(3f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
                g2.drawLine(cx - 55, cy - 30, cx + 55, cy - 30);
                g2.setColor(new Color(52, 211, 153, 120));
                g2.drawLine(cx - 55, cy - 10, cx + 30, cy - 10);
                g2.drawLine(cx - 55, cy + 10, cx + 45, cy + 10);

                // Monitor stand
                g2.setColor(TEAL_ACCENT);
                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(cx, cy + 46, cx, cy + 70);
                g2.fillRoundRect(cx - 30, cy + 68,
                                 60, 10, 6, 6);

                // Shield overlay 
                g2.setColor(new Color(10, 120, 90, 200));
                int[] sx = {cx - 18, cx, cx + 18,
                            cx + 18, cx, cx - 18};
                int[] sy = {cy + 25, cy + 20, cy + 25,
                            cy + 38, cy + 46, cy + 38};
                g2.fillPolygon(sx, sy, 6);
                g2.setColor(TEAL_ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawPolygon(sx, sy, 6);

                // Lock dot on stand
                g2.setColor(TEAL_ACCENT);
                g2.fillOval(cx - 5, cy + 62, 10, 10);
            }
        };
        illustration.setOpaque(false);
        illustration.setPreferredSize(new Dimension(200, 200));
        lc.gridy  = 3; lc.weighty = 0.4;
        lc.anchor = GridBagConstraints.CENTER;
        lc.insets = new Insets(0, 40, 10, 40);
        left.add(illustration, lc);

        // Tagline
        JLabel tagline = new JLabel(
            "<html><b>Secure access to your<br>encrypted vault</b></html>");
        tagline.setFont(new Font("Georgia", Font.BOLD, 22));
        tagline.setForeground(Color.WHITE);
        lc.gridy = 4; lc.weighty = 0;
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(0, 40, 6, 40);
        left.add(tagline, lc);

        // Subtitle description
        JLabel desc = new JLabel(
            "<html>Military-grade AES-256 encryption protecting<br>"
          + "your files. Every session is verified and logged.</html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        desc.setForeground(new Color(180, 230, 210));
        lc.gridy = 5; lc.insets = new Insets(0, 40, 16, 40);
        left.add(desc, lc);

        // Bottom spacer
        lc.gridy = 6; lc.weighty = 0.2;
        lc.insets = new Insets(0, 40, 20, 40);
        lc.anchor = GridBagConstraints.SOUTHWEST;
        left.add(new JLabel(""), lc);

        // RIGHT PANEL: login form 
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(new Color(18, 20, 28));

        GridBagConstraints rc = new GridBagConstraints();
        rc.gridx  = 0;
        rc.fill   = GridBagConstraints.HORIZONTAL;
        rc.insets = new Insets(8, 60, 8, 60);

        // Welcome heading
        JLabel welcome = new JLabel("Welcome back");
        welcome.setFont(new Font("Georgia", Font.BOLD, 28));
        welcome.setForeground(Color.WHITE);
        rc.gridy = 0; rc.insets = new Insets(40, 60, 4, 60);
        right.add(welcome, rc);

        JLabel welcomeSub = new JLabel(
            "Sign in to access your encrypted files");
        welcomeSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeSub.setForeground(new Color(140, 150, 165));
        rc.gridy = 1; rc.insets = new Insets(0, 60, 28, 60);
        right.add(welcomeSub, rc);

        // Username label and field
        rc.gridy  = 2; rc.insets = new Insets(6, 60, 4, 60);
        JLabel userLbl = makeLabel("Username", 
            new Color(200, 210, 220), 13);
        right.add(userLbl, rc);

        rc.gridy = 3; rc.insets = new Insets(0, 60, 10, 60);
        usernameField = new JTextField(20);
        styleLoginField(usernameField, "\uD83D\uDC64  username");
        right.add(usernameField, rc);

        // Password label and field
        rc.gridy  = 4; rc.insets = new Insets(6, 60, 4, 60);
        JLabel passLbl = makeLabel("Password",
            new Color(200, 210, 220), 13);
        right.add(passLbl, rc);

        rc.gridy = 5; rc.insets = new Insets(0, 60, 6, 60);
        passwordField = new JPasswordField(20);
        styleLoginField(passwordField, "\uD83D\uDD10  password");
        right.add(passwordField, rc);

        // Status & label
        rc.gridy = 6; rc.insets = new Insets(2, 60, 4, 60);
        statusLabel = new JLabel("", SwingConstants.LEFT);
        statusLabel.setForeground(DANGER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        right.add(statusLabel, rc);

        // Sign in button
        rc.gridy = 7; rc.insets = new Insets(12, 60, 10, 60);
        JButton loginBtn = new JButton(
            "\uD83D\uDD12  Sign in securely");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setBackground(new Color(15, 150, 100));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(
                           13, 20, 13, 20));
        loginBtn.setCursor(Cursor.getPredefinedCursor(
                           Cursor.HAND_CURSOR));
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginBtn.setBackground(
                    new Color(20, 180, 120));
            }
            public void mouseExited(MouseEvent e) {
                loginBtn.setBackground(
                    new Color(15, 150, 100));
            }
        });

        loginBtn.addActionListener(e -> loginAction());
        right.add(loginBtn, rc);

        // Hint footer
        rc.gridy = 8; rc.insets = new Insets(20, 60, 30, 60);
        JLabel hint = new JLabel(
            "Credentials:  widath / pass123   "
          + "\u00B7   admin / admin123",
            SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hint.setForeground(new Color(80, 95, 110));
        right.add(hint, rc);

        loginPanel.add(left);
        loginPanel.add(right);
    }

    // Styles a login form text field with placeholder colour
    private void styleLoginField(JComponent f, String placeholder) {
        f.setBackground(new Color(26, 30, 42));
        f.setForeground(new Color(200, 210, 225));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(40, 50, 68), 1, true),
            BorderFactory.createEmptyBorder(11, 14, 11, 14)));
        if (f instanceof JTextField)
            ((JTextField) f).setCaretColor(ACCENT);
    }


    // =========================
    //  MAIN DASHBOARD PANEL
    // =========================
    private void buildMainPanel() {
        mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BG_BASE);
        mainPanel.add(buildTopBar(),     BorderLayout.NORTH);
        mainPanel.add(buildCentreArea(), BorderLayout.CENTER);
    }

    // Top navigation bar
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_CARD);
        bar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR),
            BorderFactory.createEmptyBorder(12, 22, 12, 22)));

        JLabel logo = new JLabel("\uD83D\uDEE1  Cipherloop");
        logo.setFont(new Font("Georgia", Font.BOLD, 18));
        logo.setForeground(ACCENT);
        bar.add(logo, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(
                                   FlowLayout.RIGHT, 8, 0));
        right.setBackground(BG_CARD);

        JButton auditBtn = makeNavButton("Audit Report");
        auditBtn.addActionListener(e -> showAuditDialog());
        right.add(auditBtn);

        JButton keyBtn = makeNavButton("Key Manager");
        keyBtn.addActionListener(e -> showKeyManagerDialog());
        right.add(keyBtn);

        JButton logoutBtn = makeNavButton("Logout");
        logoutBtn.setForeground(DANGER);
        logoutBtn.addActionListener(e -> logoutAction());
        right.add(logoutBtn);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // Centre area
    private JPanel buildCentreArea() {
        JPanel centre = new JPanel(new BorderLayout(0, 0));
        centre.setBackground(BG_BASE);
        centre.setBorder(BorderFactory.createEmptyBorder(
                         18, 20, 18, 20));

        centre.add(buildStatsBar(), BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG_BASE);
        content.setBorder(BorderFactory.createEmptyBorder(
                           14, 0, 0, 0));

        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.BOTH;
        g.weighty = 1.0;

        g.gridx   = 0;
        g.weightx = 0.37;
        g.insets  = new Insets(0, 0, 0, 14);
        content.add(buildEncryptCard(), g);

        g.gridx   = 1;
        g.weightx = 0.63;
        g.insets  = new Insets(0, 0, 0, 0);
        content.add(buildFileTableCard(), g);

        centre.add(content, BorderLayout.CENTER);
        return centre;
    }

    // Stats bar
    private JPanel buildStatsBar() {
        JPanel bar = new JPanel(new GridLayout(1, 4, 12, 0));
        bar.setBackground(BG_BASE);

        statTotalVal = new JLabel("0");
        statEncVal   = new JLabel("0");
        statUnencVal = new JLabel("0");
        statKeysVal  = new JLabel("0");

        bar.add(buildStatCard(
            "\uD83D\uDCC4  Total Files",  statTotalVal, TEXT_PRI));
        bar.add(buildStatCard(
            "\uD83D\uDD12  Encrypted",    statEncVal,   SUCCESS));
        bar.add(buildStatCard(
            "\uD83D\uDD13  Decrypted",    statUnencVal, WARNING));
        bar.add(buildStatCard(
            "\uD83D\uDD11  Keys Stored",  statKeysVal,  ACCENT));
        return bar;
    }

    private JPanel buildStatCard(String title,
                                  JLabel valueLabel,
                                  Color  valueColor) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JLabel titleLbl = makeLabel(title, TEXT_SEC, 11);
        valueLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        valueLabel.setForeground(valueColor);

        card.add(titleLbl,   BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // Encrypt control card
    private JPanel buildEncryptCard() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel head = makeLabel(
            "\uD83D\uDD12  ENCRYPT A FILE", ACCENT, 11);
        card.add(head, BorderLayout.NORTH);

        JPanel mid = new JPanel(new BorderLayout(0, 10));
        mid.setBackground(BG_CARD);

        JPanel dropZone = new JPanel(new GridBagLayout());
        dropZone.setBackground(BG_INPUT);
        dropZone.setBorder(new DashedBorder(BORDER_CLR, 2));
        dropZone.setPreferredSize(new Dimension(0, 100));
        dropZone.setCursor(Cursor.getPredefinedCursor(
                           Cursor.HAND_CURSOR));

        JPanel inner = new JPanel(new GridLayout(2, 1, 0, 4));
        inner.setBackground(BG_INPUT);
        JLabel dIcon = makeLabel("\uD83D\uDCC2", TEXT_SEC, 24);
        dIcon.setHorizontalAlignment(SwingConstants.CENTER);
        fileInfoLabel = makeLabel(
            "Click Browse to select a file", TEXT_SEC, 11);
        fileInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inner.add(dIcon);
        inner.add(fileInfoLabel);
        dropZone.add(inner);

        dropZone.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectFile();
            }
        });
        mid.add(dropZone, BorderLayout.CENTER);

        JButton browseBtn = makeAccentButton(
            "Browse File", ACCENT, Color.WHITE);
        browseBtn.addActionListener(e -> selectFile());
        mid.add(browseBtn, BorderLayout.SOUTH);
        card.add(mid, BorderLayout.CENTER);

        // Action buttons at the bottom
        JPanel actions = new JPanel(new GridLayout(3, 1, 0, 8));
        actions.setBackground(BG_CARD);

        JButton genKeyBtn = makeAccentButton(
            "\uD83D\uDD11  Generate AES-256 Key",
            ACCENT2, Color.WHITE);
        genKeyBtn.addActionListener(e -> generateKeyAction());

        JButton encBtn = makeAccentButton(
            "\uD83D\uDD12  Encrypt File",
            SUCCESS, Color.WHITE);
        encBtn.addActionListener(e -> encryptAction());

        JButton decBtn = makeAccentButton(
            "\uD83D\uDD13  Decrypt File",
            new Color(30, 80, 80), Color.WHITE);
        decBtn.addActionListener(e -> decryptAction());

        actions.add(genKeyBtn);
        actions.add(encBtn);
        actions.add(decBtn);
        card.add(actions, BorderLayout.SOUTH);

        return card;
    }

    // File library table card
    private JPanel buildFileTableCard() {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        card.add(makeLabel(
            "\uD83D\uDDC2  FILE LIBRARY", ACCENT, 11),
            BorderLayout.NORTH);

        // Table setup
        String[] cols = {"FILE", "SIZE", "ALGORITHM",
                          "STATUS", "TIME"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        fileTable = new JTable(tableModel);
        fileTable.setBackground(BG_CARD);
        fileTable.setForeground(TEXT_PRI);
        fileTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fileTable.setRowHeight(34);
        fileTable.setShowGrid(false);
        fileTable.setIntercellSpacing(new Dimension(0, 0));
        fileTable.setSelectionBackground(BG_INPUT);
        fileTable.setSelectionForeground(TEXT_PRI);

        JTableHeader header = fileTable.getTableHeader();
        header.setBackground(BG_BASE);
        header.setForeground(TEXT_SEC);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_CLR));

        // Custom cell renderer for alternating rows and status
        fileTable.setDefaultRenderer(Object.class,
            new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel,
                        boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(
                        t, v, sel, foc, row, col);
                    setBackground(row % 2 == 0
                        ? BG_CARD : BG_ROW_ALT);
                    setForeground(TEXT_PRI);
                    setBorder(BorderFactory.createEmptyBorder(
                              0, 10, 0, 10));
                    if (col == 3 && v != null) {
                        String s = v.toString();
                        if (s.equals("Encrypted"))
                            setForeground(SUCCESS);
                        else if (s.equals("Unencrypted"))
                            setForeground(WARNING);
                        else if (s.equals("Decrypted"))
                            setForeground(ACCENT);
                    }
                    return this;
                }
            });

        JScrollPane scroll = new JScrollPane(fileTable);
        scroll.setBackground(BG_CARD);
        scroll.getViewport().setBackground(BG_CARD);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);

        // Audit log strip at the bottom of the table card
        logArea = new JTextArea(4, 0);
        logArea.setEditable(false);
        logArea.setBackground(BG_BASE);
        logArea.setForeground(new Color(80, 200, 150));
        logArea.setFont(new Font("Courier New", Font.PLAIN, 10));
        logArea.setBorder(BorderFactory.createEmptyBorder(
                          4, 8, 4, 8));

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(new MatteBorder(
                            1, 0, 0, 0, BORDER_CLR));
        logScroll.setPreferredSize(new Dimension(0, 88));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(BG_CARD);
        JLabel lh = makeLabel("  AUDIT LOG", TEXT_SEC, 10);
        lh.setBorder(BorderFactory.createEmptyBorder(
                     6, 4, 2, 0));
        bottom.add(lh,        BorderLayout.NORTH);
        bottom.add(logScroll, BorderLayout.CENTER);
        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    // ==================
    //  DIALOG WINDOWS
    // ==================

    private void showAuditDialog() {
        JTextArea area = new JTextArea(
            auditLogger.viewLogs(), 20, 60);
        area.setEditable(false);
        area.setFont(new Font("Courier New", Font.PLAIN, 11));
        area.setBackground(BG_BASE);
        area.setForeground(SUCCESS);
        JOptionPane.showMessageDialog(this,
            new JScrollPane(area),
            "Audit Report", JOptionPane.PLAIN_MESSAGE);
    }

    private void showKeyManagerDialog() {
        String msg = "Keys currently stored: "
                   + keyManager.getKeyCount() + "\n\n"
                   + (keyManager.getKeyCount() == 0
                      ? "No keys loaded. Select a file and "
                        + "click Generate AES-256 Key."
                      : "Keys are held in memory for this "
                        + "session.");
        JOptionPane.showMessageDialog(this, msg,
            "Key Manager",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ==================
    //  ACTION METHODS 
    // ==================

    private void loginAction() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        currentUser = authService.validateUser(user, pass);
        if (currentUser != null) {
            statusLabel.setText("");
            showPanel("MAIN");
            updateStats();
            updateLog();
        } else {
            statusLabel.setText(
                "Invalid username or password.");
        }
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this)
                == JFileChooser.APPROVE_OPTION) {
            String path =
                chooser.getSelectedFile().getAbsolutePath();
            fileHandler.chooseFile(path);
            selectedFile = fileHandler.getFileMetadata();
            if (selectedFile != null) {
                fileInfoLabel.setText(
                    selectedFile.getFileName()
                    + "  (" + selectedFile.getFileSize()
                    + " bytes)");
                fileInfoLabel.setForeground(ACCENT);
            }
            updateLog();
        }
    }

    // Generates AES-256 key
    private void generateKeyAction() {
        if (selectedFile == null) {
            showWarn("Please select a file first.", "No File");
            return;
        }
        try {
            SecretKey key = keyManager.generateKey(
                selectedFile.getFileName());
            encryptionService.setSecretKey(key);
            updateStats();
            JOptionPane.showMessageDialog(this,
                "AES-256 key generated for:\n"
                + selectedFile.getFileName(),
                "Key Generated",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (NoSuchAlgorithmException e) {
            showError("Key generation failed: "
                      + e.getMessage());
            auditLogger.logError(
                "Key gen failed: " + e.getMessage());
        }
        updateLog();
    }

    // IOException is a checked exception
    private void encryptAction() {
        if (selectedFile == null) {
            showWarn("Please select a file first.", "No File");
            return;
        }
        if (!encryptionService.validateKey()) {
            showWarn("Please generate a key first.", "No Key");
            return;
        }
        try {
            byte[] data = fileHandler.readFile();
            byte[] enc  = encryptionService.encrypt(data);
            if (enc != null) {
                fileHandler.writeEncryptedFile(enc);
                addTableRow(
                    selectedFile.getFileName(),
                    selectedFile.getFileSize() + " B",
                    "AES-256", "Encrypted");
                updateStats();
                JOptionPane.showMessageDialog(this,
                    "File encrypted successfully.\n"
                    + "Saved as: "
                    + selectedFile.getFileName() + ".enc",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            showError("File error: " + e.getMessage());
            auditLogger.logError(
                "Encrypt error: " + e.getMessage());
        }
        updateLog();
    }

    private void decryptAction() {
        if (selectedFile == null) {
            showWarn("Please select the .enc file.", "No File");
            return;
        }
        if (!encryptionService.validateKey()) {
            showWarn("Please generate a key first.", "No Key");
            return;
        }
        try {
            byte[] enc = fileHandler.readFile();
            byte[] dec = encryptionService.decrypt(enc);
            if (dec != null) {
                fileHandler.writeDecryptedFile(dec);
                addTableRow(
                    selectedFile.getFileName(),
                    selectedFile.getFileSize() + " B",
                    "AES-256", "Decrypted");
                updateStats();
                JOptionPane.showMessageDialog(this,
                    "File decrypted successfully.\n"
                    + "Saved as: "
                    + selectedFile.getFileName() + ".dec",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            showError("File error: " + e.getMessage());
            auditLogger.logError(
                "Decrypt error: " + e.getMessage());
        }
        updateLog();
    }

    private void logoutAction() {
        if (currentUser != null) {
            currentUser.logout();
            auditLogger.logAction(
                "Logged out: "
                + currentUser.getUsername());
        }
        currentUser  = null;
        selectedFile = null;
        fileInfoLabel.setText(
            "Click Browse to select a file");
        fileInfoLabel.setForeground(TEXT_SEC);
        usernameField.setText("");
        passwordField.setText("");
        showPanel("LOGIN");
    }

    // ==================
    //  HELPER METHODS
    // ==================

    private void addTableRow(String name, String size,
                              String algo, String status) {
        String time = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm:ss"));
        tableModel.addRow(new Object[]{
            "\uD83D\uDCC4  " + name,
            size, algo, status, time });
    }

    private void updateStats() {
        int total = tableModel.getRowCount();
        long enc = 0, dec = 0;
        for (int i = 0; i < total; i++) {
            String s = tableModel
                .getValueAt(i, 3).toString();
            if (s.equals("Encrypted")) enc++;
            else if (s.equals("Decrypted")) dec++;
        }
        statTotalVal.setText(String.valueOf(total));
        statEncVal.setText(String.valueOf(enc));
        statUnencVal.setText(String.valueOf(dec));
        statKeysVal.setText(
            String.valueOf(keyManager.getKeyCount()));
    }

    private void updateLog() {
        if (logArea != null) {
            logArea.setText(auditLogger.viewLogs());
            logArea.setCaretPosition(
                logArea.getDocument().getLength());
        }
    }

    private void showPanel(String name) {
        ((CardLayout) getContentPane().getLayout())
            .show(getContentPane(), name);
    }

    private void showWarn(String msg, String title) {
        JOptionPane.showMessageDialog(this, msg, title,
            JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    // UI Factory Helpers

    private JLabel makeLabel(String text, Color color,
                              int size) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }


    private JButton makeAccentButton(String text,
                                      Color bg,
                                      Color fg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(
                    9, 16, 9, 16));
        b.setCursor(Cursor.getPredefinedCursor(
                    Cursor.HAND_CURSOR));
        Color hover = bg.brighter();
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                b.setBackground(hover);
            }
            public void mouseExited(MouseEvent e) {
                b.setBackground(bg);
            }
        });
        return b;
    }

    private JButton makeNavButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(BG_INPUT);
        b.setForeground(TEXT_PRI);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            BorderFactory.createEmptyBorder(
                6, 14, 6, 14)));
        b.setCursor(Cursor.getPredefinedCursor(
                    Cursor.HAND_CURSOR));
        return b;
    }

    // Custom dashed border for the file drop zone
    static class DashedBorder extends AbstractBorder {
        private Color color;
        private int   thickness;

        DashedBorder(Color color, int thickness) {
            this.color     = color;
            this.thickness = thickness;
        }

        public void paintBorder(Component c, Graphics g,
                int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10f, new float[]{6f, 4f}, 0f));
            g2.drawRoundRect(x + 1, y + 1,
                             w - 2, h - 2, 8, 8);
            g2.dispose();
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 4, thickness + 4,
                              thickness + 4, thickness + 4);
        }
    }

    // Application Entry Point
    public static void main(String[] args) {
        // Run GUI on the Event Dispatch Thread as required
        // by Java's Swing threading model
        SwingUtilities.invokeLater(() -> new DashboardGUI());
    }
}