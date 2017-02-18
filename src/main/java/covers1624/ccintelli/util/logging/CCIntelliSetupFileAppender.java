package covers1624.ccintelli.util.logging;

import covers1624.ccintelli.launch.Launch;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender;
import org.apache.logging.log4j.core.appender.rolling.*;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.util.Booleans;
import org.apache.logging.log4j.core.util.Integers;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Deflater;

/**
 * Created by covers1624 on 18/02/2017.
 * Mainly just a carbon copy of RollingRandomAccessFileAppender, Credits to apache.
 */
@Plugin (name = "CCIntelliFileAppender", category = "Core", elementType = "appender", printObject = true)
public class CCIntelliSetupFileAppender extends AbstractOutputStreamAppender<RollingFileManager> {

    private static final long serialVersionUID = 1L;

    private final String fileName;
    private final String filePattern;
    private Object advertisement;
    private final Advertiser advertiser;

    public CCIntelliSetupFileAppender(final String name, final Layout<? extends Serializable> layout, final Filter filter, final RollingFileManager manager, final String fileName, final String filePattern, final boolean ignoreExceptions, final boolean immediateFlush, final int bufferSize, final Advertiser advertiser) {
        super(name, layout, filter, ignoreExceptions, immediateFlush, manager);
        if (advertiser != null) {
            final Map<String, String> configuration = new HashMap<>(layout.getContentFormat());
            configuration.put("contentType", layout.getContentType());
            configuration.put("name", name);
            advertisement = advertiser.advertise(configuration);
        }
        this.fileName = fileName;
        this.filePattern = filePattern;
        this.advertiser = advertiser;
    }

    @Override
    public void stop() {
        super.stop();
        if (advertiser != null) {
            advertiser.unadvertise(advertisement);
        }
    }

    @Override
    public void append(final LogEvent event) {
        final RollingRandomAccessFileManager manager = (RollingRandomAccessFileManager) getManager();
        manager.checkRollover(event);

        // Leverage the nice batching behaviour of async Loggers/Appenders:
        // we can signal the file manager that it needs to flush the buffer
        // to disk at the end of a batch.
        // From a user's point of view, this means that all log events are
        // _always_ available in the log file, without incurring the overhead
        // of immediateFlush=true.
        manager.setEndOfBatch(event.isEndOfBatch());
        super.append(event);
    }

    /**
     * Returns the File name for the Appender.
     *
     * @return The file name.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns the file pattern used when rolling over.
     *
     * @return The file pattern.
     */
    public String getFilePattern() {
        return filePattern;
    }

    /**
     * Returns the size of the file manager's buffer.
     *
     * @return the buffer size
     */
    public int getBufferSize() {
        return getManager().getBufferSize();
    }

    /**
     * Create a RollingRandomAccessFileAppender.
     *
     * @param fileName       The name of the file that is actively written to.
     *                       (required).
     * @param filePattern    The pattern of the file name to use on rollover.
     *                       (required).
     * @param append         If true, events are appended to the file. If false, the
     *                       file is overwritten when opened. Defaults to "true"
     * @param name           The name of the Appender (required).
     * @param immediateFlush When true, events are immediately flushed. Defaults
     *                       to "true".
     * @param bufferSizeStr  The buffer size, defaults to {@value RollingRandomAccessFileManager#DEFAULT_BUFFER_SIZE}.
     * @param policy         The triggering policy. (required).
     * @param strategy       The rollover strategy. Defaults to
     *                       DefaultRolloverStrategy.
     * @param layout         The layout to use (defaults to the default PatternLayout).
     * @param filter         The Filter or null.
     * @param ignore         If {@code "true"} (default) exceptions encountered when appending events are logged; otherwise
     *                       they are propagated to the caller.
     * @param advertise      "true" if the appender configuration should be
     *                       advertised, "false" otherwise.
     * @param advertiseURI   The advertised URI which can be used to retrieve the
     *                       file contents.
     * @param config         The Configuration.
     * @return A RollingRandomAccessFileAppender.
     */
    @PluginFactory
    public static CCIntelliSetupFileAppender createAppender(@PluginAttribute ("fileName") String fileName, @PluginAttribute ("filePattern") String filePattern, @PluginAttribute ("append") String append, @PluginAttribute ("name") String name, @PluginAttribute ("immediateFlush") String immediateFlush, @PluginAttribute ("bufferSize") String bufferSizeStr, @PluginElement ("Policy") TriggeringPolicy policy, @PluginElement ("Strategy") RolloverStrategy strategy, @PluginElement ("Layout") Layout<? extends Serializable> layout, @PluginElement ("Filter") Filter filter, @PluginAttribute ("ignoreExceptions") String ignore, @PluginAttribute ("advertise") String advertise, @PluginAttribute ("advertiseURI") String advertiseURI, @PluginConfiguration Configuration config) {

        final boolean isAppend = Booleans.parseBoolean(append, true);
        final boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
        final boolean isFlush = Booleans.parseBoolean(immediateFlush, true);
        final boolean isAdvertise = Boolean.parseBoolean(advertise);
        final int bufferSize = Integers.parseInt(bufferSizeStr, RollingRandomAccessFileManager.DEFAULT_BUFFER_SIZE);

        if (name == null) {
            LOGGER.error("No name provided for FileAppender");
            return null;
        }

        if (fileName == null) {
            LOGGER.error("No filename was provided for FileAppender with name " + name);
            return null;
        }

        if (filePattern == null) {
            LOGGER.error("No filename pattern provided for FileAppender with name " + name);
            return null;
        }

        if (policy == null) {
            LOGGER.error("A TriggeringPolicy must be provided");
            return null;
        }
        fileName = Launch.WORKING_DIR.getAbsolutePath() + "/" + fileName;
        filePattern = Launch.WORKING_DIR.getAbsolutePath() + "/" + filePattern;
        if (strategy == null) {
            strategy = DefaultRolloverStrategy.createStrategy(null, null, null, String.valueOf(Deflater.DEFAULT_COMPRESSION), null, true, config);
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        final RollingRandomAccessFileManager manager = RollingRandomAccessFileManager.getRollingRandomAccessFileManager(fileName, filePattern, isAppend, isFlush, bufferSize, policy, strategy, advertiseURI, layout);
        if (manager == null) {
            return null;
        }

        return new CCIntelliSetupFileAppender(name, layout, filter, manager, fileName, filePattern, ignoreExceptions, isFlush, bufferSize, isAdvertise ? config.getAdvertiser() : null);
    }

}
