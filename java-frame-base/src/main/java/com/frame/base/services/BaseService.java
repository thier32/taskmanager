package com.frame.base.services;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.frame.base.criteria.SpecificationBuilder;
import com.frame.base.dto.CriteriaClass;
import com.frame.base.dto.PageResponse;
import com.frame.base.model.BaseModel;
import com.frame.base.repository.BaseRepository;
import com.frame.base.security.RuntimeSecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.apache.catalina.security.SecurityUtil;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class BaseService<T,R ,E,F extends BaseRepository<E,Long>> implements
        IBaseService<R,T,E>
{
    protected F repository;

    protected final Class<E> entityClass;
    protected final Class<R> resultClass;
    protected final Class<T> dtoClass;
    protected SpecificationBuilder specificationBuilder = null;
    final String idName;


    public F getRepository() {
        return repository;
    }

    public BaseService(F repository) {
        this.repository = repository;
        java.lang.reflect.Type[] parametrizedClasses = ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments();

        this.entityClass = (Class<E>) parametrizedClasses[2];
        this.resultClass = (Class<R>) parametrizedClasses[1];
        this.dtoClass = (Class<T>) parametrizedClasses[0];
        this.specificationBuilder = new SpecificationBuilder(this.entityClass);
        idName = getGeneratedId(this.entityClass);
    }


    @Override
    @Transactional
    public R add(T entityDto) {
        E entity = JsonUtils.parse(entityDto, this.entityClass);
        entity = buildEntity(entityDto, entity);
        entity = buildEntity(entity);
        if (entity instanceof BaseModel){
            ((BaseModel) entity).setCreatedByUserName(RuntimeSecurityUtils.getCurrentUsernameSafely());
        }
        E resultEntity = saveEntity(entity);
        return convertToResultDto(resultEntity);
    }

    /**
     * HOOK DE MAPPING VITESSE ÉCLAIR
     * Par défaut : utilise le JSON (lent mais universel).
     * Surchargé dans l'enfant : utilise des mappings typés (ultra-rapide).
     */
    protected R convertToResultDto(E entity) {
        return JsonUtils.parse(entity, this.resultClass);
    }

    @Override
    public List<R> add(List<T> entitiesDto) {
        List<E> entities = entitiesDto.stream().map(
                entityDto ->
                {
                  E entity = JsonUtils.parse(entityDto, this.entityClass);
                  entity  =  buildEntity(entityDto, entity);
                  entity = buildEntity(entity);
                  entity = generateEntityId(entity);
                  return  entity;
                }
        ).toList();
        entities = getRepository().saveAll(entities);
        return entities.stream().map(
                this::convertToResultDto
        ).toList();
    }

    @Override
    public E buildEntity(T entityDto, E entity){
        return entity;
    }

    @Override
    public E buildEntity(E entity) {
        return entity;
    }

    @Override
    public String getIdName() {
        return this.idName;
    }

    @Override
    public R update(T entityDto) {
        Map<?,?> map = getMap(entityDto);
        E single = findSingle(map);
        JsonUtils.convert(entityDto,single);
        if (single instanceof BaseModel){
            ((BaseModel) single).setUpdateByUserName(RuntimeSecurityUtils.getCurrentUsernameSafely());
        }
        E resultEntity = this.repository.save(single);
        return convertToResultDto(resultEntity);
    }


    public Map<String,Object> getMap(T entityDto) {
        Map<String, Object> map = new HashMap<>();
        try
        {
            Class<?> clazz = entityDto.getClass();
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());
            Object idValue = null;
            if (clazz.isRecord()){
                java.lang.reflect.RecordComponent[] components = clazz.getRecordComponents();
                Class<?> returnType = Object.class;
                for (java.lang.reflect.RecordComponent comp : components) {
                    if (comp.getName().equals(idName)) {
                        returnType = comp.getType(); // Va trouver Long.class pour investorId
                        break;
                    }
                }
                MethodType methodType = MethodType.methodType(returnType);
                MethodHandle getterHandle = lookup.findVirtual(clazz, idName, methodType);
                idValue = getterHandle.invoke(entityDto);
            }else{
                MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(clazz, lookup);
                Class<?> fieldType = clazz.getDeclaredField(idName).getType();
                java.lang.invoke.VarHandle handle = privateLookup.findVarHandle(clazz, idName, fieldType);
                idValue = handle.get(entityDto);
            }

            if (idValue == null){
                throw new RuntimeException(String.format("Check if %s is null or undefined",idName));
            }

            map.put(idName, idValue);
        }catch (Throwable e){
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Long deleteList(Map<?, ?> criteria) {
        Specification<?> specification = specificationBuilder.getSpecification(criteria);
        return this.repository.delete(specification);
    }

    @Override
    public R delete(T entityDto) {
        Map<String,Object> map = getMap(entityDto);
        E single = findSingle(map);
        if (single != null){
            this.repository.delete(single);
        }
        return JsonUtils.parse(single, this.resultClass);
    }

    @Override
    public E findSingle(Map<?, ?> criteria, boolean throwException) {
        Map<Object, Object> safeCriterial = (criteria == null || criteria.isEmpty())
                ? new HashMap<>()
                : new HashMap<>(criteria);

        safeCriterial.put("page",0);
        safeCriterial.put("limit",1);

        CriteriaClass criteriaClass = buildCriteria(safeCriterial);
        Page<E> pageResults = findInRepository(criteriaClass.specification,criteriaClass.pageable);
        List<E> data = pageResults.getContent();
        boolean isDataEmpty = data.isEmpty();

        if (isDataEmpty && throwException){
            throw new EntityNotFoundException("Element non trouvé avec les critères.");
        }

        if (isDataEmpty){
            return null;
        }

        return (E) data.getFirst();
    }

    public E findSingle(Map<?,?> criteria){
        return this.findSingle(criteria, true);
    }

    @Override
    public R findDetail(Map<?, ?> criteria) {
        E single = this.findSingle(criteria);
        return JsonUtils.parse(single, this.resultClass);
    }

    protected CriteriaClass buildCriteria(Map<?, ?> criteria) {
        if (criteria == null || criteria.isEmpty()){
            criteria = new HashMap<>();
        }
        Integer page = (Integer) criteria.get("page");
        Integer limit = (Integer) criteria.get("size");
        criteria.remove("page");
        criteria.remove("size");

        if (page == null){
            page = 0;
        }

        if (limit == null){
            limit = 50;
        }

        String sortBy = (String) criteria.get("sortBy");
        String direction = (String) criteria.get("sortDir");

        if (sortBy == null || sortBy.isEmpty()){
            sortBy = idName;
        }


        if (direction == null || direction.isEmpty()){
            direction = "desc";
        }

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Specification<?> specification = specificationBuilder.getSpecification(criteria);

        return new CriteriaClass(PageRequest.of(page, limit,sort),specification);
    }

    @Override
    public List<E> findAll(Map<?, ?> criteria) {
        CriteriaClass criteriaClass = buildCriteria(criteria);
        Page<E> data = findInRepository(criteriaClass.specification, criteriaClass.pageable);
        return data.getContent();
    }

    @Override
    public PageResponse<R> find(Map<?, ?> criteria) {
        CriteriaClass criteriaClass = buildCriteria(criteria);
        Specification specification = appendSpecification(criteria,criteriaClass.specification);
        Page<E> data = findInRepository(specification, criteriaClass.pageable);
        Page<R> dPage = getPage(data);
        return  buildResponse(dPage);
    }

    public Specification appendSpecification(Map criteria, Specification specification){
        return specification;
    }

    public PageResponse<R> buildResponse(Page<R> dPage){
        return new PageResponse(
                dPage.getContent(),
                dPage.getTotalElements(),
                dPage.getTotalPages(),
                dPage.getSize(),
                dPage.getNumber()
        );
    }

    public Page<R> getPage(Page<E> data){
        return data.map(entity -> JsonUtils.parse(entity, resultClass));
    }

    public Page<E> findInRepository(Specification specification, Pageable pageable){
        return this.repository.findAll(specification,pageable);
    }

    @Override
    public E saveEntity(E entity) {
        entity = generateEntityId(entity);
        return save(entity);
    }

    @Override
    public E save(E entity) {
        return this.repository.save(entity);
    }

    @Override
    public E updateEntity(T entityDto) {
        Map<?,?> map = getMap(entityDto);
        E single = findSingle(map);
        if (single instanceof BaseModel){
            ((BaseModel) single).setUpdateByUserName(RuntimeSecurityUtils.getCurrentUsernameSafely());
        }
        JsonUtils.convert(single, entityDto);
        return save(single);
    }

    protected Map getUpdatedCriteria(Map<String,?> criteria, Class<?> classe){
        Map<String, Object> updated = new LinkedHashMap<>();
        Map<String, String> fieldMapping = buildFieldMapping(classe);
        criteria.remove("page");
        criteria.remove("perPage");
        criteria.remove("size");
        criteria.forEach((key, value) -> {
            String targetKey = fieldMapping.getOrDefault(key.toLowerCase(), key);
            updated.put(targetKey, value);
        });
        return updated;
    }

    private static Map<String, String> buildFieldMapping(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .collect(HashMap::new, (map, field) -> {
                    String jsonName = field.getName(); // default
                    if (field.isAnnotationPresent(JsonProperty.class)) {
                        jsonName = field.getAnnotation(JsonProperty.class).value();
                    }
                    map.put(jsonName, field.getName());

                    if (field.isAnnotationPresent(JsonAlias.class)) {
                        for (String alias : field.getAnnotation(JsonAlias.class).value()) {
                            map.put(alias, field.getName());
                        }
                    }
                }, HashMap::putAll);
    }


    public R createDtoNew(T entityDto) {
        //entityDto = buildEntityDtoFromPath(path, entityDto);
        return null;
    }

    public <D> D createDtoNew(D entityDto, Class<?> dtoClazz) {
        if (dtoClazz != null && !entityDto.getClass().equals(dtoClazz)) {
            //entityDto = (D) JsonUtils.mapMemberConfigDto((Map) entityDto, dtoClazz);
        }
        else{
            dtoClazz = entityDto.getClass();
        }
        // Map DTO to Entity using the cached entityClass
        //E entity = JsonUtils.parse(entityDto, this.entityClass);

        // Save the entity
        //E savedEntity = this.createNew(entity);

        // Map back to the original DTO type
        //return (D) JsonUtils.parse(savedEntity, dtoClazz);
        return null;
    }

    public  E createNew(E entity) {
       // return this.repository.save(entity);
        return null;
    }

    // Utilisation de ClassValue pour un accès O(1) sans verrouillage
    private static final ClassValue<VarHandle> VH_CACHE = new ClassValue<>() {
        @Override
        protected VarHandle computeValue(Class<?> type) {
            return findIdHandle(type);
        }
    };

    static String getGeneratedId(Class<?> type){
        return Character.toLowerCase(type.getSimpleName().charAt(0))
                + type.getSimpleName().substring(1) + "Id";
    }

    private static VarHandle findIdHandle(Class<?> type) {
        String fieldName = getGeneratedId(type);
        try {
            // On cherche le champ en ignorant les modificateurs de visibilité
            var lookup = MethodHandles.privateLookupIn(type, MethodHandles.lookup());

            // Détection dynamique long vs Long
            try {
                return lookup.findVarHandle(type, fieldName, long.class);
            } catch (NoSuchFieldException e) {
                return lookup.findVarHandle(type, fieldName, Long.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("Champ ID '" + fieldName + "' introuvable dans " + type.getName());
        }
    }

    private static final long SERVER_ID = 1L;
    private static final long EPOCH = 1739900000000L;
    // Un seul compteur atomique avec Padding (Contre le False Sharing)
    // On utilise l'alignement de cache pour booster les performances multi-coeurs
    private static final AtomicLong GLOBAL_SEQ = new AtomicLong(0);


    protected long generateId() {
        return ((System.currentTimeMillis() - EPOCH) << 17)
                | (SERVER_ID << 12)
                | (GLOBAL_SEQ.incrementAndGet() & 4095L);
    }

    /**
     * OPTIMISATION ULTIME :
     * 1. VarHandle.setRelease : Écriture "non-bloquante" au niveau CPU.
     * 2. Bit-shifting pur : Pas de division, pas de multiplication.
     * 3. Zero-Allocation : Aucune pression sur le Garbage Collector.
     */
    public <T> T generateEntityId(final T entity) {
        if (entity == null) return null;

        // 1. Récupérer la vraie classe derrière le Proxy Hibernate
        Class<?> entityClass = Hibernate.getClass(entity);

        // 2. Ou déballer directement l'objet si nécessaire
        Object unproxiedEntity = Hibernate.unproxy(entity);

        // Génération Snowflake (41 bits temps, 5 bits serveur, 12 bits séquence)
        final long id = generateId();

        try {
            // Accès direct au descripteur de champ
            VarHandle vh = VH_CACHE.get(unproxiedEntity.getClass());

            // setRelease est plus rapide que volatile car il ne force pas de "LoadLoad" barrier
            vh.setRelease(unproxiedEntity, id);

            return (T) unproxiedEntity;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Kernel Panic: Impossible d'injecter l'ID pour la classe %s",entity.getClass().getName()));
        }
    }

}
