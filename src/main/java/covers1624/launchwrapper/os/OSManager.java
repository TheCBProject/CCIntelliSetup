package covers1624.launchwrapper.os;

/**
 * Created by covers1624 on 2/17/2016.
 */
public class OSManager {

	private static EnumOS currOs;

	static {
		String osType = System.getProperty("os.name").toLowerCase();
		if (osType.contains("win")) {
			currOs = EnumOS.WINDOWS;
		} else if (osType.contains("mac")) {
			currOs = EnumOS.OSX;
		} else if (osType.contains("solaris") || osType.contains("sunos")) {
			currOs = EnumOS.SOLARIS;
		} else if (osType.contains("linux") || osType.contains("unix")) {
			currOs = EnumOS.LINUX;
		} else {
			currOs = EnumOS.UNKNOWN;
		}

	}

	public static EnumOS getCurrentOS() {
		return currOs;
	}

	public static EnumOS parseFromString(String osType) {
		switch (osType) {
		case "windows":
			return EnumOS.WINDOWS;
		case "osx":
			return EnumOS.OSX;
		case "unix":
			return EnumOS.LINUX;
		default:
			return EnumOS.UNKNOWN;
		}
	}

	public enum EnumOS {
		LINUX,
		WINDOWS,
		OSX,
		SOLARIS,
		UNKNOWN;
	}

}
