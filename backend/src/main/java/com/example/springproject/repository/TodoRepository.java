package com.example.springproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springproject.entity.Todo;

public interface TodoRepository extends JpaRepository<Todo, Long> {

}
