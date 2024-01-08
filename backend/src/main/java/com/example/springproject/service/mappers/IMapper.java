package com.example.springproject.service.mappers;

public interface IMapper<T, S> {

    S mapFromEntity(T a);

    T mapToEntity(S b);

}
