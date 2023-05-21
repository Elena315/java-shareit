package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
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

    //Создание вещи
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор пользователя"));

        item.setOwner(user);

        itemRepository.save(item);
        return ItemMapper.toItemDto(item);
    }

    //Обновление вещи
    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор вещи"));

        if (userRepository.findById(userId).isEmpty() || !item.getOwner().getId().equals(userId)) {
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
            createItemDtoWithBooking(itemDtoBooking);
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
    public List<ItemDtoBooking> getAllItemsByUser(Long userId) {
        List<ItemDtoBooking> userItemList = itemRepository.findAll().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .map(ItemMapper::toItemDtoWithBooking)
                .collect(Collectors.toList());

        for (ItemDtoBooking itemDtoBooking : userItemList) {
            createItemDtoWithBooking(itemDtoBooking);

            List<Comment> comments = commentRepository.findAllByItemId(itemDtoBooking.getId());

            if (!comments.isEmpty()) {
                itemDtoBooking.setComments(comments
                        .stream().map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()));
            }
        }

        userItemList.sort(Comparator.comparing(ItemDtoBooking::getId));
        return userItemList;
    }

    //Поиск вещи
    @Override
    public List<ItemDto> search(String text) {
        if (!text.isBlank()) {
            return itemRepository.search(text)
                    .stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private void createItemDtoWithBooking(ItemDtoBooking itemDtoBooking) {
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
    }

    //Добавление комментария
    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор вещи"));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Неверный идентификатор пользователя"));

        bookingRepository.searchBookingByBookerIdAndItemIdAndEndIsBefore(userId, itemId, LocalDateTime.now())
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