package com.frame.base.services;

public interface IBaseService<R, T, E> extends IService<R,T,E>  {
    E buildEntity(T entityDto, E entity);

    E buildEntity(E entity);

    String getIdName();
}
