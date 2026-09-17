package com.frame.base.business;

import com.frame.base.dto.ResponseDto;
import com.frame.base.services.IBaseService;
import io.swagger.v3.core.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BaseBusiness<R,T> implements IBusiness<R,T> {

    protected IBaseService IService;

    public BaseBusiness(IBaseService IService) {
        this.IService = IService;
    }

    /*
    @Override
    public R add(T entityDto) {
        return this.IService.add(entityDto);
    } */

    @Override
    public ResponseDto add(T entity) {
        return buildResponseDto(this.IService.add(entity));
    }

    @Override
    public ResponseDto update(T entityDto) {
        return buildResponseDto((R) this.IService.update(entityDto));
    }

    @Override
    public ResponseDto update(Long id, T entityDto) {
        T updatedDto = entityDto;
        try {
            Method withIdMethod = entityDto.getClass().getMethod("withId", Long.class);
            updatedDto = (T) withIdMethod.invoke(entityDto, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Record must implement a withId(Long) method", e);
        }
        return update(updatedDto);
    }

    @Override
    public ResponseDto delete(T entityDto) {
        return null;
    }

    @Override
    public ResponseDto findAll(Map<?, ?> map) {
        return null;
    }

    @Override
    public ResponseDto findSingle(Map<?, ?> map) {
        return buildResponseDto((R) this.IService.findDetail(map));
    }

    @Override
    public ResponseDto findAllData(Map<?, ?> map) {
        return buildResponseDto(this.IService.find(map));
    }

    @Override
    public ResponseDto deleteData(Map<?, ?> map) {
        return buildResponseDto(this.IService.deleteList(map));
    }

    @Override
    public ResponseDto deleteData(Long id, Map<?, ?> map) {
        if( map == null) {
            map = new HashMap<>();
        }
        Map<String, Object> mutableMap = new HashMap(map);
        // Put the ID parameter
        mutableMap.put((String) this.IService.getIdName(), id);
        return deleteData(mutableMap);
    }

    public ResponseDto buildResponseDto(Object responseDto)
    {
        return new ResponseDto(
                "200",
                "Success",
                responseDto
        );
    }
}
