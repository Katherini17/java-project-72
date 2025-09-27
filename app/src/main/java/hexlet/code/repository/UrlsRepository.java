package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UrlsRepository extends BaseRepository {
    public static void save(Url url) throws SQLException {
        String sql = """
                INSERT INTO urls (name, created_at)
                VALUES (?, ?)
                """;
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, url.getName());
            preparedStatement.setObject(2, url.getCreatedAt());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                Long id = generatedKeys.getLong(1);
                url.setId(id);
            } else {
                throw new SQLException("DataBase have not returned an id after saving an entity");
            }
        }
    }

    public static List<Url> getEntities() throws SQLException {
        String sql = "SELECT * FROM urls";

        try (Connection connection = getDataSource().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Url> urls = new ArrayList<>();

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Instant createdAt = resultSet.getTimestamp("created_at").toInstant();

                Url url = new Url(name, createdAt);
                url.setId(id);

                urls.add(url);
            }

            return urls;
        }

    }

    public static boolean existsByName(String name) throws SQLException {
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
                return resultSet.getBoolean(1);
            }
        }

        return false;
    }
}
