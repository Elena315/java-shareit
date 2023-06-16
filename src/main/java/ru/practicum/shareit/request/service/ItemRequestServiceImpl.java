package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserRepository userRepository;

    //Создание запроса
    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Неверный идентификатор пользователя"));

        itemRequest.setRequestor(user);
        log.info("Создан запрос id={}", itemRequest.getId());
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    //Получение запросов пользователя
    @Override
    public List<ItemRequestDtoWithItems> getAll(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Неверный идентификатор пользователя"));

        List<ItemRequestDtoWithItems> itemRequestDtoWithItemsList = new ArrayList<>();

        itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId)
                .forEach(itemRequest -> itemRequestDtoWithItemsList.add(convertToItemRequestDtoWithItems(itemRequest)));
        log.info("Получен список всех запросов от пользователей");
        return itemRequestDtoWithItemsList;
    }

    //Получение запроса
    @Override
    public ItemRequestDtoWithItems getItemRequest(Long userId, Long itemRequestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Попробуйте другой идентификатор"));

        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(() ->
                new NotFoundException("Попробуйте другой идентификатор"));
        log.info("Получен запрос id={}", itemRequestId);
        return convertToItemRequestDtoWithItems(itemRequest);
    }

    //Получение запросов со страницами
    @Override
    public List<ItemRequestDtoWithItems> getAllWithPageable(Long userId, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("created"));

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Неверный идентификатор пользователя"));

        List<ItemRequest> requestList = itemRequestRepository.findAll(pageable)
                .stream()
                .filter(itemRequest -> !Objects.equals(itemRequest.getRequestor().getId(), userId))
                .collect(Collectors.toList());

        List<ItemRequestDtoWithItems> itemRequestDtoWithItemsList = new ArrayList<>();

        requestList.forEach(itemRequest -> itemRequestDtoWithItemsList.add(convertToItemRequestDtoWithItems(itemRequest)));
        log.info("Получен список всех запросов от пользователей, кроме id={}", userId);
        return itemRequestDtoWithItemsList;
    }

    private ItemRequestDtoWithItems convertToItemRequestDtoWithItems(ItemRequest itemRequest) {
        ItemRequestDtoWithItems dtoWithItems = itemRequestMapper.toItemRequestDtoWithItems(itemRequest);

        List<ItemDto> items = itemRepository.findAllByItemRequestId(dtoWithItems.getId())
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());

        if (!items.isEmpty()) {
            dtoWithItems.setItems(items);
        }
        return dtoWithItems;
    }
}