package com.example.springproject.entity;

import java.time.LocalDate;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "todos")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Todo {

    public enum Status {
        OPEN, FINISHED
    }

    public enum Priority {
        LOW, MID, HIGH
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nonnull
    @NotBlank(message = "name cannot be blank")
    @Column(nullable = false, unique = false)
    private String name;

    @Nonnull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    @Nonnull
    @Future(message = "The deadline must be in the future")
    @Column(nullable = false)
    // @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate untilDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
