package hexlet.code.controller;

import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.validation.ValidationException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.time.Instant;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    private static final String INVALID_URL_FLASH_MESSAGE = "Некорректный URL";
    private static final String EXISTING_URL_FLASH_MESSAGE = "Страница уже существует";
    private static final String SUCCESSFULLY_ADDED_URL_FLASH_MESSAGE = "Страница успешно добавлена";

    private static final String ERROR_FLASH_TYPE = "error";
    private static final String ALERT_FLASH_TYPE = "alert";
    private static final String SUCCESS_FLASH_TYPE = "success";

    private static final String FLASH_SESSION_ATTRIBUTE = "flash";
    private static final String FLASH_TYPE_SESSION_ATTRIBUTE = "flash-type";

    public static void create(Context ctx) throws SQLException {
        try {
            String fullUrlStr = ctx.formParamAsClass("name", String.class)
                    .check(value -> value != null && !value.trim().isEmpty(), INVALID_URL_FLASH_MESSAGE)
                    .get()
                    .trim()
                    .toLowerCase();
            URL fullUrl = URI.create(fullUrlStr).toURL();
            String normalizedUrlStr = getNormalizedUrl(fullUrl);

            if (UrlsRepository.existsByName(normalizedUrlStr)) {
                ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, EXISTING_URL_FLASH_MESSAGE);
                ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, ALERT_FLASH_TYPE);

                ctx.redirect(NamedRoutes.rootPath());
                return;
            }

            Url url = new Url(normalizedUrlStr, Instant.now());
            UrlsRepository.save(url);

            ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, SUCCESSFULLY_ADDED_URL_FLASH_MESSAGE);
            ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, SUCCESS_FLASH_TYPE);

            UrlsPage page = new UrlsPage(UrlsRepository.getEntities());
            ctx.render("urls.jte", model("page", page));

        } catch (MalformedURLException | ValidationException | IllegalArgumentException error) {
            ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, INVALID_URL_FLASH_MESSAGE);
            ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, ERROR_FLASH_TYPE);

            System.out.println("Ошибка в catch " + error.getMessage());
            ctx.redirect(NamedRoutes.rootPath());
        }
    }

    public static String getNormalizedUrl(URL url) {
        String protocol = url.getProtocol();
        String host = url.getHost();
        String port = url.getPort() != -1 ? String.format(":%d", url.getPort()) : "";

        return String.format("%s://%s%s", protocol, host, port);
    }
}
