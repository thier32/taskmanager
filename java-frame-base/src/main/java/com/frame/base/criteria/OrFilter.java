package com.frame.base.criteria;

import jakarta.persistence.criteria.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrFilter extends AbstractFilter{

    private List<AbstractFilter> filters;

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> attributeToJoin) {
        return criteriaBuilder.or(filters.stream().map(filter -> filter.toPredicate(root,query,criteriaBuilder,attributeToJoin)).collect(Collectors.toList()).toArray(Predicate[]::new));
    }
}
