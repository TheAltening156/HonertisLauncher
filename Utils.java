import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class Utils {
	public static boolean isValidMinecraftUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]{3,16}$");
    }

	public static void download(String url, File directory, String name) {
		try {
			saveFileFromUrlWithCommonsIO(directory.toString() + "/" + name, url);
			System.out.println("Finished downloading " + name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void saveFileFromUrlWithCommonsIO(String fileName, String fileUrl) throws MalformedURLException, IOException {
        FileUtils.copyURLToFile(new URL(fileUrl), new File(fileName));
    }
	
	public static boolean openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
			try {
				desktop.browse(uri);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		return false;
	}
}
