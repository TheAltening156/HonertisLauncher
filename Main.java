import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class Main extends JFrame {

    public Auth auth;

    public Main() {
        // Configuration de la fenêtre
        setTitle("Honertis Launcher 1.0");
        setSize(500, 250);
        setLocationRelativeTo(null); // Centrer la fenêtre
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Fond avec panneau personnalisé
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(34, 34, 34));
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setContentPane(mainPanel);

        // Texte d'accueil
        JLabel text = new JLabel("Bienvenue sur le lanceur de Honertis !");
        text.setForeground(Color.WHITE);
        text.setFont(new Font("Segoe UI", Font.BOLD, 16));
        text.setBorder(null);
        text.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(34, 34, 34));
        topBar.add(text, BorderLayout.CENTER);
        mainPanel.add(topBar, BorderLayout.PAGE_START);

        // Titre
        JLabel logoLabel = new JLabel("Honertis Launcher 1.0");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(logoLabel, BorderLayout.NORTH);

        // Panneau central vertical
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(34, 34, 34));
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Panel horizontal pour champ pseudo + bouton à droite
        JPanel nameLinePanel = new JPanel();
        nameLinePanel.setLayout(new BoxLayout(nameLinePanel, BoxLayout.X_AXIS));
        nameLinePanel.setBackground(new Color(34, 34, 34));

        // Champ pseudo
        JTextField name = new JTextField();
        name.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        name.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        name.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Placeholder visuel
        String placeholder = "Pseudo (cracké)";
        name.setText(placeholder);
        name.setForeground(Color.GRAY);

        // Comportement du placeholder
        name.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (name.getText().equals(placeholder)) {
                    name.setText("");
                    name.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (name.getText().isEmpty()) {
                    name.setText(placeholder);
                    name.setForeground(Color.GRAY);
                }
            }
        });

        // Bouton à droite du champ
        JButton btnAction = new JButton("Microsoft");
        btnAction.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAction.setPreferredSize(new Dimension(100, 40));
        btnAction.setMaximumSize(new Dimension(120, 40));
        btnAction.setBackground(new Color(98, 142, 203));
        btnAction.setForeground(Color.WHITE);
        btnAction.setFocusPainted(false);
        btnAction.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAction.setToolTipText("Options (à définir)");

        // Action du bouton ⋮
        btnAction.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Action du bouton d'options !");
        });

        // Ajout des composants à la ligne
        nameLinePanel.add(name);
        nameLinePanel.add(Box.createRigidArea(new Dimension(10, 0))); // espacement
        nameLinePanel.add(btnAction);

        // Ajout du champ + bouton au panneau central
        centerPanel.add(nameLinePanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20))); // espacement vertical

        // Bouton Lancer le jeu
        JButton launchButton = new JButton("Lancer le jeu");
        launchButton.setBackground(new Color(70, 130, 180));
        launchButton.setForeground(Color.WHITE);
        launchButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        launchButton.setFocusPainted(false);
        launchButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        launchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        launchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        SwingUtilities.invokeLater(() -> {
            launchButton.requestFocusInWindow(); // ou tout autre composant
        });
        // Action du bouton Lancer
        launchButton.addActionListener(e -> {
            String username = name.getText().trim();
            if (username.isEmpty() || !Utils.isValidMinecraftUsername(username)) {
                JOptionPane.showMessageDialog(null, "Veuillez entrer un nom d'utilisateur valide.");
                return;
            } else {
                auth = new Auth(username, "", "", false);
            }

            if (auth != null) {
                try {
                    Runtime.getRuntime().exec("java -jar tonjeu.jar " + username);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Erreur lors du lancement du jeu.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        centerPanel.add(launchButton);
        JPanel bottomLinePanel = new JPanel();
        bottomLinePanel.setLayout(new BoxLayout(bottomLinePanel, BoxLayout.X_AXIS));
        bottomLinePanel.setBackground(new Color(34, 34, 34));

        // Liste des versions (à personnaliser)
        String[] versions = {"1.2", "1.3", "1.4", "1.5" };
        JComboBox<String> versionCombo = new JComboBox<>(versions);
        versionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        versionCombo.setMaximumSize(new Dimension(120, 40));
        versionCombo.setPreferredSize(new Dimension(120, 40));
        versionCombo.setBackground(new Color(60, 60, 60));
        versionCombo.setForeground(Color.WHITE);
        versionCombo.setFocusable(false);

        // Ajout des composants au panel
        bottomLinePanel.add(launchButton);
        bottomLinePanel.add(Box.createRigidArea(new Dimension(20, 0))); // espacement
        bottomLinePanel.add(versionCombo);
        centerPanel.add(bottomLinePanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}
