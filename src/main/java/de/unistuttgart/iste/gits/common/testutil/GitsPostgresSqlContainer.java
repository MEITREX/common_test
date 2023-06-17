package de.unistuttgart.iste.gits.common.testutil;

import org.testcontainers.containers.PostgreSQLContainer;

public class GitsPostgresSqlContainer extends PostgreSQLContainer<GitsPostgresSqlContainer> {

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




}
