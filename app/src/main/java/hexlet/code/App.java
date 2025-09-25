package hexlet.code;

import io.javalin.Javalin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import hexlet.code.repository.BaseRepository;


public class App {
    private static final String DEFAULT_PORT = "7070";
    private static final String DEFAULT_DATABASE_URL = "jdbc:h2:mem:my_app";

    public static void main(String[] args) {
        Javalin app = getApp();

        app.get("/", ctx -> {
            ctx.result("Hello World!");
        });

        app.start(getPort());
    }

    public static Javalin getApp() {
        HikariConfig hikariConfig =  new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        BaseRepository.setDataSource(dataSource);
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });

        return app;
    }

    public static int getPort() {
        String port = System.getenv().getOrDefault("PORT", DEFAULT_PORT);
        return Integer.parseInt(port);
    }

    public static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", DEFAULT_DATABASE_URL);
    }
}
