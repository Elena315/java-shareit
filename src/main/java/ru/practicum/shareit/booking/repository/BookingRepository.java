package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(long userId);

    List<Booking> searchBookingByItemOwnerId(long id);

    List<Booking> searchBookingByBookerIdAndItemIdAndEndIsBefore(long id, long itemId, LocalDateTime time);

    List<Booking> searchBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc(long id, LocalDateTime time);

    Optional<Booking> findTopByItemOwnerIdAndStatusAndStartBeforeOrderByEndDesc(long id, Status status, LocalDateTime time);

    Optional<Booking> findTopByItemOwnerIdAndStatusAndStartAfterOrderByStartAsc(long id, Status status, LocalDateTime time);

}