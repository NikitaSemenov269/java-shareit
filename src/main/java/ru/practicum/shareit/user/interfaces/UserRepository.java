package ru.practicum.shareit.user.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserId(Long userId);

    boolean existsByEmailAndIdNot(String email, Long userId);

    /*

    User getUserById(Long userId);

    UserDto getUserDTOById(Long userId);

    void deleteUserById(Long userId);

    boolean existsByUserId(Long userId); // удалится

    boolean existsByEmailByUserId(String email, Long userId); // удалится
    */
}
