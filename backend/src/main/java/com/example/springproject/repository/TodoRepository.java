package com.example.springproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.springproject.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // @Query("SELECT t FROM todos t WHERE t.name = :name AND t.user.userId =
    // :userId")
    // Optional<Todo> findByNameAndUserUserId(@Param("name") String name,
    // @Param("userId") Long userId);

    Optional<Todo> findByNameAndUser_Id(String name, Long userId);
}