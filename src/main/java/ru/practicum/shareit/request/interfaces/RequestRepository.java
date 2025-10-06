package ru.practicum.shareit.request.interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long requesterId);

    @Query("SELECT r FROM Request r WHERE r.requester.id <> ?1")
    List<Request> findAllExceptRequester(Long userId, Pageable pageable);

    @Query("SELECT r FROM Request r WHERE r.requester.id <> ?1")
    List<Request> findAllExceptRequester(Long userId);
}