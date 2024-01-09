package com.andreasx42.taskmanagerapi.service.mapper;

public interface IMapper<T, S> {

    S mapFromEntity(T a);

    T mapToEntity(S b);

}
