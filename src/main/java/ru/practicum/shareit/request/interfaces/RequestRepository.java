package ru.practicum.shareit.request.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.Request;

import java.util.Collection;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Collection<Request> findByRequesterId(Long userId);

}