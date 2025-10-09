package ru.practicum.shareit.request.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.Request;

import java.util.Collection;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Collection<Request> findByRequesterId(Long userId);

    @Query("SELECT Request" +
            "FROM Request r " +
            "WHERE r.requester.id <> :userId")
    Collection<Request> findAllRequestsExceptUser(@Param("userId") Long userId);

}