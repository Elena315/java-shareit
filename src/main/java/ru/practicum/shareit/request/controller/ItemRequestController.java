package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос к эндпоинту: '{} {}', Запрос на вещь: Описание: {}",
                "POST", "/requests",
                itemRequestDto.getDescription());
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoWithItems> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен GET-запрос к эндпоинту: '/requests' на получение запросов пользователя ID={}",
                userId);
        return itemRequestService.getAll(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItems> getAllWithPageable(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                            @RequestParam(defaultValue = "20") @Positive Integer size) {
        log.info("Получен GET-запрос к эндпоинту: '/requests/all' от пользователя с ID={} на получение всех запросов",
                userId);
        return itemRequestService.getAllWithPageable(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItems getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @Valid @PathVariable Long requestId) {
        log.info("Получен GET-запрос к эндпоинту: '/requests/requestId' на получение запроса с ID={}", requestId);
        return itemRequestService.getItemRequest(userId, requestId);
    }
}