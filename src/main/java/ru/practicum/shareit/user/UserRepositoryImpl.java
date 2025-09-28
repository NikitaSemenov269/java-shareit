/*
package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.interfaces.UserMapper;
import ru.practicum.shareit.user.interfaces.UserRepository;

@Repository
@RequiredArgsConstructor
@Transactional
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Transactional
    public User createUser(User newUser) {
        return save(newUser);
    }

    @Transactional
    public User updateUser(Long userId, User user) {
        User updateUser = findByUserId(userId);
        if (user.getName() != null && !updateUser.getName().equals(user.getName())) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null && !updateUser.getEmail().equals(user.getEmail())) {
            updateUser.setEmail(user.getEmail());
        }
        return updateUser;
    }

    @Override
    public User getUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public UserDto getUserDTOById(Long userId) {
        return userMapper.userToUserDto(users.get(userId));
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        users.remove(userId);
    }
}
*/
