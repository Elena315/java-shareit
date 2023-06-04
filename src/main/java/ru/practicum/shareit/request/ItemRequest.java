package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}