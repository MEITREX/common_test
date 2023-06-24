package de.unistuttgart.iste.gits.common.testutil;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * This class is a singleton that starts a postgresql container for testing.
 * It can be used in two ways:
 * <p>
 * 1. Use the {@link GitsPostgresSqlContainer} as a JUnit 5 extension:
 * <pre>
 *         &#64;ExtendWith(GitsPostgresSqlContainer.class)
 *         public class MyTest {
 *         // ...
 *         }
 *      </pre>
 * This is the preferred way and is automatically done by the {@link GraphQlApiTest} annotation.
 * <p>
 * 2. Use the {@link GitsPostgresSqlContainer} as a container:
 * <pre>
 *         &#64;Testcontainers
 *         public class MyTest {
 *
 *            &#64;Container
 *            private static final GitsPostgresSqlContainer container = GitsPostgresSqlContainer.getInstance();
 *            // ...
 *         }
 *      </pre>
 */
public class GitsPostgresSqlContainer extends PostgreSQLContainer<GitsPostgresSqlContainer>
        implements BeforeAllCallback {

    private static final String IMAGE_VERSION = "postgres:latest";

    private static GitsPostgresSqlContainer container;

    private GitsPostgresSqlContainer() {
        super(IMAGE_VERSION);
    }

    public static GitsPostgresSqlContainer getInstance() {
        if (container == null) {
            container = new GitsPostgresSqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        getInstance().start();
    }
}
