package hexlet.code.repository;

import hexlet.code.model.Url;

import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class UrlsRepository extends BaseRepository {
    public static void save(Url url) throws SQLException {
        String urlName = url.getName();
        Timestamp createdAt = Timestamp.from(Instant.now());

        log.info("Attempting to save URL: {}", urlName);

        String sql = """
                INSERT INTO urls (name, created_at)
                VALUES (?, ?)
                """;
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, urlName);
            preparedStatement.setTimestamp(2, createdAt);

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

            if (generatedKeys.next()) {
                Long id = generatedKeys.getLong("id");
                url.setId(id);
                url.setCreatedAt(createdAt);

                log.info("URL saved successfully with ID: {}", id);
            } else {
                log.error("Database did not return an ID after saving URL: {}", url.getName());
                throw new SQLException("DataBase have not returned an id after saving an entity");
            }
        }
    }

    public static List<Url> getEntities() throws SQLException {
        log.info("Attempting to retrieve list of URLs");
        String sql = "SELECT * FROM urls";

        try (Connection connection = getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Url> urls = new ArrayList<>();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Timestamp createdAt = resultSet.getTimestamp("created_at");

                Url url = new Url(name);
                url.setId(id);
                url.setCreatedAt(createdAt);
                urls.add(url);
            }

            log.info("Retrieved {} URLs successfully", urls.size());
            return urls;
        }

    }

    public static boolean existsByName(String name) throws SQLException {
        log.debug("Checking if URL exists: {}", name);
        String sql = """
                    SELECT EXISTS(
                        SELECT 1
                        FROM urls
                        WHERE urls.name = ?
                    )
                    """;

        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                boolean exists = resultSet.getBoolean(1);
                log.debug("URL '{}' exists: {}", name, exists);
                return exists;
            }
        }

        return false;
    }

    public static Optional<Url> find(long id) throws SQLException {
        log.info("Attempting to find URL with id: {}", id);
        String sql = "SELECT * FROM urls WHERE id = ?";

        try (Connection connection = getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String name = resultSet.getString("name");
                Timestamp createdAt = resultSet.getTimestamp("created_at");

                Url url = new Url(name);
                url.setId(id);
                url.setCreatedAt(createdAt);

                return Optional.of(url);
            } else {
                log.info("No URL found with id: {}", id);
            }
        }

        return Optional.empty();
    }

    public static void removeAll() throws SQLException {
        String sql = "DELETE from urls";

        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(sql);
            log.info("Remove all URLs from table urls");
        } catch (SQLException e) {
            log.error("Failed to delete urls from table urls: {}", e.getMessage(), e);
        }
    }
}
