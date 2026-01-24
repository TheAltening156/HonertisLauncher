
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
import java.io.InputStream;
import java.io.OutputStream;
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
	public static File workdir = new File(Utils.getAppData() + "/HonertisLauncher");
	public File jarDir = new File(workdir, "Launcher.jar");
    private JLabel currentText;
    public static JProgressBar progressBar;
	public static JProgressBar stateBar;

    public static void main(String[] args) {
    	if (!workdir.exists()) workdir.mkdirs();
    	SwingUtilities.invokeLater(() -> new Main().setVisible(true));
	}
    
    public Main() {
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
        
        stateBar = new JProgressBar();
        stateBar.setStringPainted(true);
        stateBar.setForeground(new Color(100,149,237));
        stateBar.setMaximum(100);
        installPanel.add(stateBar, BorderLayout.NORTH);
        
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(100,149,237));
        progressBar.setMaximum(4);
        installPanel.add(progressBar, BorderLayout.SOUTH);
        
        mainPanel.add(installPanel, BorderLayout.SOUTH);
        
        new Thread(() -> {
        	try {
        		currentText.setText("Téléchargement et Installation du Launcher...");
        		setProgress(1, " / 4", progressBar);
				Utils.download("https://github.com/TheAltening156/LauncherJar/releases/latest/download/HonertisLauncher.jar", jarDir);
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, "Impossible d'installer Launcher.jar. \nVérifiez votre connexion internet et réessayez.", "Une erreur est survenue", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
        	try {
        		setProgress(2, " / 4", progressBar);
        		currentText.setText("Installation des fichiers requis...");
        		CefBootstrap.download(false);
			} catch (IOException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, "Impossible d'installer les fichiers requis.  \nVérifiez votre connexion internet et réessayez.", "Une erreur est survenue", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
        	try {
        		setProgress(3, " / 4", progressBar);
        		currentText.setText("Extraction des fichiers...");
        		CefBootstrap.extract(false);
			} catch (IOException e1) {
				e1.printStackTrace();
				try {
	        		currentText.setText("Réinstallation des fichiers requis...");
	        		CefBootstrap.download(true);
				} catch (IOException e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(this, "Impossible d'installer les fichiers requis.  \nVérifiez votre connexion internet et réessayez.", "Une erreur est survenue", JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}
	        	try {
	        		currentText.setText("Extraction des fichiers...");
	        		CefBootstrap.extract(true);
				} catch (IOException e2) {
					e2.printStackTrace();
					JOptionPane.showMessageDialog(this, "Impossible d'extraire les fichiers requis.", "Une erreur est survenue", JOptionPane.ERROR_MESSAGE);
					JOptionPane.showMessageDialog(this, e2.getLocalizedMessage());
					System.exit(0);
				}
			}
        	try {
        		setProgress(4, " / 4", progressBar);
        		currentText.setText("Lancement du launcher...");
        		startLauncher();
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(this, "Impossible d'executer le launcher.", "Une erreur est survenue", JOptionPane.ERROR_MESSAGE);
				JOptionPane.showMessageDialog(this, e1.getLocalizedMessage());
				System.exit(0);
			}
            
        }).start();
    }
    
    public void createWindow() {}
    
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
    
	public static void setProgress(long progress, String text, JProgressBar progressBar) {
    	if (progress <= progressBar.getMaximum()) 
    		SwingUtilities.invokeLater(() -> { 
    			progressBar.setValue((int) progress);
    			progressBar.setString(text.contains("Extracting") ? text : (progress + text));
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

	    public static void download(boolean bruteforce) throws IOException{
            baseDir.mkdirs();

            if ((!jarFile.exists() && !bruteforce) || bruteforce) {
                System.out.println("[Launcher] " + (bruteforce ? "Red" : "D") + "ownloading JCEF natives...");
                Utils.download(getCefUrl(), jarFile);
            }
	    }
	    
	    public static void extract(boolean bruteforce) throws IOException{
	    	 if ((!tarGzFile.exists() && !bruteforce) || bruteforce) {
	    		 setProgress(0, " %", stateBar);
	    		 System.out.println("[Launcher] " + (bruteforce ? "Ree" : "E") + "xtracting " + tarGzFile);
	             extractTarGzFromJar(jarFile, tarGzFile);
	         }

	         if ((!tarFile.exists() && !bruteforce) || bruteforce) {
	        	 setProgress(0, " %", stateBar);
	        	 System.out.println("[Launcher] " + (bruteforce ? "Ree" : "E") + "xtracting " + tarFile);
	             gunzip(tarGzFile, tarFile);
	         }
	         setProgress(0, " %", stateBar);
	         extractTar(tarFile, baseDir);
		}
	    private static void extractTarGzFromJar(File jar, File outTarGz) throws IOException {
	        try (ZipInputStream zip = new ZipInputStream(new FileInputStream(jar))) {
	            ZipEntry entry;
	            while ((entry = zip.getNextEntry()) != null) {
	                if (entry.getName().endsWith(".tar.gz") || entry.getName().endsWith(".tgz")) {
	                    try (FileOutputStream fos = new FileOutputStream(outTarGz)) {
	                    	extractMethod(zip, fos);
	                    }
	                    return;
	                }
	            }
	        }
	        throw new IOException("No .tar.gz found in JCEF jar");
	    }
	    
	    private static void gunzip(File gzip, File tar) throws IOException {
	        extractMethod(new GZIPInputStream(new FileInputStream(gzip)), new FileOutputStream(tar));
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
	                        extractMethod(tis, fos);
	                    }
	                }
	            }
	        }
	    }
	    
	    private static void extractMethod(InputStream in, OutputStream out) throws IOException{
	    	byte[] buffer = new byte[8192];
            int len;

            long totalBytesRead = 0;
			long lastTime = System.currentTimeMillis();
			long lastBytes = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
                
                totalBytesRead += len;

				long currentTime = System.currentTimeMillis();
				if (currentTime - lastTime >= 250) {
					long bytesInLastSec = totalBytesRead - lastBytes;
					setProgress(0, "Extracting | " + Utils.getBytesPerSecString(bytesInLastSec * 4), Main.stateBar);
				
					lastTime = currentTime;
					lastBytes = totalBytesRead;
				}
            }
            setProgress(100, " %", stateBar);
	    }
	}
}
