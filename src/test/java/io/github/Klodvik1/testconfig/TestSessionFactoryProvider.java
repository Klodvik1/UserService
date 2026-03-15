package io.github.Klodvik1.testconfig;

import io.github.Klodvik1.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

public final class TestSessionFactoryProvider {
    private TestSessionFactoryProvider() {
    }

    public static SessionFactory createSessionFactory(PostgreSQLContainer<?> postgresContainer) {
        return new Configuration()
                .addAnnotatedClass(User.class)
                .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                .setProperty("hibernate.connection.url", postgresContainer.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgresContainer.getUsername())
                .setProperty("hibernate.connection.password", postgresContainer.getPassword())
                .setProperty("hibernate.show_sql", "false")
                .setProperty("hibernate.format_sql", "true")
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .buildSessionFactory();
    }
}