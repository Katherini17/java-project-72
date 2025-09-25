package hexlet.code;

import io.javalin.Javalin;

public class App {
    public static void main(String[] args) {
        Javalin app = getApp();

        app.get("/", ctx -> {
            ctx.result("Hello World!");
        });

        app.start(7070);
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });

        return app;
    }
}
