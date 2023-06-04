package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    //Создание вещи
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор пользователя"));

        item.setOwner(user);

        Long requestId = itemDto.getRequestId();
        if (requestId != null) {
            item.setItemRequest(itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Неверный идентификатор запроса")));
        }

        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    //Обновление вещи
    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор вещи"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нельзя изменить чужую вещь");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    //Получение вещи
    @Override
    public ItemDtoBooking getItem(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор вещи"));

        ItemDtoBooking itemDtoBooking = ItemMapper.toItemDtoWithBooking(item);

        if (item.getOwner().getId().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();
            BookingDtoForItem lastBooking = bookingRepository
                    .findTopByItemOwnerIdAndStatusAndStartBeforeOrderByEndDesc(userId, Status.APPROVED, now)
                    .map(BookingMapper::toBookingDtoForItem)
                    .orElse(null);

            BookingDtoForItem nextBooking = bookingRepository
                    .findTopByItemOwnerIdAndStatusAndStartAfterOrderByStartAsc(userId, Status.APPROVED, now)
                    .map(BookingMapper::toBookingDtoForItem)
                    .orElse(null);

            itemDtoBooking.setLastBooking(lastBooking);
            itemDtoBooking.setNextBooking(nextBooking);
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        if (!comments.isEmpty()) {
            itemDtoBooking.setComments(comments
                    .stream().map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList())
            );
        }

        return itemDtoBooking;
    }

    //Получение всех вещей пользователя
    @Override
    public List<ItemDtoBooking> getAllItemsByUser(Long userId, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        List<Item> userItemList = itemRepository.findByOwnerId(userId, pageable).stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
        return userItemList.stream()
                .map(ItemMapper::toItemDtoWithBooking)
                .peek(item -> {
                    List<Booking> bookings = bookingRepository.findBookingByItemIdOrderByStartAsc(item.getId());
                    LocalDateTime now = LocalDateTime.now();
                    BookingDtoForItem lastBooking = null;
                    BookingDtoForItem nextBooking = null;

                    for (Booking booking : bookings) {
                        if (booking.getEnd().isBefore(now)) {
                            lastBooking = BookingMapper.toBookingDtoForItem(booking);
                        } else if (booking.getStart().isAfter(now)) {
                            nextBooking = BookingMapper.toBookingDtoForItem(booking);
                            break;
                        }
                    }
                    item.setLastBooking(lastBooking);
                    item.setNextBooking(nextBooking);

                    List<Comment> comments = commentRepository.findAllByItemId(item.getId());

                    if (!comments.isEmpty()) {
                        item.setComments(comments
                                .stream().map(CommentMapper::toCommentDto)
                                .collect(Collectors.toList()));
                    }

                })
                .collect(Collectors.toList());
    }

    //Поиск вещи
    @Override
    public List<ItemDto> search(String text, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        if (!text.isBlank()) {
            return itemRepository.search(text, pageable)
                    .stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

  /*  private void createItemDtoWithBooking(ItemDtoBooking itemDtoBooking) {
        List<Booking> lastBookings = bookingRepository
                .findBookingsByItemIdAndEndIsBeforeOrderByEndDesc(itemDtoBooking.getId(),
                        LocalDateTime.now());

        if (!lastBookings.isEmpty()) {
            BookingDtoForItem lastBooking = BookingMapper.toBookingDtoForItem(lastBookings.get(0));
            itemDtoBooking.setLastBooking(lastBooking);
        }

        List<Booking> nextBookings = bookingRepository
                .findBookingsByItemIdAndStartIsAfterOrderByStartDesc(itemDtoBooking.getId(),
                        LocalDateTime.now());

        if (!nextBookings.isEmpty()) {
            BookingDtoForItem nextBooking = BookingMapper.toBookingDtoForItem(nextBookings.get(0));
            itemDtoBooking.setNextBooking(nextBooking);
        }
    }*/

    //Добавление комментария
    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор вещи"));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор пользователя"));

        bookingRepository.searchBookingByBookerIdAndItemIdAndEndIsBeforeAndStatus(userId, itemId,
                        LocalDateTime.now(), Status.APPROVED)
                .stream()
                .filter(booking -> booking.getStatus().equals(Status.APPROVED)).findAny()
                .orElseThrow(() -> new ValidationException("Пользователь с id = " + userId + " не брал в аренду вещь с id = " + itemId));

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);

        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }
}