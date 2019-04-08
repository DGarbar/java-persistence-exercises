package Locking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;
import javax.sql.DataSource;

public class PessimisticLocking {

    public static final String sqlSelect = "Select description From programs Where id = ? For update;";
    public static final String sqlUpdate = "UPDATE programs SET description = ? WHERE id = ?;";

    public void handleProgramUpdateWithPessimisticLocking(DataSource dataSource, Long programId)
        throws SQLException {

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(sqlSelect);
            preparedStatement.setLong(1, programId);
            preparedStatement.execute();
            PreparedStatement prStmt = connection.prepareStatement(sqlUpdate);
            int rand = ThreadLocalRandom.current().nextInt(1, 10000);
            prStmt.setLong(1, rand);
            prStmt.setLong(2, programId);
            int i = prStmt.executeUpdate();
            connection.commit();
            if (i != 1) {
                throw new SQLException("Nothing was changed");
            }
        }
    }
}
