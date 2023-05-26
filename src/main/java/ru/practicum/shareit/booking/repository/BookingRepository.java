package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(long userId);

    List<Booking> findByBookerId(long userId, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(long userId, LocalDateTime start,
                                                              LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(long userId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(long userId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndStatus(long userId, Status status, Sort sort);

    List<Booking> findByItemOwnerId(long userI, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(long userI, LocalDateTime start,
                                                                 LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndEndIsBefore(long userI, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndStartIsAfter(long userI, LocalDateTime start, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(long userI, Status status, Sort sort);

    List<Booking> searchBookingByBookerIdAndItemIdAndEndIsBefore(long id, long itemId, LocalDateTime time);

    Optional<Booking> findTopByItemOwnerIdAndStatusAndStartBeforeOrderByEndDesc(long id, Status status, LocalDateTime time);

    Optional<Booking> findTopByItemOwnerIdAndStatusAndStartAfterOrderByStartAsc(long id, Status status, LocalDateTime time);

    List<Booking> findBookingByItemIdOrderByStartAsc(long itemId);
}