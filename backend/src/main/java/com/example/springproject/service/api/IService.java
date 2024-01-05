package com.example.springproject.service.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IService<T, S> {
    T getById(Long id);

    Page<T> getAll(Pageable pageable);

    void delete(Long id);

    T update(Long id, S dto);

}
