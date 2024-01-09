package com.andreasx42.taskmanagerapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.andreasx42.taskmanagerapi.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    Optional<Todo> findByNameAndUser_Id(String name, Long userId);

    List<Todo> findByUser_Id(Long userId);

    Page<Todo> findAllByUser_Id(Long userId, Pageable pageable);

}