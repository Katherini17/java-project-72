package hexlet.code;

import hexlet.code.controller.RootController;
import hexlet.code.controller.UrlsController;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

import static hexlet.code.App.getApp;
import static hexlet.code.App.setupAppEnviroment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jsoup.nodes.Document;

@Slf4j
public class AppTest {
    private Javalin app;

    @BeforeEach
    public final void setUp() throws SQLException, IOException {
        setupAppEnviroment();
        app = getApp(true);

        app.get(NamedRoutes.rootPath(), RootController::root);
        app.post(NamedRoutes.urlsPath(), UrlsController::create);
        app.get(NamedRoutes.urlsPath(), UrlsController::index);

        UrlsRepository.removeAll();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get(NamedRoutes.rootPath());
            log.debug("!!!! код ответа = " + response.code());
            assertEquals(200, response.code());
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get(NamedRoutes.urlsPath());
            assertEquals(200, response.code());
        });
    }

    @Test
    public void testCreateUrlSuccessfully() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=https://ru.hexlet.io/courses";
            String normalizedUrl = "https://ru.hexlet.io";
            Response response = client.post(NamedRoutes.urlsPath(), requestBody);
            Document document = Jsoup.parse(response.body().string());
            Elements links = document.select(String.format("a[href^='%s']", normalizedUrl));

            assertTrue(links.text().contains(normalizedUrl));
            assertTrue(UrlsRepository.existsByName(normalizedUrl));
            assertEquals(200, response.code());
        });
    }

    @Test
    public void testCreateExistedUrl() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=https://ru.hexlet.io/courses";

            client.post(NamedRoutes.urlsPath(), requestBody);
            Response response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertEquals(200, response.code());
        });
    }

    @Test
    public void testCreateInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=12345";

            client.post(NamedRoutes.urlsPath(), requestBody);
            Response response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertEquals(200, response.code());
        });
    }

}
