package com.frame.base.criteria;

import jakarta.persistence.Transient;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpecificationBuilder {

    private Class<?> entityClass;

    public SpecificationBuilder(){

    }
    public SpecificationBuilder(Class<?> clazz){
        this();
        this.entityClass = clazz;
    }

    public Specification buildSpecification(Map criteria) {
        return getSpecification(criteria);
    }

    public AbstractFilter getRequestFilter(Map<?,?> criteria){
        List<AbstractFilter> filters = new ArrayList<>();
        boolean isEmbebed = false;
        String className = null;
        for (Map.Entry criterion : criteria.entrySet()){
            if (criterion.getValue() == null || criterion.getValue().toString().isEmpty()){
                continue;
            }
            String fieldName = criterion.getKey().toString();
            if (!hasValidEntityField(entityClass,fieldName)){
                if (!fieldName.endsWith("Id") && !fieldName.endsWith("id")){
                    continue;
                }
                String fieldName1 = fieldName.substring(0, fieldName.length()-2);
                if (!hasValidEntityField(entityClass,fieldName1))
                {
                    continue;
                }
                isEmbebed = true;
                className = fieldName1;
                //fieldName = String.format("%s.%s",fieldName1,"id");
            }

            String entityName = entityClass.getSimpleName().toLowerCase().replace("service","");
            Filter filter = new Filter();
            if (filter.isValidDate(criterion.getValue().toString())){
                filter = new Filter(fieldName,
                        FilterOperator.EQUAL_TO,criterion.getValue().toString(),
                        entityName
                );
            }else if (filter.isNumber(criterion.getValue())){
                filter = new Filter(fieldName,
                        FilterOperator.EQUAL_TO,criterion.getValue(),
                        entityName
                );
            }else if (filter.isList(criterion.getValue())){
                filter = new Filter(fieldName,
                        FilterOperator.IN,criterion.getValue(),
                        entityName
                );
            }
            else{
                filter = new Filter(fieldName,
                        FilterOperator.LIKE,criterion.getValue().toString(),
                        entityName
                );
            }


            SimpleFilter simpleFilter = new SimpleFilter();
            simpleFilter.setFilter(filter);
            filters.add(simpleFilter);
        }

        if (isEmbebed){
            EmbededFilter embededFilter = new EmbededFilter(filters);
            embededFilter.setClassName(className);
            return embededFilter;
        }
        return new AndFilter(filters);
    }

    /**
     * Vérifie si une classe (ou ses superclasses) possède un champ donné
     * ET vérifie que ce champ N'EST PAS annoté @Transient ni marqué transient en Java.
     */
    private boolean hasValidEntityField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                Field field = current.getDeclaredField(fieldName);

                // 1. Vérifie si le champ est annoté @Transient (JPA)
                if (field.isAnnotationPresent(Transient.class)) {
                    return false; // Champ ignoré par JPA
                }

                // 2. (Optionnel) Vérifie si le champ utilise le mot-clé Java 'transient'
                if (Modifier.isTransient(field.getModifiers())) {
                    return false;
                }

                return true; // Le champ existe et est valide pour les requêtes JPA/Criteria

            } catch (NoSuchFieldException e) {
                // Remonte dans la hiérarchie des classes (ex: BaseModel)
                current = current.getSuperclass();
            }
        }
        return false; // Champ inexistant
    }

    private static String convertCamelToSnake(String str) {
        return str.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    public  Specification<?> getSpecification(Map<?, ?> criteria){
        GenericSpecification specification = new GenericSpecification<>();
        AbstractFilter filter = getRequestFilter(criteria);
        if (filter instanceof EmbededFilter)
        {
            GenericSpecification<Object> embededSpec = new GenericSpecification<>();
            embededSpec.setFilter(filter);

            Specification<?> subquerySpec = autoResolveSubquery(((EmbededFilter) filter).getClassName(), embededSpec);

            specification.setCustomPredicateProvider((root, query, criteriaBuilder) ->
                    subquerySpec.toPredicate(root, query, criteriaBuilder)
            );
        }else{
            specification.setFilter(filter);
        }
        return specification;
    }


    /**
     *
     * @param relationName
     * @param subSpecification
     * @return
     * @param <T>
     * @param <Y>
     */
    public static <T, Y> Specification<?> autoResolveSubquery(
            String relationName,
            Specification<Y> subSpecification) {

        return (root, query, criteriaBuilder) -> {
            if (subSpecification == null) {
                return criteriaBuilder.conjunction();
            }

            // 1. AUTO-DÉCOUVERTE : On extrait dynamiquement la Classe de la sous-requête depuis le Root principal
            @SuppressWarnings("unchecked")
            Class<Y> subEntityClass = (Class<Y>) root.get(relationName).getJavaType();

            // 2. Définition de la sous-requête sur le type de la clé primaire (souvent Long)
            Subquery<Long> subquery = query.subquery(Long.class);

            // 3. Le subRoot est maintenant typé de manière complète et exacte
            Root<Y> subRoot = subquery.from(subEntityClass);

            // 4. Sélection de l'ID (Découverte dynamique ou "id" par défaut)
            subquery.select(subRoot.get("id"));

            // 5. Injection du subRoot complété dans la spécification d'origine
            subquery.where(subSpecification.toPredicate(subRoot, query, criteriaBuilder));

            // 6. Liaison finale
            return root.get(relationName).get("id").in(subquery);
        };
    }

    private boolean hasField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                current.getDeclaredField(fieldName);
                return true; // Le champ existe !
            } catch (NoSuchFieldException e) {
                // On cherche dans la classe parente (ex: BaseModel pour l'id, createdAt, etc.)
                current = current.getSuperclass();
            }
        }
        return false; // Le champ n'existe nulle part
    }
}
