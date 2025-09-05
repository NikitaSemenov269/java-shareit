package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.interfacesUser.UserRepositoryInterface;

import java.util.*;

@Repository // планируется переход на работу с БД
@RequiredArgsConstructor
public class UserRepository implements UserRepositoryInterface {

    private final UserMapper userMapper;
    private Map<Long, User> users = new HashMap();

    @Override
    public boolean addUser(User newUser) {
        Long userId = newUser.getUserId();
        if (users.containsKey(userId)) {
            return false;
        }
        users.put(userId, newUser);
        return true;
    }

//    @Override
//    public User getUserById(Long userId) {
//        return Optional.ofNullable(users.get(userId))
//                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
//    }

    @Override
    public UserDTO getUserDTOById(Long userId) {
        return Optional.ofNullable(userMapper.toUserDTO(users.get(userId)))
                .orElseThrow(() -> new NotFoundException("Пользователь с ID: " + userId + " не найден"));
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }
}
