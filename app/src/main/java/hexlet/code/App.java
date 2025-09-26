package hexlet.code;

import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;

import hexlet.code.repository.BaseRepository;

public class App {
    private static final String DEFAULT_PORT = "7070";
    private static final String DEFAULT_DATABASE_URL = "jdbc:h2:mem:project";

    public static void main(String[] args) {
        Javalin app = getApp();

        app.get("/", ctx -> {
            ctx.render("index.jte");
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
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
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

    public static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);

        return TemplateEngine.create(codeResolver, ContentType.Html);
    }
}
