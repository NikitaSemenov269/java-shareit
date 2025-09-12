package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.interfacesUser.UserRepositoryInterface;

import java.util.*;

@Repository // планируется переход на работу с БД
@RequiredArgsConstructor
public class UserRepository implements UserRepositoryInterface {

    private final UserMapper userMapper;
    private Map<Long, User> users = new HashMap<>();

    @Override
    public void addUser(User newUser) {
        users.put(newUser.getId(), newUser);
    }

    @Override
    public User updateUser(Long userId, User user) {
        User updateUser = users.get(userId);
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updateUser.setEmail(user.getEmail());
        }
        return updateUser;
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public UserDTO getUserDTOById(Long userId) {
        return userMapper.toUserDTO(users.get(userId));
    }

    @Override
    public void deleteUserById(Long userId) {
        users.remove(userId);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean existsByEmailByUserId(String email, Long userId) {
        return users.values().stream()
                .filter(user -> !user.getId().equals(userId)) //исключаем самого пользователя
                .anyMatch(userEmail -> userEmail.getEmail().equals(email));
    }
}
