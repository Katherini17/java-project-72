package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jsoup.nodes.Document;

@Slf4j
public class AppTest {
    private Javalin app;
    private static MockWebServer mockServer;
    private static final String TEST_HTML_FILE_NAME = "index.html";

    @BeforeAll
    public static void setUpMockServer() throws IOException {
        log.info("Setting up mock server");

        mockServer = new MockWebServer();
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(readFixtures(TEST_HTML_FILE_NAME)));

        mockServer.start();

        log.info("Mock server started");
    }

    @AfterAll
    public static void tearDownMockServer() throws IOException {
        log.info("Shutting down mock server");

        mockServer.shutdown();

        log.info("Mock server stopped");
    }

    @BeforeEach
    public final void setUpApp() throws SQLException, IOException {
        log.info("Setting up application before test");

        App.setupAppEnviroment();
        app = App.getApp(true);

        log.info("Clearing repositories");

        UrlChecksRepository.removeAll();
        UrlsRepository.removeAll();

        log.info("Setup complete");
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get(NamedRoutes.rootPath());

            log.info("Test main page, response status: {}", response.code());

            assertEquals(200, response.code());
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get(NamedRoutes.urlsPath());

            log.info("Test urls page, response status: {}", response.code());

            assertEquals(200, response.code());
        });
    }

    @Test
    public void testCreateUrlSuccessfully() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=https://ru.hexlet.io/courses";
            String normalizedUrl = "https://ru.hexlet.io";

            Url url = new Url(normalizedUrl);
            UrlsRepository.save(url);

            UrlCheck urlCheck = new UrlCheck(url.getId());
            urlCheck.setStatusCode(200);
            UrlChecksRepository.save(urlCheck);

            try (Response response = client.post(NamedRoutes.urlsPath(), requestBody)) {
                Document document = Jsoup.parse(response.body().string());
                Elements tds = document.select(String.format("tr:has(td:has(a:matchesOwn(%s)))",
                                Pattern.quote(normalizedUrl))
                                )
                                .select("td");
                log.info("Test createUrl url, response status: {}", response.code());

                assertEquals(String.valueOf(url.getId()), tds.getFirst().text());
                assertTrue(tds.get(1).text().contains(normalizedUrl));
                assertEquals(getNormalizedDate(urlCheck.getCreatedAt()), tds.get(2).text());
                assertEquals(String.valueOf(urlCheck.getStatusCode()), tds.get(3).text());

                assertTrue(UrlsRepository.existsByName(normalizedUrl));
                assertEquals(200, response.code());
            }
        });
    }

    @Test
    public void testCreateExistingUrl() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=https://ru.hexlet.io/courses";

            try (Response response = client.post(NamedRoutes.urlsPath(), requestBody)) {
                log.info("Test createUrl existing url, response status: {}", response.code());

                assertEquals(200, response.code());
            }
        });
    }

    @Test
    public void testCreateInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=12345";

            try (Response response = client.post(NamedRoutes.urlsPath(), requestBody)) {
                log.info("Test createUrl invalid url, response status: {}", response.code());

                assertEquals(200, response.code());
            }
        });
    }

    @Test
    public void testShowUrl() {
        JavalinTest.test(app, (server, client) -> {

            Url url = new Url("https://ru.hexlet.io");
            UrlsRepository.save(url);
            Long urlId = url.getId();
            String urlCreatedAt = getNormalizedDate(url.getCreatedAt());

            UrlCheck urlCheck1 = new UrlCheck(urlId);
            UrlChecksRepository.save(urlCheck1);
            String urlCheck1CreatedAt = getNormalizedDate(urlCheck1.getCreatedAt());

            UrlCheck urlCheck2 = new UrlCheck(urlId);
            UrlChecksRepository.save(urlCheck2);
            String urlCheck2CreatedAt = getNormalizedDate(urlCheck2.getCreatedAt());

            try (Response response = client.get(NamedRoutes.urlPath(urlId))) {
                log.info("Test show url, response status: {}", response.code());

                assertEquals(200, response.code());

                Document document = Jsoup.parse(response.body().string());
                Elements liTags = document.select("li");

                assertTrue(liTags.text().contains(urlCreatedAt));
                assertTrue(liTags.text().contains(String.valueOf(urlId)));

                Elements rows = document.select("tr");

                Elements tds1 = rows.selectFirst(
                        String.format("tr:has(td:first-child:matchesOwn(^%d$))", urlCheck1.getId())
                        )
                        .select("td");

                assertEquals(String.valueOf(urlCheck1.getId()), tds1.get(0).text());
                assertEquals(urlCheck1CreatedAt, tds1.get(5).text());

                Elements tds2 = rows.selectFirst(
                        String.format("tr:has(td:first-child:matchesOwn(^%d$))", urlCheck2.getId())
                        )
                        .select("td");

                assertEquals(String.valueOf(urlCheck2.getId()), tds2.get(0).text());
                assertEquals(urlCheck2CreatedAt, tds2.get(5).text());
            }
        });
    }

    @Test
    public void testCheckUrl() {
        JavalinTest.test(app, (server, client) -> {

            String testUrlStr = mockServer.url("/").toString();
            Url testUrlObj = new Url(testUrlStr);
            UrlsRepository.save(testUrlObj);

            try (Response response = client.post(NamedRoutes.urlCheckPath(testUrlObj.getId()))) {
                log.info("Test create url, response status: {}", response.code());

                assertEquals(200, response.code());

                UrlCheck urlCheck = UrlChecksRepository.findChecksByUrlId(1L).getLast();

                Document document = Jsoup.parse(response.body().string());
                Elements rows = document.select(
                        String.format("tr:has(td:first-child:matchesOwn(^%d$))", urlCheck.getId())
                );

                Elements tds = rows.getFirst().select("td");

                assertEquals("200", tds.get(1).text());
                assertEquals("Test title", tds.get(2).text());
                assertEquals("Test h1", tds.get(3).text());
                assertEquals("Test description", tds.get(4).text());
                assertEquals(getNormalizedDate(urlCheck.getCreatedAt()), tds.get(5).text());
            }
        });
    }

    private static Path getFixturePath(String fileName) {
        return Paths.get("src", "test", "resources", "fixtures", fileName)
                .toAbsolutePath()
                .normalize();
    }

    private static String readFixtures(String fileName) throws IOException {
        Path filePath = getFixturePath(fileName);
        return Files.readString(filePath).trim();
    }

    private static String getNormalizedDate(Instant instant) {
        return instant
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }
}
