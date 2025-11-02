package hexlet.code.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"id", "createdAt"})
public class UrlCheck {
    private Long id;

    private int statusCode;
    private String title;
    private String h1;
    private String description;
    private Long urlId;

    private Timestamp createdAt;

    public UrlCheck(Long urlId) {
        this.urlId = urlId;
    }
}
