package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlChecksRepository extends BaseRepository {
    public static void save(UrlCheck urlCheck) throws SQLException {
        Long urlId = urlCheck.getUrlId();
        Timestamp urlCheckCreatedAt = urlCheck.getCreatedAt();
        log.info("Attempting to save URL check with url ID: {}, created at: {}", urlId, urlCheckCreatedAt);

        String sql = """
                INSERT INTO url_checks (status_code, title, h1, description, url_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, urlCheck.getStatusCode());
            preparedStatement.setString(2, urlCheck.getTitle());
            preparedStatement.setString(3, urlCheck.getH1());
            preparedStatement.setString(4, urlCheck.getDescription());
            preparedStatement.setLong(5, urlCheck.getUrlId());

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                Long id = generatedKeys.getLong(1);
                urlCheck.setId(id);

                log.info("URL check saved successfully with ID: {}", id);
            } else {
                log.error("Database did not return an ID after saving URL check with URL ID: {}, created at: {}",
                        urlId, urlCheckCreatedAt);
                throw new SQLException("DataBase have not returned an id after saving an entity");
            }
        }
    }

    public static List<UrlCheck> findChecksByUrlId(Long urlId) throws SQLException {
        String sql = """
            SELECT *
            FROM url_checks
            WHERE urlId = ?
            ORDER BY id DESC
        """;

        try (Connection connection = getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, urlId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<UrlCheck> urlChecks = new ArrayList<>();
            while (resultSet.next()) {
                UrlCheck urlCheck = new UrlCheck(urlId, resultSet.getTimestamp("createdAt"));
                urlChecks.add(urlCheck);
            }

            return urlChecks;
        }
    }
}
