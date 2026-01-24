
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Utils {

	public static boolean isValidMinecraftUsername(String username) {
		return username.matches("^[a-zA-Z0-9_]{3,16}$");
	}

	public enum EnumOS {
		WINDOWS, MACOS, LINUX, SOLARIS, UNKNOWN;
	}

	public static EnumOS getOSType() {
		String s = System.getProperty("os.name").toLowerCase();
		return s.contains("win") ? EnumOS.WINDOWS
				: (s.contains("mac") ? EnumOS.MACOS
						: (s.contains("solaris") ? EnumOS.SOLARIS
								: (s.contains("sunos") ? EnumOS.SOLARIS
										: (s.contains("linux") ? EnumOS.LINUX
												: (s.contains("unix") ? EnumOS.LINUX : EnumOS.UNKNOWN)))));
	}

	public static File getAppData() {
		EnumOS os = getOSType();
		if (os == EnumOS.WINDOWS)
			return new File(System.getenv("APPDATA"));
		if (os == EnumOS.MACOS)
			return new File(System.getProperty("user.home") + "/Library/Application Support");
		return new File(System.getProperty("user.home"));
	}
	
	public static void download(String fileUrl, File dest) throws IOException {
		Main.setProgress(0, " %", Main.stateBar);
		URL url = new URL(fileUrl);
		long fileSize = url.openConnection().getContentLengthLong();

		try (InputStream in = url.openStream(); FileOutputStream fos = new FileOutputStream(dest)) {

			byte[] buffer = new byte[1024];
			int count;
			long startTime = System.currentTimeMillis();
			long totalBytesRead = 0;
			long lastTime = startTime;
			long lastBytes = 0;
			while ((count = in.read(buffer, 0, 1024)) != -1) {
				fos.write(buffer, 0, count);
				totalBytesRead += count;

				long currentTime = System.currentTimeMillis();
				if (currentTime - lastTime >= 250) {
					long bytesInLastSec = totalBytesRead - lastBytes;
					Main.setProgress((totalBytesRead * 100) / fileSize, " % | " + getBytesPerSecString(bytesInLastSec * 4), Main.stateBar);
					lastTime = currentTime;
					lastBytes = totalBytesRead;
				}
			}
			Main.setProgress(100L, " %", Main.stateBar);
		}
	}

	public static String getBytesPerSecString(long bytesPerSecond) {
		double speed = bytesPerSecond;
		String unit = "B/s";

		if (speed >= 1024) {
			speed /= 1024;
			unit = "KB/s";
		}
		if (speed >= 1024) {
			speed /= 1024;
			unit = "MB/s";
		}
		if (speed >= 1024) {
			speed /= 1024;
			unit = "GB/s";
		}
		return String.format("(%.2f %s%n)", speed, unit);
	}

}
