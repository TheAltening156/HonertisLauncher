import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;

@SuppressWarnings("serial")
public class Main extends JFrame{
	
	public static File workdir = new File(String.valueOf(System.getenv("APPDATA")) + "/.honertis");
    public Auth auth;
    public static Main window = new Main();

    public static void main(String[] args) {
    	if (allowedOs()) {
    		if (!workdir.exists()) {
    			workdir.mkdirs();
    			System.out.println("created " + workdir);
    		} 
    		SwingUtilities.invokeLater(() -> {
    			window.setVisible(true);
    		});
    	} else {
    		JOptionPane.showMessageDialog(null, "Votre OS n'est pas supporté", "Erreur", 0);
    		System.exit(0);
    	} 
    }
    
    public Main() {

        // Configuration de la fenêtre
        setTitle("Honertis Launcher 1.0");
        setSize(500, 250);
        setLocationRelativeTo(null); // Centrer la fenêtre
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        try {
        	setIconImage(ImageIO.read(Main.class.getResource("/assets/icon32.png")).getScaledInstance(32, 32, 0));
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
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
        btnAction.setToolTipText("Connexion Microsoft");

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
        

        centerPanel.add(launchButton);
        JPanel bottomLinePanel = new JPanel();
        bottomLinePanel.setLayout(new BoxLayout(bottomLinePanel, BoxLayout.X_AXIS));
        bottomLinePanel.setBackground(new Color(34, 34, 34));
        
        String[] versions = getTagsVersions();
        JComboBox<String> versionCombo = new JComboBox<>(versions);
        versionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        versionCombo.setMaximumSize(new Dimension(120, 40));
        versionCombo.setPreferredSize(new Dimension(120, 40));
        versionCombo.setBackground(new Color(60, 60, 60));
        versionCombo.setForeground(Color.WHITE);
        versionCombo.setFocusable(false);
        
        bottomLinePanel.add(launchButton);
        bottomLinePanel.add(Box.createRigidArea(new Dimension(20, 0))); 
        bottomLinePanel.add(versionCombo);
        centerPanel.add(bottomLinePanel);
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        // Action du bouton ⋮
        btnAction.addActionListener(e -> {
        	try {
				authenticator.loginWithAsyncWebview().thenAccept(result -> 
				{
					if (auth != null)
						auth = new Auth(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), true);
					else {
						JOptionPane.showMessageDialog(null, "Veuillez réexecuter le launcher pour executer la connexion microsoft de nouveau.", "Info", 1);
					}
					}).exceptionally(ex -> {
						ex.printStackTrace();
						return null;
					});
		        } catch (Error exc) {
        		JOptionPane.showMessageDialog(null, exc.getStackTrace());
        	}
        });
        launchButton.addActionListener(e -> {
        	if (auth == null) {
	            String username = name.getText().trim();
	            if (username.isEmpty() || !Utils.isValidMinecraftUsername(username)) {
	                JOptionPane.showMessageDialog(null, "Veuillez entrer un nom d'utilisateur valide.\nOu authentifiez vous avec votre compte microsoft.");
	                return;
	            } else {
	                auth = new Auth(username, "", "", false);
	            }
        	}
            if (auth != null) {
                String selectedVersion = (String) versionCombo.getSelectedItem();
                try {
					launch(auth, selectedVersion);
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				} 
            }
        });
    }
    
    public static void downloadLibraries() throws Exception {
        File jsonFile = new File(workdir, "versions/1.8.8/1.8.8.json");
        if (!jsonFile.exists()) throw new FileNotFoundException("1.8.8.json manquant");

        JSONObject root = new JSONObject(new String(Files.readAllBytes(jsonFile.toPath()), StandardCharsets.UTF_8));
        JSONArray libraries = root.getJSONArray("libraries");

        File libDir = new File(workdir, "lib");
        if (!libDir.exists()) libDir.mkdirs();

        for (int i = 0; i < libraries.length(); i++) {
            JSONObject lib = libraries.getJSONObject(i);
            String name = lib.getString("name");

            // Ex: "com.google.code.gson:gson:2.2.4"
            String[] parts = name.split(":");
            if (parts.length < 3) continue;

            String group = parts[0].replace('.', '/');
            String artifact = parts[1];
            String version = parts[2];

            String path = group + "/" + artifact + "/" + version + "/" + artifact + "-" + version + ".jar";
            String downloadUrl = "https://libraries.minecraft.net/" + path;

            File dest = new File(libDir, artifact + "-" + version + ".jar");
            if (!dest.exists()) {
                try (InputStream in = new URL(downloadUrl).openStream()) {
                    Files.copy(in, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("[OK] Lib téléchargée : " + artifact);
                } catch (Exception e) {
                    System.err.println("[Erreur] Échec lib : " + artifact);
                }
            }
        }
    }
    public static void downloadAssets() throws Exception {
        File jsonFile = new File(workdir, "versions/1.8.8/1.8.8.json");
        JSONObject root = new JSONObject(new String(Files.readAllBytes(jsonFile.toPath()), StandardCharsets.UTF_8));
        String assetIndexName = root.getString("assets");

        // Télécharger l’index
        String assetIndexUrl = "https://pixelpc.fr/honertis/" + assetIndexName + ".json";
        File indexFile = new File(workdir, "assets/indexes/" + assetIndexName + ".json");
        if (!indexFile.exists()) {
            indexFile.getParentFile().mkdirs();
            try (InputStream in = new URL(assetIndexUrl).openStream()) {
                Files.copy(in, indexFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[OK] Index assets téléchargé.");
            }
        }

        JSONObject index = new JSONObject(new String(Files.readAllBytes(indexFile.toPath()), StandardCharsets.UTF_8)).getJSONObject("objects");
        File objectDir = new File(workdir, "assets/objects");

        for (String key : index.keySet()) {
            JSONObject entry = index.getJSONObject(key);
            String hash = entry.getString("hash");
            String subDir = hash.substring(0, 2);
            String url = "https://resources.download.minecraft.net/" + subDir + "/" + hash;

            File assetFile = new File(objectDir, subDir + "/" + hash);
            if (!assetFile.exists()) {
                assetFile.getParentFile().mkdirs();
                try (InputStream in = new URL(url).openStream()) {
                    Files.copy(in, assetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("[OK] Asset téléchargé : " + key);
                } catch (Exception e) {
                    System.err.println("[Erreur] Asset : " + key);
                }
            }
        }
    }
	private static boolean allowedOs() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win"))
          return true; 
        return false;
      }
    
    
    String owner = "TheAltening156";  // Replace with repo owner
    String repo = "Honertis";   
    private String buildClasspath(String selectedVersion) {
        File libDir = new File(workdir, "lib");
        if (!libDir.exists()) libDir.mkdirs();
        
        try(InputStream in = new URL("https://github.com/" + owner + "/" + repo +"/releases/download/" + selectedVersion + "/Honertis." + selectedVersion + ".zip").openStream()) {
        	Files.copy(in, Paths.get(workdir.getAbsolutePath() + "/Honertis" + selectedVersion + ".zip"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
			e.printStackTrace();
		}
        
        try {
			extractSpecificFileFromZip(workdir.getAbsolutePath() + "/Honertis" + selectedVersion + ".zip", "Honertis " + selectedVersion + "/" + "Honertis " + selectedVersion + ".jar", libDir.getAbsolutePath() + "/Honertis " + selectedVersion + ".jar");
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        File[] files = libDir.listFiles((dir, name) -> name.endsWith(".jar"));

        ArrayList<String> classpathElements = new ArrayList<>();

        for (File file : files) {
            String name = file.getName();

            // Inclure les libs communes (ne commencent pas par "Honertis-")
            boolean isCommon = !name.startsWith("Honertis ");

            // Inclure les .jar de la version sélectionnée
            boolean isCorrectVersion = name.contains("Honertis " + selectedVersion);

            if (isCommon || isCorrectVersion) {
                classpathElements.add(file.getAbsolutePath());
            }
        }

        return String.join(File.pathSeparator, classpathElements);
    }
    
    public static void extractSpecificFileFromZip(String zipFilePath, String fileToExtract, String outputFilePath) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals(fileToExtract)) {
                    // Crée le fichier de sortie
                    try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    zis.closeEntry();
                    return;
                }
                zis.closeEntry();
            }
            throw new FileNotFoundException("Fichier " + fileToExtract + " non trouvé dans le ZIP.");
        }
    }
    
    private void launch(Auth auth, String version) throws IOException, InterruptedException {
    	
    	File json = new File(workdir, "versions/1.8.8");
		Utils.download("https://pixelpc.fr/honertis/1.8.8.json", json, "1.8.8.json");
		try {
			downloadLibraries();
			downloadAssets();
			dlNatives();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
		window.setVisible(false);
		
    	System.out.println("[Launcher] Version sélectionnée : " + version);
    	String classpath = buildClasspath(version);
    	System.out.println("[Launcher] Classpath utilisé : " + classpath);

        ProcessBuilder builder = new ProcessBuilder(
            "java", 
            "-Djava.library.path=" + workdir.getPath() + "/natives", 
            "-cp", classpath, "net.minecraft.client.main.Main", 
            "--gameDir", workdir.getPath(), 
            "--assetsDir", "assets", 
            "--assetIndex", "1.8", 
            "--accessToken", auth.getAccessToken(), 
            "--username", auth.getUsername(), 
            "--uuid", auth.getUuid(), 
            "--version", "1.8.8", 
            "--userProperties", "{}"
        );

        builder.directory(workdir);
        builder.inheritIO();
        Process process = builder.start();
        process.waitFor();
        window.setVisible(true);
	}
    private String[] getTagsVersions() {

         try {
             String urlString = String.format("https://api.github.com/repos/%s/%s/tags", owner, repo);
             URL url = new URL(urlString);
             HttpURLConnection conn = (HttpURLConnection) url.openConnection();

             conn.setRequestMethod("GET");
             conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

             int responseCode = conn.getResponseCode();
             if (responseCode == 200) {
                 BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                 StringBuilder content = new StringBuilder();
                 String inputLine;

                 while ((inputLine = in.readLine()) != null) {
                     content.append(inputLine);
                 }
                 in.close();
                 conn.disconnect();

                 // Parse JSON response to extract tag names
                 JsonArray tagsArray = JsonParser.parseString(content.toString()).getAsJsonArray();
                 ArrayList<String> tagNames = new ArrayList<>();

                 for (JsonElement tagElement : tagsArray) {
                     String tagName = tagElement.getAsJsonObject().get("name").getAsString();
                     if (tagName.contains("Honertis")) continue;// Optional filter
                     if (tagName.equals("1.3")) continue;
                     tagNames.add(tagName);
                 }

                 // Convert List to String[]
                 return tagNames.toArray(new String[0]);
             } else {
                 System.out.println("Failed to fetch tags. Response code: " + responseCode);
             }
         } catch (Exception e) {
             e.printStackTrace();
         }

         return new String[0]; // Return empty array instead of null for safety
     }
    public void dlNatives() {
    	String[] nativeFiles = {
                "lwjgl.dll",
                "lwjgl64.dll",
                "OpenAL32.dll",
                "OpenAL64.dll"
            };

            String baseUrl = "https://pixelpc.fr/honertis/natives/";
            String outputDir = workdir.getAbsolutePath() + "/natives";

            new File(outputDir).mkdirs();

            for (String fileName : nativeFiles) {
                try {
                    String fileUrl = baseUrl + fileName;
                    Path outputPath = Paths.get(outputDir, fileName);

                    // Télécharger
                    try (InputStream in = new URL(fileUrl).openStream()) {
                        Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println(fileName + " telechargé.");
                    }
                } catch (IOException e) {
                    System.err.println("Erreur téléchargement de " + fileName + ": " + e.getMessage());
                }
            }
    }
}
