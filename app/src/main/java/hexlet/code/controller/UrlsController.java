package hexlet.code.controller;

import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static io.javalin.rendering.template.TemplateUtil.model;

@Slf4j
public class UrlsController {

    private static final String INVALID_URL_FLASH_MESSAGE = "Некорректный URL";
    private static final String EXISTING_URL_FLASH_MESSAGE = "Страница уже существует";
    private static final String SUCCESSFULLY_ADDED_URL_FLASH_MESSAGE = "Страница успешно добавлена";
    private static final String UNSUCCESSFULLY_ADDED_URL_FLASH_MESSAGE = "Возникла ошибка при добавлении страницы";

    private static final String SUCCESSFULLY_CHECKED_URL_FLASH_MESSAGE = "Страница успешно проверена";
    private static final String UNSUCCESSFULLY_CHECKED_URL_FLASH_MESSAGE = "Возникла ошибка при проверке страницы";

    private static final String ERROR_FLASH_TYPE = "error";
    private static final String ALERT_FLASH_TYPE = "alert";
    private static final String SUCCESS_FLASH_TYPE = "success";

    private static final String FLASH_SESSION_ATTRIBUTE = "flash";
    private static final String FLASH_TYPE_SESSION_ATTRIBUTE = "flash-type";

    public static void create(Context ctx) {
        log.info("Attempting to create URL from request");

        try {
            String fullUrlStr = ctx.formParamAsClass("url", String.class)
                    .check(value -> value != null && !value.trim().isEmpty(), INVALID_URL_FLASH_MESSAGE)
                    .get()
                    .trim()
                    .toLowerCase();

            URL fullUrl = URI.create(fullUrlStr).toURL();
            String normalizedUrlStr = getNormalizedUrl(fullUrl);

            if (UrlsRepository.existsByName(normalizedUrlStr)) {
                log.info("URL already exists: {}", normalizedUrlStr);
                ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, EXISTING_URL_FLASH_MESSAGE);
                ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, ALERT_FLASH_TYPE);

                ctx.redirect(NamedRoutes.urlsPath());
                return;
            }

            Url url = new Url(normalizedUrlStr, Timestamp.from(Instant.now()));
            UrlsRepository.save(url);
            log.info("URL created successfully: {}", normalizedUrlStr);

            ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, SUCCESSFULLY_ADDED_URL_FLASH_MESSAGE);
            ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, SUCCESS_FLASH_TYPE);

            ctx.redirect(NamedRoutes.urlsPath());

        } catch (MalformedURLException | ValidationException | IllegalArgumentException error) {
            log.error("Failed to create URL: {}", error.getMessage(), error);

            ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, INVALID_URL_FLASH_MESSAGE);
            ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, ERROR_FLASH_TYPE);

            ctx.redirect(NamedRoutes.rootPath());
        } catch (SQLException error) {
            log.error("Failed to create URL: {}", error.getMessage(), error);

            ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, UNSUCCESSFULLY_ADDED_URL_FLASH_MESSAGE);
            ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, ERROR_FLASH_TYPE);
        }
    }

    public static void index(Context ctx) throws SQLException {
        log.info("Rendering URLs index page");
        String flash = ctx.consumeSessionAttribute(FLASH_SESSION_ATTRIBUTE);
        String flashType = ctx.consumeSessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE);

        UrlsPage page = new UrlsPage(UrlsRepository.getEntities());
        page.setFlash(flash);
        page.setFlashType(flashType);

        ctx.render("urls/index.jte", model("page", page));
        log.debug("URLs page rendered with {} URLs and flash: {}", page.getUrls().size(), flash);
    }

    public static void show(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();

        Url url = UrlsRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));
        List<UrlCheck> urlChecks = UrlChecksRepository.findChecksByUrlId(id);

        UrlPage page = new UrlPage(url, urlChecks);

        String flash = ctx.consumeSessionAttribute(FLASH_SESSION_ATTRIBUTE);
        String flashType = ctx.consumeSessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE);

        page.setFlash(flash);
        page.setFlashType(flashType);

        ctx.render("urls/show.jte", model("page", page));
    }

    public static void check(Context ctx) throws SQLException {
        log.info("Attempting to check URL from request");

        Long urlId = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlsRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));

        try {
            UrlCheck urlCheck = new UrlCheck(urlId, Timestamp.from(Instant.now()));
            UrlChecksRepository.save(urlCheck);

            ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, SUCCESSFULLY_CHECKED_URL_FLASH_MESSAGE);
            ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, SUCCESS_FLASH_TYPE);

            ctx.redirect(NamedRoutes.urlPath(urlId));
        } catch (SQLException error) {
            ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, UNSUCCESSFULLY_CHECKED_URL_FLASH_MESSAGE);
            ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, ERROR_FLASH_TYPE);
        }
    }

    public static String getNormalizedUrl(URL url) {
        String protocol = url.getProtocol();
        String host = url.getHost();
        String port = url.getPort() != -1 ? String.format(":%d", url.getPort()) : "";

        return String.format("%s://%s%s", protocol, host, port);
    }

}

