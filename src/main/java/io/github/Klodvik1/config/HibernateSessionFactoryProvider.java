package io.github.Klodvik1.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateSessionFactoryProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateSessionFactoryProvider.class);
    private static SessionFactory sessionFactory;

    private HibernateSessionFactoryProvider() {

    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try{
                sessionFactory = new Configuration()
                        .configure("hibernate.cfg.xml")
                        .buildSessionFactory();

                LOGGER.info("SessionFactory успешно создана.");
            }
            catch (Exception exception) {
                LOGGER.error("Ошибка при создании SessionFactory.", exception);
                throw new RuntimeException("Не удалось создать SessionFactory.", exception);
            }
        }

        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            LOGGER.info("SessionFactory закрыта.");
        }
    }
}
