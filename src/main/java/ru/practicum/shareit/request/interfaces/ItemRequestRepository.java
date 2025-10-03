package ru.practicum.shareit.request.interfaces;

import org.apache.coyote.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.ItemRequest;


public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {


}
