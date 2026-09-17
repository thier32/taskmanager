package com.frame.base.business;

import com.frame.base.dto.PageResponse;
import com.frame.base.dto.ResponseDto;

import java.util.List;
import java.util.Map;

public interface IBusiness<R,T> {
   ResponseDto add(T entityDto);
   ResponseDto update(T entityDto);
   ResponseDto update(Long id,T entityDto);
   ResponseDto delete(T entityDto);
   ResponseDto findAll(Map<?, ?> map);
   ResponseDto findSingle(Map<?, ?> map);

   ResponseDto findAllData(Map<?, ?> map);
   ResponseDto deleteData(Map<?, ?> map);
   ResponseDto deleteData(Long id,Map<?, ?> map);

}
