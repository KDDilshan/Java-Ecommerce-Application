package product;

public class ServletUtils {
    public static double parseDouble(String param, double defaultValue) {
        try {
            return Double.parseDouble(param);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    public static int parseInt(String param, int defaultValue) {
        try {
            return Integer.parseInt(param);
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }
}
