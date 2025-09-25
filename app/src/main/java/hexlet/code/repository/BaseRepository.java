package hexlet.code.repository;

import lombok.Getter;
import lombok.Setter;

import com.zaxxer.hikari.HikariDataSource;

public class BaseRepository {
    @Getter
    @Setter
    private static HikariDataSource dataSource;
}
