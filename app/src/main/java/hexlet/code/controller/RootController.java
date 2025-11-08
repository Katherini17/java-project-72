package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import io.javalin.http.Context;
import lombok.extern.slf4j.Slf4j;

import static io.javalin.rendering.template.TemplateUtil.model;

@Slf4j
public class RootController extends BaseController {

    public static void root(Context ctx) {
        log.info("Rendering root page for request: {}", ctx.path());

        BasePage page = new BasePage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("index.jte", model("page", page));

        log.debug("Root page rendered successfully");
    }

}
