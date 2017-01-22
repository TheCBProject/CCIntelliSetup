package covers1624.ccintelli.util.logger;

import covers1624.ccintelli.util.Utils;

import java.io.*;

/**
 * Simple rotatable log. Nothing to special.
 */
public class LogHelper {

	private static PrintWriter writer;

	public static void setLogFile(File logFile) {
		try {
			if (writer != null) {
				writer.close();
			}
			boolean append = true;
			File latest = getCacheFile(logFile, "latest");
			File one = getCacheFile(logFile, "1");
			File two = getCacheFile(logFile, "2");
			File three = getCacheFile(logFile, "3");

			Utils.archiveFile(two, three);
			Utils.archiveFile(one, two);
			Utils.archiveFile(latest, one);

			if (!latest.exists()) {
				Utils.tryCreateFile(latest);
			} else {
				append = false;
			}

			writer = new PrintWriter(new FileOutputStream(latest, append));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static File getCacheFile(File log, String level) {
		int lastDot = log.getName().lastIndexOf('.');
		String name = log.getName();
		String ext = log.getName().substring(lastDot);
		return new File(log.getAbsolutePath().replace(name, ""), name.substring(0, lastDot) + "-" + level + ext);
	}

    /**
     * Log with a supplied level.
     */
    public static void log(Level logLevel, Object object) {
    	String log = String.format("[%s]: %s", logLevel.name(), String.valueOf(object));
        System.out.println(log);
        if (writer != null) {
        	writer.println(log);
        	writer.flush();
        }
    }

    //Standard log entries.

    public static void error(Object object) {
        log(Level.ERROR, object);
    }

    public static void fatal(Object object) {
        log(Level.FATAL, object);
    }

    public static void info(Object object) {
        log(Level.INFO, object);
    }

    public static void warn(Object object) {
        log(Level.WARN, object);
    }

    public static void debug(Object object) {
        log(Level.DEBUG, object);
    }

    //log with format.

    public static void error(String object, Object... format) {
        log(Level.ERROR, String.format(object, format));
    }

    public static void fatal(String object, Object... format) {
        log(Level.FATAL, String.format(object, format));
    }

    public static void info(String object, Object... format) {
        log(Level.INFO, String.format(object, format));
    }

    public static void warn(String object, Object... format) {
        log(Level.WARN, String.format(object, format));
    }

    public static void debug(String object, Object... format) {
        log(Level.DEBUG, String.format(object, format));
    }

    public static void bigError(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        error("****************************************");
        error("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            error("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        error("****************************************");
    }

    public static void bigFatal(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        fatal("****************************************");
        fatal("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            fatal("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        fatal("****************************************");
    }

    public static void bigInfo(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        info("****************************************");
        info("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            info("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        info("****************************************");
    }

    public static void bigWarn(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        warn("****************************************");
        warn("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            warn("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        warn("****************************************");
    }

    public static void bigDebug(String format, Object... data) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        debug("****************************************");
        debug("* " + format, data);
        for (int i = 2; i < 8 && i < trace.length; i++) {
            debug("*  at %s%s", trace[i].toString(), i == 7 ? "..." : "");
        }
        debug("****************************************");
    }
}
