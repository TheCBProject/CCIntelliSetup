package covers1624.ccintelli.util;

import covers1624.ccintelli.launch.Launch;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;

/**
 * Created by covers1624 on 18/02/2017.
 */
@Plugin (name = "GuiAppender", category = "Core", elementType = "appender", printObject = true)
public class GuiLogAppender extends AbstractAppender {

    protected GuiLogAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @PluginFactory
    public static GuiLogAppender createAppender(@PluginAttribute ("name") String name, @PluginAttribute ("ignoreExceptions") String ignore, @PluginElement ("Layout") Layout<? extends Serializable> layout, @PluginElement ("Filters") Filter filter) {
        return new GuiLogAppender(name, filter, layout, Boolean.parseBoolean(ignore));
    }

    @Override
    public void append(LogEvent event) {
        Launch.console.println(getLayout().toSerializable(event).toString());
    }
}
