package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import io.javalin.http.Context;
import static io.javalin.rendering.template.TemplateUtil.model;

public class RootController {

    public static void root(Context ctx) {
        BasePage page = new BasePage();
        page.setTitle("Анализатор страниц");
        ctx.render("index.jte", model("page", page));
    }

}
