package de.unistuttgart.iste.gits.common.testutil;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    private String[] tablesInOrderOfDeletion = null;

    @Override
    public void beforeAll(ExtensionContext context) {
        context.getTestClass().ifPresent(testClass -> {
            if (testClass.isAnnotationPresent(TablesToDelete.class)) {
                this.tablesInOrderOfDeletion = testClass.getAnnotation(TablesToDelete.class).value();
            }
        });
        this.dataSource = SpringExtension.getApplicationContext(context).getBean("dataSource", DataSource.class);
    }

    @Override
    public void afterEach(ExtensionContext context) throws SQLException {
        JdbcTemplate template = new JdbcTemplate(this.dataSource);
        JdbcTestUtils.deleteFromTables(template, getTablesToDelete());
    }

    private String[] getTablesToDelete() throws SQLException {
        if (this.tablesInOrderOfDeletion == null) {
            this.tablesInOrderOfDeletion = getAllDbTableNames(this.dataSource).toArray(new String[0]);
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
