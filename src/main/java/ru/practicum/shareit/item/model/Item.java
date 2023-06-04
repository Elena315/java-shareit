package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;
    @Column(name = "item_name", nullable = false, length = 255)
    private String name;
    @Column(name = "description", nullable = false, length = 255)
    private String description;
    @Column(name = "available", nullable = false)
    private Boolean available;
    @OneToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;
}