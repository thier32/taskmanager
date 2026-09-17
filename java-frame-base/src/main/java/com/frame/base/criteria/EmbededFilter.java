package com.frame.base.criteria;

import jakarta.persistence.criteria.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EmbededFilter extends AbstractFilter{

    private List<AbstractFilter> filters;
    private String className;
    private Class clazz;
    public EmbededFilter(List<AbstractFilter> filters) {
        super();
        this.filters = filters;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
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
