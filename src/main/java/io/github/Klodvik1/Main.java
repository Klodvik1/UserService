package io.github.Klodvik1;

import io.github.Klodvik1.config.HibernateSessionFactoryProvider;
import io.github.Klodvik1.dao.UserDao;
import io.github.Klodvik1.dao.UserDaoImpl;
import io.github.Klodvik1.mapper.UserMapper;
import io.github.Klodvik1.service.UserService;
import io.github.Klodvik1.service.UserServiceImpl;
import io.github.Klodvik1.ui.UserConsoleMenu;
import io.github.Klodvik1.validation.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("UserService запущен.");

        UserDao userDao = new UserDaoImpl();
        UserMapper userMapper = new UserMapper();
        UserValidator userValidator = new UserValidator();
        UserService userService = new UserServiceImpl(userDao, userMapper, userValidator);
        UserConsoleMenu userConsoleMenu = new UserConsoleMenu(userService);

        try {
            userConsoleMenu.start();
        }
        finally {
            HibernateSessionFactoryProvider.shutdown();
            LOGGER.info("Работа UserService завершена.");
        }
    }
}