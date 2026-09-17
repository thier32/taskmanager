package com.frame.base.criteria;

import jakarta.persistence.criteria.*;
import org.springframework.util.Assert;

import java.util.Map;

public abstract class AbstractFilter {
    public abstract Predicate toPredicate(Root root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> attributeToJoin);

    public Predicate getPredicate(Filter filter, CriteriaBuilder criteriaBuilder, Path expression) {

        Predicate predicate = null;
        switch (filter.getOperator()) {
            case EQUAL_TO:{
                Object value = filter.getValue().toString();
                if (filter.isValidDate(filter.getValue().toString())){
                    value = filter.getDate(value.toString());
                }
                predicate = criteriaBuilder.equal(expression, value);
                break;
            }
            case LIKE:
                predicate = criteriaBuilder.like(expression, "%" + filter.getValue() + "%");
                break;
            case IN:
                Object value = filter.getValue().toString();
                if (filter.isValidDate(filter.getValue().toString())){
                    Object value2 = filter.getValue2();
                    if (value2 != null && filter.isValidDate(filter.getValue2().toString())){
                        value2 = filter.getDate(value2.toString());
                        predicate = criteriaBuilder.between(expression,(Comparable) value, (Comparable) value2);
                        break;
                    }
                    predicate = criteriaBuilder.greaterThanOrEqualTo(expression, (Comparable) filter.getValue());
                    break;
                }
                predicate = criteriaBuilder.in(expression).value(filter.getValue());
                break;
            case GT:
                predicate = criteriaBuilder.greaterThan(expression, (Comparable) filter.getValue());
                break;
            case LT:
                predicate = criteriaBuilder.lessThan(expression, (Comparable) filter.getValue());
                break;
            case GTE:
                predicate = criteriaBuilder.greaterThanOrEqualTo(expression, (Comparable) filter.getValue());
                break;
            case LTE:
                predicate = criteriaBuilder.lessThanOrEqualTo(expression, (Comparable) filter.getValue());
                break;
            case NOT_EQUAL:
                predicate = criteriaBuilder.notEqual(expression, filter.getValue());
                break;
            case IS_NULL:
                predicate = criteriaBuilder.isNull(expression);
                break;
            case NOT_NULL:
                predicate = criteriaBuilder.isNotNull(expression);
                break;
            default:
               // log.error("Invalid Operator");
                throw new IllegalArgumentException(filter.getOperator() + " is not valid operator");
        }
        return predicate;
    }

    public Predicate getPredicateFromFilter(Filter filter, Root root, CriteriaBuilder criteriaBuilder, Map<String, Join<Object, Object>> attributeToJoin) {
        Assert.notNull(filter,"Filter must not be null");
        if (attributeToJoin != null && attributeToJoin.get(filter.getEntityName()) != null) {
            return  getPredicate(filter, criteriaBuilder, attributeToJoin.get(filter.getEntityName()).get(filter.getField()));
        } else {
            return getPredicate(filter, criteriaBuilder, root.get(filter.getField()));
        }
    }
}
