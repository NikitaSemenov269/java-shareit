package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.interfaces.UserRepositoryInterface;

import java.util.*;

@Repository // планируется переход на работу с БД
public class UserRepository implements UserRepositoryInterface {

    private Set<User> users = new HashSet<>();

    @Override
    public boolean addUser(User newUser) {
        return users.add(newUser);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.empty();
    }

    @Override
    public Collection<User> getAllUsers() {
        return null;
    }

    @Override
    public boolean deleteUser(Long userId) {
        return true;
    }
}
