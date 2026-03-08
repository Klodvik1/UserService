package io.github.Klodvik1.dao;

import io.github.Klodvik1.config.HibernateSessionFactoryProvider;
import io.github.Klodvik1.entity.User;
import io.github.Klodvik1.exception.DataAccessException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    public User create(User user) {
        Transaction transaction = null;

        try (Session session = HibernateSessionFactoryProvider.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(user);

            transaction.commit();
            LOGGER.info("Пользователь успешно сохранён. id={}", user.getId());

            return user;
        }
        catch (Exception exception) {
            rollbackTransaction(transaction);
            LOGGER.error("Ошибка при сохранении пользователя.", exception);
            throw new DataAccessException("Не удалось сохранить пользователя.", exception);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        try (Session session = HibernateSessionFactoryProvider.getSessionFactory().openSession()) {
            User user = session.get(User.class, id);

            if (user != null) {
                LOGGER.info("Пользователь найден. id={}", id);
            }
            else {
                LOGGER.warn("Пользователь не найден. id={}", id);
            }

            return Optional.ofNullable(user);
        }
        catch (Exception exception) {
            LOGGER.error("Ошибка при поиске пользователя по id={}.", id, exception);
            throw new DataAccessException("Не удалось найти пользователя по id.", exception);
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = HibernateSessionFactoryProvider.getSessionFactory().openSession()) {
            List<User> users = session.createQuery(
                            "from User user order by user.id",
                            User.class)
                    .getResultList();

            LOGGER.info("Получен список пользователей. count={}", users.size());

            return users;
        }
        catch (Exception exception) {
            LOGGER.error("Ошибка при получении списка пользователей.", exception);
            throw new DataAccessException("Не удалось получить список пользователей.", exception);
        }
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;

        try (Session session = HibernateSessionFactoryProvider.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User updatedUser = session.merge(user);

            transaction.commit();
            LOGGER.info("Пользователь успешно обновлён. id={}", updatedUser.getId());

            return updatedUser;
        }
        catch (Exception exception) {
            rollbackTransaction(transaction);
            LOGGER.error("Ошибка при обновлении пользователя. id={}", user.getId(), exception);
            throw new DataAccessException("Не удалось обновить пользователя.", exception);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        Transaction transaction = null;

        try (Session session = HibernateSessionFactoryProvider.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User user = session.get(User.class, id);

            if (user == null) {
                transaction.commit();
                LOGGER.warn("Удаление не выполнено: пользователь не найден. id={}", id);

                return false;
            }

            session.remove(user);
            transaction.commit();

            LOGGER.info("Пользователь успешно удалён. id={}", id);

            return true;
        }
        catch (Exception exception) {
            rollbackTransaction(transaction);
            LOGGER.error("Ошибка при удалении пользователя. id={}", id, exception);
            throw new DataAccessException("Не удалось удалить пользователя.", exception);
        }
    }

    private void rollbackTransaction(Transaction transaction) {
        if (transaction != null) {
            try {
                transaction.rollback();
                LOGGER.info("Транзакция успешно откатана.");
            }
            catch (Exception exception) {
                LOGGER.error("Ошибка при откате транзакции.", exception);
            }
        }
    }
}
