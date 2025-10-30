package hexlet.code.util;

public class NamedRoutes {
    private static final String ROOT_PATH = "/";
    private static final String URLS_PATH = "/urls";

    public static String rootPath() {
        return ROOT_PATH;
    }

    public static String urlsPath() {
        return URLS_PATH;
    }

    public static String urlPath(String id) {
        return String.format("%s/%s", URLS_PATH, id);
    }

    public static String urlPath(Long id) {
        return urlPath(String.valueOf(id));
    }

    public static String urlCheckPath(String id) {
        return String.format("%s/check", urlPath(id));
    }

    public static String getUrlsPath(Long id) {
        return urlCheckPath(String.valueOf(id));
    }
}
