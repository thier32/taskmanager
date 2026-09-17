package com.frame.base.controller;

import com.frame.base.business.IBusiness;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.frame.base.constant.SystemRoutes.*;

public class BaseController<R,P> {

    protected IBusiness<R,P> business;

    @PostMapping()
    public ResponseEntity $New(
            @RequestBody P entityDto) {
        return ResponseEntity.ok(this.business.add(entityDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity $Edit(
            @PathVariable Long id,
            @RequestBody P entityDto
    ){
        return ResponseEntity.ok(this.business.update(id,entityDto));
    }

    @GetMapping()
    public  ResponseEntity $List(
            @RequestBody(required = false) Map<?,?> criteria)
    {
        return ResponseEntity.ok(this.business.findAllData(criteria));
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity $Delete(
            @PathVariable Long id,
            @RequestBody(required = false) Map<?,?> criteria)
    {
        return ResponseEntity.ok(this.business.deleteData(id,criteria));
    }

    //@PostMapping(SINGLE_ROUTE)
    public ResponseEntity $Single(
            @RequestBody(required = false) Map<?,?> criteria){
        return ResponseEntity.ok(this.business.findSingle(criteria));
    }
}
