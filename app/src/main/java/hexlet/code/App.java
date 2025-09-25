package hexlet.code;

import io.javalin.Javalin;

public class App {
    private static final String DEFAULT_PORT = "7070";

    public static void main(String[] args) {
        Javalin app = getApp();

        app.get("/", ctx -> {
            ctx.result("Hello World!");
        });

        app.start(getPort());
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });

        return app;
    }

    public static int getPort() {
        String port = System.getenv().getOrDefault("PORT", DEFAULT_PORT);
        return Integer.parseInt(port);
    }
}
