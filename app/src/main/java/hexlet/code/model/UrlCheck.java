package hexlet.code.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"id", "urlId", "createdAt"})
public class UrlCheck {
    private Long id;

    private int statusCode;
    private String title;
    private String h1;
    private String description;

    private Long urlId;
    private Timestamp createdAt;

    public UrlCheck(int statusCode, String title, String h1, String description, Long urlId, Timestamp createdAt) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.urlId = urlId;
        this.createdAt = createdAt;
    }


}
