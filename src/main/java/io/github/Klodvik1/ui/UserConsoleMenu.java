package io.github.Klodvik1.ui;

import io.github.Klodvik1.dto.UserRequestDto;
import io.github.Klodvik1.dto.UserResponseDto;
import io.github.Klodvik1.exception.DataAccessException;
import io.github.Klodvik1.exception.UserNotFoundException;
import io.github.Klodvik1.exception.ValidationException;
import io.github.Klodvik1.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

public class UserConsoleMenu {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserConsoleMenu.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    private final UserService userService;
    private final Scanner scanner;

    public UserConsoleMenu(UserService userService) {
        this.userService = userService;
        scanner = new Scanner(System.in);
    }

    public void start() {
        boolean isRunning = true;

        while (isRunning) {
            printMenu();

            String command = scanner.nextLine().trim();

            try {
                switch (command) {
                    case "1" -> createUser();
                    case "2" -> findUserById();
                    case "3" -> showAllUsers();
                    case "4" -> updateUser();
                    case "5" -> deleteUserById();
                    case "0" -> isRunning = false;
                    default -> System.out.println("Неизвестная команда. Повторите ввод.");
                }
            }
            catch (ValidationException | UserNotFoundException exception) {
                System.out.println("Ошибка: " + exception.getMessage());
                LOGGER.warn("Ошибка пользовательского сценария: {}", exception.getMessage());
            }
            catch (DataAccessException exception) {
                System.out.println("Ошибка при работе с базой данных: " + exception.getMessage());
                LOGGER.error("Ошибка доступа к данным.", exception);
            }
            catch (Exception exception) {
                System.out.println("Непредвиденная ошибка: " + exception.getMessage());
                LOGGER.error("Непредвиденная ошибка в консольном меню.", exception);
            }
        }

        System.out.println("Приложение завершено.");
        LOGGER.info("Работа консольного меню завершена.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== USER SERVICE MENU =====");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по id");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private void createUser() {
        System.out.println();
        System.out.println("=== Создание пользователя ===");

        UserRequestDto userRequestDto = readUserRequestDto();
        UserResponseDto createdUser = userService.createUser(userRequestDto);

        System.out.println("Пользователь успешно создан:");
        printUser(createdUser);
    }

    private void findUserById() {
        System.out.println();
        System.out.println("=== Поиск пользователя по id ===");

        Long id = readLong("Введите id пользователя: ");
        UserResponseDto user = userService.getUserById(id);

        System.out.println("Пользователь найден:");
        printUser(user);
    }

    private void showAllUsers() {
        System.out.println();
        System.out.println("=== Список пользователей ===");

        List<UserResponseDto> users = userService.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст.");
            return;
        }

        for (UserResponseDto user : users) {
            printUser(user);
            System.out.println("------------------------------");
        }
    }

    private void updateUser() {
        System.out.println();
        System.out.println("=== Обновление пользователя ===");

        Long id = readLong("Введите id пользователя: ");
        UserRequestDto userRequestDto = readUserRequestDto();

        UserResponseDto updatedUser = userService.updateUser(id, userRequestDto);

        System.out.println("Пользователь успешно обновлён:");
        printUser(updatedUser);
    }

    private void deleteUserById() {
        System.out.println();
        System.out.println("=== Удаление пользователя ===");

        Long id = readLong("Введите id пользователя: ");
        userService.deleteUserById(id);

        System.out.println("Пользователь успешно удалён.");
    }

    private UserRequestDto readUserRequestDto() {
        String name = readString("Введите имя: ");
        String email = readString("Введите email: ");
        Integer age = readInteger("Введите возраст: ");

        return new UserRequestDto(name, email, age);
    }

    private String readString(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private Long readLong(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim();

        try {
            return Long.parseLong(input);
        }
        catch (NumberFormatException exception) {
            throw new ValidationException("Ожидалось целое число типа Long.");
        }
    }

    private Integer readInteger(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim();

        try {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException exception) {
            throw new ValidationException("Ожидалось целое число типа Integer.");
        }
    }

    private void printUser(UserResponseDto user) {
        System.out.println("ID: " + user.id());
        System.out.println("Имя: " + user.name());
        System.out.println("Email: " + user.email());
        System.out.println("Возраст: " + user.age());
        System.out.println("Дата создания: " + user.createdAt().format(DATE_TIME_FORMATTER));
    }
}
