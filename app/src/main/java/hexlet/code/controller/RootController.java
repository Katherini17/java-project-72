package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import io.javalin.http.Context;
import static io.javalin.rendering.template.TemplateUtil.model;

public class RootController {

    public static void root(Context ctx) {
        BasePage page = new BasePage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("index.jte", model("page", page));
    }

}
