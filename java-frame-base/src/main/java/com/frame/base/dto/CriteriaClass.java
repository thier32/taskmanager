package com.frame.base.dto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public class CriteriaClass{
    public final Pageable pageable;
    public Specification specification;
    public CriteriaClass(Pageable pageable, Specification specification){
        this.pageable = pageable;
        this.specification = specification;
    }
}
