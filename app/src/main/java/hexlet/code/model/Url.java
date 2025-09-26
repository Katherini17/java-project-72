package hexlet.code.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Url {
    private Long id;

    @ToString.Include
    private String name;

    private Instant createdAt;

    public Url(String name, Instant createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }
}
