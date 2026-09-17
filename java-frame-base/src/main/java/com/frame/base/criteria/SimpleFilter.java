package com.frame.base.criteria;

import jakarta.persistence.criteria.*;
import java.util.Map;

public class SimpleFilter extends AbstractFilter{
    private Filter filter;

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> attributeToJoin) {
        return getPredicateFromFilter(filter,root,criteriaBuilder,attributeToJoin);
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Filter getFilter() {
        return filter;
    }
}
