package io.github.Klodvik1.validation;

import io.github.Klodvik1.exception.DuplicateEmailException;
import io.github.Klodvik1.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserValidator {
    private static final String DUPLICATE_EMAIL_MESSAGE = "Пользователь с таким email уже существует.";

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    public void validateEmailUniqueness(String normalizedEmail) {
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateEmailException(DUPLICATE_EMAIL_MESSAGE);
        }
    }

    public void validateEmailUniquenessForUpdate(Long id, String normalizedEmail) {
        userRepository.findByEmailIgnoreCase(normalizedEmail)
                .filter(foundUser -> !foundUser.getId().equals(id))
                .ifPresent(foundUser -> {
                    throw new DuplicateEmailException(DUPLICATE_EMAIL_MESSAGE);
                });
    }
}
