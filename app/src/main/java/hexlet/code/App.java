package hexlet.code;

import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlsController;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;

import hexlet.code.repository.BaseRepository;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public class App {
    private static final String DEFAULT_PORT = "7070";
    private static final String DEFAULT_DATABASE_URL = "jdbc:h2:mem:project";
    private static final String DEFAULT_APP_ENV = "development";

    public static void main(String[] args) throws IOException, SQLException {
        log.info("Starting application...");
        setupAppEnviroment();
        Javalin app = getApp();

        app.get(NamedRoutes.rootPath(), RootController::root);
        app.post(NamedRoutes.urlsPath(), UrlsController::create);
        app.get(NamedRoutes.urlsPath(), UrlsController::index);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::show);

        app.start(getPort());
        log.info("Application started with APP_ENV: {}, port: {}", getAppEnv(), getPort());
    }

    public static Javalin getApp() throws SQLException, IOException {
        return getApp(false);
    }
    public static Javalin getApp(boolean isTest) throws IOException, SQLException {
        log.info("Configuring database connection");
        HikariConfig hikariConfig =  new HikariConfig();
        String databaseUrl = isTest ? DEFAULT_DATABASE_URL : getDatabaseUrl();
        hikariConfig.setJdbcUrl(databaseUrl);

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        initializeDatabase(dataSource);

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

    public static String getAppEnv() {
        return System.getenv().getOrDefault("APP_ENV", DEFAULT_APP_ENV);
    }

    public static void setupAppEnviroment() {
        String appEnv = getAppEnv();
        System.setProperty("APP_ENV", appEnv);
    }

    public static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);

        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static String readResourceFile(String fileName) throws IOException, URISyntaxException {
        URL resourceUrl = App.class.getResource("/" + fileName);

        if (resourceUrl == null) {
            throw new IOException("Resource not found: " + fileName);
        }

        return Files.readString(Path.of(resourceUrl.toURI()), StandardCharsets.UTF_8);
    }

    public static void initializeDatabase(HikariDataSource dataSource) throws SQLException, IOException {
        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {

            String sql = readResourceFile("schema.sql");
            statement.execute(sql);
            log.info("Database initialized");
        } catch (URISyntaxException error) {
            log.error("Failed to read schema.sql due to invalid URI: {}", error.getMessage(), error);
        }
    }
}
