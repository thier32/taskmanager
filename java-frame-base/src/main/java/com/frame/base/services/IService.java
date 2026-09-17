package com.frame.base.services;

import com.frame.base.dto.PageResponse;

import java.util.List;
import java.util.Map;

public interface IService<R,T,E> extends IRService {
   R add(T entityDto);
   List<R> add(List<T> entitiesDto);
   //List<R> find(Map<?,?> criteria);
   PageResponse<R> find(Map<?,?> criteria);
   List<E> findAll(Map<?,?> criteria);
   R findDetail(Map<?,?> criteria);
   E findSingle(Map<?,?> criteria);
   E findSingle(Map<?,?> criteria, boolean throwException);
   E saveEntity(E entity);
   E save(E entity);
   E updateEntity(T entityDto);
   R update(T entityDto);
   R delete(T entityDto);
   Map<String,Object> getMap(T entityDto);
   Long deleteList(Map<?,?> criteria);

}
