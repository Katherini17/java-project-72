package hexlet.code.controller.urls;

import hexlet.code.controller.BaseController;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.sql.SQLException;

@Slf4j
public class UrlChecksController extends BaseController {

    private static final String SUCCESSFULLY_CHECKED_URL_FLASH_MESSAGE = "Страница успешно проверена";
    private static final String UNSUCCESSFULLY_CHECKED_URL_FLASH_MESSAGE = "Возникла ошибка при проверке страницы";

    public static void create(Context ctx) throws SQLException {
        log.info("Attempting to create URL from request");

        Long urlId = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlsRepository.find(urlId)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));

        HttpResponse<String> response;
        String responseBody;
        try {
            response = Unirest.get(url.getName())
                    .asString();
            responseBody = response.getBody();


            if (responseBody == null || responseBody.isEmpty()) {
                log.error("Empty response body for URL: {}", url.getName());
                throw new IllegalStateException("Response body is empty for URL: " + url.getName());
            }

        } catch (UnirestException | IllegalStateException e) {
            log.error("Error during URL create: {}", e.getMessage(), e);

            ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, UNSUCCESSFULLY_CHECKED_URL_FLASH_MESSAGE);
            ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, ERROR_FLASH_TYPE);

            ctx.redirect(NamedRoutes.urlPath(urlId));
            return;
        }

        Document document = Jsoup.parse(responseBody);

        int statusCode = response.getStatus();
        log.info("Checking url, status code: {}", statusCode);
        String titleContent = document.title();

        Element h1Tag = document.selectFirst("h1");
        String h1Content = h1Tag != null ? h1Tag.text() : "";

        Element descriptionMetaTag = document.selectFirst("meta[name=description]");
        String descriptionContent = descriptionMetaTag != null ? descriptionMetaTag.attr("content") : "";

        UrlCheck urlCheck = new UrlCheck(urlId);
        urlCheck.setStatusCode(statusCode);
        urlCheck.setTitle(titleContent);
        urlCheck.setH1(h1Content);
        urlCheck.setDescription(descriptionContent);

        UrlChecksRepository.save(urlCheck);

        ctx.sessionAttribute(FLASH_SESSION_ATTRIBUTE, SUCCESSFULLY_CHECKED_URL_FLASH_MESSAGE);
        ctx.sessionAttribute(FLASH_TYPE_SESSION_ATTRIBUTE, SUCCESS_FLASH_TYPE);

        ctx.redirect(NamedRoutes.urlPath(urlId));
    }
}
