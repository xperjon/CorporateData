package se.blinfo.corporate.data.core.utils;

/**
 *
 * @author jel
 */
public class CinUtils {

    public static String formatCIN(String cin) {
        return cin.replaceAll("-", "");
    }
}
