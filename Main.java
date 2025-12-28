
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

@SuppressWarnings("serial")
public class Main extends JFrame{
	public static File workdir = new File(Utils.getAppData() + "\\HonertisLauncher");
	public File jarDir = new File(workdir, "Launcher.jar");
    private JLabel currentText;
    private JProgressBar progressBar;


    public Main() {
    	if (!workdir.exists()) workdir.mkdirs();
        setTitle("Installateur du launcher Honertis");
        setSize(550, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);

        // Fond dégradé
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(25, 25, 25),
                                                     0, getHeight(), new Color(25, 25, 25));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        // Titre
        JLabel title = new JLabel("Honertis Launcher Installer", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        mainPanel.add(title, BorderLayout.NORTH);

        // Chemin
        JPanel pathPanel = new JPanel(new BorderLayout(5, 5));
        pathPanel.setOpaque(false);
        
        currentText = new JLabel("Installation du launcher...", SwingConstants.CENTER);
        currentText.setFont(new Font("Arial", Font.BOLD, 22));
        currentText.setForeground(Color.WHITE);
        mainPanel.add(currentText);
        pathPanel.add(currentText, BorderLayout.CENTER);
        mainPanel.add(pathPanel, BorderLayout.CENTER);

        // Installation
        JPanel installPanel = new JPanel(new BorderLayout(5, 5));
        installPanel.setOpaque(false);
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(60, 179, 113));
        installPanel.add(progressBar, BorderLayout.SOUTH);
        mainPanel.add(installPanel, BorderLayout.SOUTH);
        
        new Thread(() -> {
        	try {
        		currentText.setText("Téléchargement et Installation du Launcher...");
        		setProgress(25);
				Utils.download("https://github.com/TheAltening156/LauncherJar/releases/latest/download/HonertisLauncher.jar", jarDir);
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, "Impossible d'installer Launcher.jar", "Une erreur est survenue", JOptionPane.ERROR_MESSAGE);
				JOptionPane.showMessageDialog(this, e1.getMessage());
				System.exit(0);
			}
        	try {
        		setProgress(50);
        		currentText.setText("Installation des fichiers requis...");
        		CefBootstrap.download();
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, "Impossible d'installer les fichiers requis.", "Une erreur est survenue", JOptionPane.ERROR_MESSAGE);
				JOptionPane.showMessageDialog(this, e1.getMessage());
				System.exit(0);
			}
        	try {
        		setProgress(75);
        		currentText.setText("Extraction des fichiers requis...");
        		CefBootstrap.extract();
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, "Impossible d'extraire les fichiers requis.", "Une erreur est survenue", JOptionPane.ERROR_MESSAGE);
				JOptionPane.showMessageDialog(this, e1.getMessage());
				System.exit(0);
			}
        	try {
        		setProgress(100);
        		currentText.setText("Lancement du launcher...");
        		startLauncher();
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, "Impossible d'extraire les fichiers requis.", "Une erreur est survenue", JOptionPane.ERROR_MESSAGE);
				JOptionPane.showMessageDialog(this, e1.getMessage());
				System.exit(0);
			}
            
        }).start();
    }
    
    private void startLauncher() throws IOException, InterruptedException{
    	setVisible(false);
		ProcessBuilder builder = new ProcessBuilder(
				"java",
				"-Djava.library.path=" + getJcefDir(),
				"-jar",
				"Launcher.jar");
		builder.directory(workdir);
        builder.inheritIO();
        Process process= null;
    	process = builder.start();
		process.waitFor();
		System.exit(0);
	}

	public void setProgress(int progress) {
    	if (progress <= 100) 
    		SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
	}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
    
    private static File getJcefDir() {
        return new File(workdir, "jcef");
    }
	public static class CefBootstrap {

	    private static String getCefUrl() {
	        switch (Utils.getOSType()) {
	            case WINDOWS:
	                return "https://github.com/jcefmaven/jcefmaven/releases/download/141.0.10/jcef-natives-windows-amd64-jcef-2caef5a+cef-141.0.10+g1d65b0d+chromium-141.0.7390.123.jar";
	            case LINUX:
	                return "https://github.com/jcefmaven/jcefmaven/releases/download/141.0.10/jcef-natives-linux-amd64-jcef-2caef5a+cef-141.0.10+g1d65b0d+chromium-141.0.7390.123.jar";
	            case MACOS:
	                return "https://github.com/jcefmaven/jcefmaven/releases/download/141.0.10/jcef-natives-macosx-amd64-jcef-2caef5a+cef-141.0.10+g1d65b0d+chromium-141.0.7390.123.jar";
	            default:
	                throw new RuntimeException("Unsupported OS");
	        }
	    }
	    public static File baseDir = getJcefDir();
	    public static File jarFile = new File(baseDir, "jcef-natives.jar");
        public static File tarGzFile = new File(baseDir, "jcef-natives.tar.gz");
        public static File tarFile = new File(baseDir, "jcef-natives.tar");

	    public static void download() throws IOException{
            baseDir.mkdirs();

            if (!jarFile.exists()) {
                System.out.println("[JCEF] Downloading natives...");
                Utils.download(getCefUrl(), jarFile);
            }
	    }
	    
	    public static void extract() throws IOException{
	    	 if (!tarGzFile.exists()) {
	             extractTarGzFromJar(jarFile, tarGzFile);
	         }

	         if (!tarFile.exists()) {
	             gunzip(tarGzFile, tarFile);
	         }

	         extractTar(tarFile, baseDir);
		}
	    private static void extractTarGzFromJar(File jar, File outTarGz) throws IOException {
	        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(jar))) {
	            ZipEntry entry;
	            while ((entry = zip.getNextEntry()) != null) {
	                if (entry.getName().endsWith(".tar.gz") || entry.getName().endsWith(".tgz")) {
	                    try (FileOutputStream fos = new FileOutputStream(outTarGz)) {
	                        byte[] buffer = new byte[8192];
	                        int len;
	                        while ((len = zip.read(buffer)) > 0) {
	                            fos.write(buffer, 0, len);
	                        }
	                    }
	                    return;
	                }
	            }
	        }
	        throw new IOException("No .tar.gz found in JCEF jar");
	    }
	    
	    private static void gunzip(File gzip, File tar) throws IOException {
	        try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(gzip));
	             FileOutputStream fos = new FileOutputStream(tar)) {

	            byte[] buffer = new byte[8192];
	            int len;
	            while ((len = gis.read(buffer)) > 0) {
	                fos.write(buffer, 0, len);
	            }
	        }
	    }
	    
	    @SuppressWarnings("deprecation")
		private static void extractTar(File tarFile, File outDir) throws IOException {
	        try (TarArchiveInputStream tis =
	                     new TarArchiveInputStream(new FileInputStream(tarFile))) {

	            TarArchiveEntry entry;
	            while ((entry = tis.getNextTarEntry()) != null) {
	                File out = new File(outDir, entry.getName());

	                if (entry.isDirectory()) {
	                    out.mkdirs();
	                } else {
	                    out.getParentFile().mkdirs();
	                    try (FileOutputStream fos = new FileOutputStream(out)) {
	                        byte[] buffer = new byte[8192];
	                        int len;
	                        while ((len = tis.read(buffer)) > 0) {
	                            fos.write(buffer, 0, len);
	                        }
	                    }
	                }
	            }
	        }
	    }
	}
}
