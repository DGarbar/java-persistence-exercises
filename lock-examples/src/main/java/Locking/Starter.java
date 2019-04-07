package Locking;

import java.sql.SQLException;
import javax.sql.DataSource;
import ua.procamp.util.JdbcUtil;

public class Starter {

    public static void main(String[] args) {
        DataSource dataSource = JdbcUtil
            .createPostgresDataSource("jdbc:postgresql://localhost:5432/ProCamp", "root", "root");
        Long programId = 2L;

    }

    private static void optimisticStart(DataSource dataSource, Long id) {
        OptimisticLocking optimisticLocking = new OptimisticLocking();
        try {
            optimisticLocking.handleProgramUpdateWithOptimisticLocking(dataSource, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void pessimistic(DataSource dataSource, Long id) {
    }
}
