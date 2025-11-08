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
import lombok.extern.slf4j.Slf4j;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static io.javalin.rendering.template.TemplateUtil.model;

@Slf4j
public class UrlsController extends BaseController {

    private static final String INVALID_URL_FLASH_MESSAGE = "Некорректный URL";
    private static final String EXISTING_URL_FLASH_MESSAGE = "Страница уже существует";
    private static final String SUCCESSFULLY_ADDED_URL_FLASH_MESSAGE = "Страница успешно добавлена";

    public static void createUrl(Context ctx) throws SQLException {
        log.info("Attempting to createUrl URL from request");

        String formParamUrl = ctx.formParam("url");
        String fullUrlStr = formParamUrl != null ? formParamUrl.trim().toLowerCase() : null;

        URL parsedUrl;

        try {
            parsedUrl = URI.create(fullUrlStr).toURL();
        } catch (MalformedURLException | IllegalArgumentException e) {
            log.error("Failed to createUrl URL: {}", e.getMessage(), e);

            ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, INVALID_URL_FLASH_MESSAGE);
            ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, ERROR_FLASH_TYPE);

            ctx.redirect(NamedRoutes.rootPath());
            return;
        }

        String normalizedUrlStr = getNormalizedUrl(parsedUrl);

        if (UrlsRepository.existsByName(normalizedUrlStr)) {
            log.info("URL already exists: {}", normalizedUrlStr);
            ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, EXISTING_URL_FLASH_MESSAGE);
            ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, ALERT_FLASH_TYPE);

            ctx.redirect(NamedRoutes.urlsPath());
            return;
        }

        Url url = new Url(normalizedUrlStr);
        UrlsRepository.save(url);
        log.info("URL created successfully: {}", normalizedUrlStr);

        ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, SUCCESSFULLY_ADDED_URL_FLASH_MESSAGE);
        ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, SUCCESS_FLASH_TYPE);

        ctx.redirect(NamedRoutes.urlsPath());
    }

    public static void index(Context ctx) throws SQLException {
        log.info("Rendering URLs index page");
        String flash = ctx.consumeSessionAttribute(FLASH_SESSION_ATTRIBUTE);
        String flashType = ctx.consumeSessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE);

        List<Url> urls = UrlsRepository.getEntities();
        Map<Long, UrlCheck> lastUrlsChecks = UrlChecksRepository.getLastUrlsChecks();

        UrlsPage page = new UrlsPage(urls, lastUrlsChecks);
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

    public static String getNormalizedUrl(URL url) {
        String protocol = url.getProtocol();
        String host = url.getHost();
        String port = url.getPort() != -1
                ? String.format(":%d", url.getPort())
                    .toLowerCase()
                : "";

        return String.format("%s://%s%s", protocol, host, port);
    }
}

