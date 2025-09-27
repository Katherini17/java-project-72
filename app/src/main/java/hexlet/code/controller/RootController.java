package hexlet.code.controller;

import io.javalin.http.Context;

public class RootController {

    public static void root(Context ctx) {
        ctx.render("index.jte");
    }

}
