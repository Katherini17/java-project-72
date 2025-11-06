package hexlet.code.repository;
import hexlet.code.model.UrlCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlChecksRepository extends BaseRepository {
    public static void save(UrlCheck urlCheck) throws SQLException {
        Long urlId = urlCheck.getUrlId();
        Instant createdAt = Instant.now();

        log.info("Attempting to save URL check with url ID: {}", urlId);

        String sql = """
                INSERT INTO url_checks (status_code, title, h1, description, url_id, created_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, urlCheck.getStatusCode());
            preparedStatement.setString(2, urlCheck.getTitle());
            preparedStatement.setString(3, urlCheck.getH1());
            preparedStatement.setString(4, urlCheck.getDescription());
            preparedStatement.setLong(5, urlCheck.getUrlId());
            preparedStatement.setTimestamp(6, Timestamp.from(createdAt));

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                Long id = generatedKeys.getLong("id");
                urlCheck.setId(id);
                urlCheck.setCreatedAt(createdAt);

                log.info("URL check saved successfully with ID: {}", id);
            } else {
                log.error("Database did not return an ID after saving URL check with URL ID: {}", urlId);
                throw new SQLException("DataBase have not returned an id after saving an entity");
            }
        }
    }

    public static List<UrlCheck> findChecksByUrlId(Long urlId) throws SQLException {
        String sql = """
            SELECT *
            FROM url_checks
            WHERE url_id = ?
            ORDER BY id DESC
            """;

        try (Connection connection = getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, urlId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<UrlCheck> urlChecks = new ArrayList<>();
            while (resultSet.next()) {
                Long checkId = resultSet.getLong("id");
                int statusCode = resultSet.getInt("status_code");
                String title = resultSet.getString("title");
                String h1 = resultSet.getString("h1");
                String description = resultSet.getString("description");
                Instant createdAt = resultSet.getTimestamp("created_at")
                        .toInstant();

                UrlCheck urlCheck = new UrlCheck(urlId);

                urlCheck.setId(checkId);
                urlCheck.setStatusCode(statusCode);
                urlCheck.setTitle(title);
                urlCheck.setH1(h1);
                urlCheck.setDescription(description);
                urlCheck.setUrlId(urlId);
                urlCheck.setCreatedAt(createdAt);

                urlChecks.add(urlCheck);

            }
            log.info("Fetched {} URL checks for URL ID: {}", urlChecks.size(), urlId);
            return urlChecks;
        }
    }

    public static Map<Long, UrlCheck> getLastUrlsChecks() throws SQLException {
        String sql = """
                SELECT DISTINCT ON (url_id)
                *
                FROM url_checks
                ORDER BY url_id, created_at DESC;
                """;
        try (Connection connection = getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            Map<Long, UrlCheck> lastUrlChecks = new HashMap<>();

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long checkId = resultSet.getLong("id");
                int statusCode = resultSet.getInt("status_code");
                String title = resultSet.getString("title");
                String h1 = resultSet.getString("h1");
                String description = resultSet.getString("description");
                Long urlId = resultSet.getLong("url_id");
                Timestamp createdAt = resultSet.getTimestamp("created_at");

                UrlCheck urlCheck = new UrlCheck(urlId);

                urlCheck.setId(checkId);
                urlCheck.setStatusCode(statusCode);
                urlCheck.setTitle(title);
                urlCheck.setH1(h1);
                urlCheck.setDescription(description);
                urlCheck.setUrlId(urlId);
                urlCheck.setCreatedAt(createdAt.toInstant());

                lastUrlChecks.put(urlId, urlCheck);
            }

            log.info("Fetched {} last URLs checks", lastUrlChecks.size());
            return lastUrlChecks;
        }
    }

    public static void removeAll() {
        String sql = "DELETE from url_checks";

        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.executeUpdate(sql);
            log.info("Remove all url checks from table url_checks");
        } catch (SQLException e) {
            log.error("Failed to delete url checks from table url_checks: {}", e.getMessage(), e);
        }
    }
}
