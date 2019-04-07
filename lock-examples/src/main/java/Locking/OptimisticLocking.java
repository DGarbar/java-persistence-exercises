package Locking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;
import javax.sql.DataSource;

public class OptimisticLocking {

    public static final String sqlUpdate = "UPDATE programs SET description = ? WHERE id = ?;";
    public static final String sqlSelectVerByID = "SELECT ver FROM programs WHERE id = ?";

    public void handleProgramUpdateWithOptimisticLocking(DataSource dataSource, Long programId)
        throws SQLException {

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement prStmt = prepareOptimisticStatement(connection, sqlUpdate,
                programId);
            int rand = ThreadLocalRandom.current().nextInt(1, 10000);
            prStmt.setLong(1, rand);
            prStmt.setLong(2, programId);
            int i = prStmt.executeUpdate();
            if (i != 1) {
                throw new SQLException("Nothing was changed, Ver is not consistent");
            }
        }
    }

    private PreparedStatement prepareOptimisticStatement(Connection connection, String update,
        Long id)
        throws SQLException {
        String optimisticSqlUpdate = optimisticDecorator(update);
        Long verById = getVerById(connection, id);

        int numberOdParameters = (int) optimisticSqlUpdate.chars()
            .filter(value -> (char) value == '?')
            .count();
        PreparedStatement preparedStatement = connection.prepareStatement(optimisticSqlUpdate);
        preparedStatement.setLong(numberOdParameters, verById);
        //"UPDATE programs SET description = ?, ver = ver+1 WHERE id = ? AND ver = ?;"
        return preparedStatement;
    }

    private Long getVerById(Connection connection, Long id) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlSelectVerByID)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong(1);
            } else {
                throw new SQLException("Version not exist");
            }
        }
    }

    private String optimisticDecorator(String update) {
        StringBuilder stringBuilder = new StringBuilder();
        int whereIndex = update.indexOf("WHERE");
        return stringBuilder
            .append(update, 0, whereIndex)
            .append(", ver = ver + 1 WHERE")
            .append(update, whereIndex + 5, update.indexOf(";"))
            .append(" AND ver = ?;")
            .toString();
    }
}
