package com.frame.base.criteria;

import jakarta.persistence.criteria.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AndFilter extends AbstractFilter{

    private List<AbstractFilter> filters;

    public AndFilter(List<AbstractFilter> filters) {
        super();
        this.filters = filters;
    }

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> attributeToJoin) {
        return criteriaBuilder.and(filters.stream().map(filter -> filter.toPredicate(root,query,criteriaBuilder,attributeToJoin)).collect(Collectors.toList()).toArray(Predicate[]::new));
    }

    public List<AbstractFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<AbstractFilter> filters) {
        this.filters = filters;
    }
}
