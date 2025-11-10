package hexlet.code.dto;

import io.javalin.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorPage extends BasePage {
    private String statusCode;
    private String errorMessage;
}
