package com.example.springproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springproject.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Optional<Todo> findByNameAndUser_Id(String name, Long userId);

    List<Todo> findByUser_Id(Long userId);
}