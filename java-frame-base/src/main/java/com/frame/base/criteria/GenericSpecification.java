package com.frame.base.criteria;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;


public class GenericSpecification<T> implements Specification<T> {

    private AbstractFilter filter;
    private JoinDataSupplier<T> joinDataSupplier;
    private Specification<T> customPredicateProvider; // Permet de stocker une sous-requête lambda

    public void setCustomPredicateProvider(Specification<T> customPredicateProvider) {
        this.customPredicateProvider = customPredicateProvider;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        // Si on a un fournisseur de prédicat personnalisé (comme notre sous-requête générique)
        if (customPredicateProvider != null) {
            return customPredicateProvider.toPredicate(root, query, criteriaBuilder);
        }

        if (joinDataSupplier != null && filter !=null) {
            return filter.toPredicate(root, query, criteriaBuilder, joinDataSupplier.getJoinData(root,query));
        }

        if (filter !=null) {
            return filter.toPredicate(root, query, criteriaBuilder, null);
        }

        return criteriaBuilder.conjunction();
    }

    public AbstractFilter getFilter() {
        return filter;
    }

    public void setFilter(AbstractFilter filter) {
        this.filter = filter;
    }

    public JoinDataSupplier<T> getJoinDataSupplier() {
        return joinDataSupplier;
    }

    public void setJoinDataSupplier(JoinDataSupplier<T> joinDataSupplier) {
        this.joinDataSupplier = joinDataSupplier;
    }


}
