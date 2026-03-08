package io.github.Klodvik1;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import io.github.Klodvik1.config.HibernateSessionFactoryProvider;
import io.github.Klodvik1.dao.UserDao;
import io.github.Klodvik1.dao.UserDaoImpl;
import io.github.Klodvik1.mapper.UserMapper;
import io.github.Klodvik1.service.UserService;
import io.github.Klodvik1.ui.UserConsoleMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("UserService запущен.");

        UserDao userDao = new UserDaoImpl();
        UserMapper userMapper = new UserMapper();
        UserService userService = new UserService(userDao, userMapper);
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