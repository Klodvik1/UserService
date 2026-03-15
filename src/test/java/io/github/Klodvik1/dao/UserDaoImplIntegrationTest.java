package io.github.Klodvik1.dao;

import io.github.Klodvik1.entity.User;
import io.github.Klodvik1.testconfig.TestSessionFactoryProvider;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoImplIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("test_user_service_db")
                    .withUsername("postgres")
                    .withPassword("postgres");

    private static SessionFactory sessionFactory;
    private static UserDaoImpl userDao;

    @BeforeAll
    static void setUpAll() {
        System.out.println("DOCKER_HOST=" + System.getenv("DOCKER_HOST"));
        sessionFactory = TestSessionFactoryProvider.createSessionFactory(POSTGRES_CONTAINER);
        userDao = new UserDaoImpl(sessionFactory);
    }

    @AfterAll
    static void tearDownAll() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void clearDatabase() {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.createMutationQuery("delete from User").executeUpdate();

            transaction.commit();
        }
    }

    @Test
    void create_ShouldPersistUser() {
        User user = buildUser("Denis", "denis@gmail.com", 23);

        User savedUser = userDao.create(user);

        assertNotNull(savedUser.getId());
        assertEquals("Denis", savedUser.getName());
        assertEquals("denis@gmail.com", savedUser.getEmail());
        assertEquals(23, savedUser.getAge());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        User savedUser = userDao.create(buildUser("Denis", "denis@gmail.com", 23));

        Optional<User> foundUser = userDao.findById(savedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("Denis", foundUser.get().getName());
        assertEquals("denis@gmail.com", foundUser.get().getEmail());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        Optional<User> foundUser = userDao.findById(999L);

        assertTrue(foundUser.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllUsersInOrder() {
        User firstUser = userDao.create(buildUser("Denis", "denis@gmail.com", 23));
        User secondUser = userDao.create(buildUser("Anna", "anna@gmail.com", 30));

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
        assertEquals(firstUser.getId(), users.get(0).getId());
        assertEquals(secondUser.getId(), users.get(1).getId());
    }

    @Test
    void findAll_ShouldReturnEmptyList_WhenTableIsEmpty() {
        List<User> users = userDao.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    void update_ShouldUpdateExistingUser() {
        User savedUser = userDao.create(buildUser("Denis", "denis@gmail.com", 23));

        savedUser.setName("Updated Denis");
        savedUser.setEmail("updated@gmail.com");
        savedUser.setAge(35);

        User updatedUser = userDao.update(savedUser);
        Optional<User> foundUser = userDao.findById(updatedUser.getId());

        assertTrue(foundUser.isPresent());
        assertEquals("Updated Denis", foundUser.get().getName());
        assertEquals("updated@gmail.com", foundUser.get().getEmail());
        assertEquals(35, foundUser.get().getAge());
    }

    @Test
    void deleteById_ShouldDeleteUser_WhenUserExists() {
        User savedUser = userDao.create(buildUser("Denis", "denis@gmail.com", 23));

        boolean isDeleted = userDao.deleteById(savedUser.getId());
        Optional<User> foundUser = userDao.findById(savedUser.getId());

        assertTrue(isDeleted);
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void deleteById_ShouldReturnFalse_WhenUserDoesNotExist() {
        boolean isDeleted = userDao.deleteById(999L);

        assertFalse(isDeleted);
    }

    private User buildUser(String name, String email, Integer age) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);

        return user;
    }
}