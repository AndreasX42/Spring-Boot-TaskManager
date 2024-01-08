package com.example.springproject.service.mapper;

public interface IMapper<T, S> {

    S mapFromEntity(T a);

    T mapToEntity(S b);

}
