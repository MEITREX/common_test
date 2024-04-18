package de.unistuttgart.iste.meitrex.common.testutil;

import org.junit.jupiter.api.extension.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * JUnit test extension that clears the database after each test.
 * <p>
 * Usage:
 * <pre>
 *     &#64;ExtendWith(ClearDatabase.class)
 *     public class MyTest {
 *       // ...
 * </pre>
 * To specify the tables to delete, annotate the test class with {@link TablesToDelete} <br>
 * This can also be used to specify the order of deletion. <br>
 * This extension is automatically used by {@link GraphQlApiTest}.
 */
public class ClearDatabase implements AfterEachCallback, BeforeAllCallback {

    private DataSource dataSource;
    private List<String> tablesInOrderOfDeletion = null;

    @Override
    public void beforeAll(ExtensionContext context) {
        context.getTestClass().ifPresent(testClass -> {
            if (testClass.isAnnotationPresent(TablesToDelete.class)) {
                this.tablesInOrderOfDeletion = Arrays.asList(testClass.getAnnotation(TablesToDelete.class).value());
            }
        });
        this.dataSource = SpringExtension.getApplicationContext(context).getBean("dataSource", DataSource.class);
    }

    @Override
    public void afterEach(ExtensionContext context) throws SQLException {
        deleteTables();
    }

    private void deleteTables() throws SQLException {
        List<String> notDeletedTables = getTablesToDelete();

        while (!notDeletedTables.isEmpty()) {
            List<String> tablesToDelete = new ArrayList<>(notDeletedTables);

            RuntimeException lastException = new RuntimeException("Could not delete tables");

            for (String table : notDeletedTables) {
                try {
                    deleteSingleTable(table);
                    tablesToDelete.remove(table);
                } catch (RuntimeException e) {
                    lastException = e;
                }
            }

            if (tablesToDelete.size() == notDeletedTables.size()) {
                // no tables were deleted
                throw lastException;
            }

            notDeletedTables = tablesToDelete;
        }
    }

    private void deleteSingleTable(String table) {
        JdbcTemplate template = new JdbcTemplate(this.dataSource);
        JdbcTestUtils.deleteFromTables(template, table);
    }

    private List<String> getTablesToDelete() throws SQLException {
        if (this.tablesInOrderOfDeletion == null) {
            this.tablesInOrderOfDeletion = getAllDbTableNames(this.dataSource);
        }
        return this.tablesInOrderOfDeletion;
    }

    /**
     * Returns all table names of the database
     *
     * @param dataSource the datasource
     * @return a list of table names
     */
    private List<String> getAllDbTableNames(DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            List<String> result = new ArrayList<>();
            ResultSet resultTables = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
            while (resultTables.next()) {
                result.add(resultTables.getString("TABLE_NAME"));
            }
            return result;
        }
    }

}
