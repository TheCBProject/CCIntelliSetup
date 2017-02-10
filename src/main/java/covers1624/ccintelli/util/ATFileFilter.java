package covers1624.ccintelli.util;

import org.apache.commons.io.filefilter.AbstractFileFilter;

import java.io.File;

/**
 * Created by covers1624 on 11/02/2017.
 */
public class ATFileFilter extends AbstractFileFilter {
    //TODO, Actually regex the file to see if it is an AT file.
    @Override
    public boolean accept(File file) {
        return file.isFile() && file.getAbsolutePath().endsWith("_at.cfg");
    }
}
