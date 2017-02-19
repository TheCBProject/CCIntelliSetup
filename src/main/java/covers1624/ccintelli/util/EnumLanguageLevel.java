package covers1624.ccintelli.util;

/**
 * Created by covers1624 on 17/02/2017.
 */
public enum EnumLanguageLevel {

    JDK_1_6("1.6", "JDK_1_6"),
    JDK_1_7("1.7", "JDK_1_7"),
    JDK_1_8("1.8", "JDK_1_8");

    private final String GUI_NAME;
    private final String XML_NAME;

    EnumLanguageLevel(String guiName, String xmlName) {
        this.GUI_NAME = guiName;
        this.XML_NAME = xmlName;
    }

    public String getGuiName() {
        return GUI_NAME;
    }

    public String getXMLName() {
        return XML_NAME;
    }

    public String getBytecodeTarget() {
        return GUI_NAME;
    }

    public static EnumLanguageLevel fromName(String name) {
        for (EnumLanguageLevel level : values()) {
            if (level.GUI_NAME.equals(name) || level.XML_NAME.equals(name)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown EnumLanguageLevel: " + name);
    }
}
