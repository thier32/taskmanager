package com.frame.base.services;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JsonUtils {

    // Shared thread-safe mapper (expensive to create, reuse it!)
    private static final ObjectMapper mapper = new ObjectMapper()
            // 1. Essential: Ignore extra fields (the "name" error fix)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            // 2. Performance: Don't fail if an object is empty
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            // 3. Performance: Ignore nulls to keep JSON small
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new JavaTimeModule())
            // 4. Critical for 1M rows: Handle Hibernate lazy loading
            .registerModule(new ParameterNamesModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);;

    private static final ConcurrentHashMap<Class<?>, ObjectReader> READER_CACHE = new ConcurrentHashMap<>();

    /**
     * For simple classes: UserRequest.class
     */
    public static <T> T parse(HttpServletRequest request, Class<T> clazz) {
        try {
            return mapper.readValue(request.getInputStream(), clazz);
        } catch (IOException e) {
            throw new RuntimeException("Mapping failed for " + clazz.getSimpleName(), e);
        }
    }


    public static <S, T> T parse(S data, Class<T> clazz) {
        if (data == null) return null;
        // convertValue is faster than serializing to String and back
        return mapper.convertValue(data, clazz);
    }



    public static  <T> T clone(T source) {
        if (source == null) return null;
        // This serializes and deserializes in memory, creating a brand new instance
        return (T) mapper.convertValue(source, source.getClass());
    }

    /**
     * For generic types: new TypeReference<List<UserRequest>>(){}
     */
    public static <T> T parse(HttpServletRequest request, TypeReference<T> typeRef) {
        try {
            return mapper.readValue(request.getInputStream(), typeRef);
        } catch (IOException e) {
            throw new RuntimeException("Mapping failed for generic type", e);
        }
    }

    public static <T> T convertMap(T source, Map dest) {
        try {
            ObjectReader reader = READER_CACHE.computeIfAbsent(source.getClass(), mapper::readerForUpdating);
            // On convertit la map directement dans l'arbre d'update, 0 String générée
            return reader.readValue(mapper.writeValueAsBytes(dest));
        } catch (IOException e) {
            throw new RuntimeException("Mapping failed for generic type", e);
        }
        //return source;
    }

    public static <T,D> D convert(T source, D dest) {
        if (source == null || dest == null) return dest;
        READER_CACHE.clear();
        try {
            // 1. Récupération ou création d'un lecteur d'update réutilisable (Évite la réflexion CPU)
            //ObjectReader reader = READER_CACHE.computeIfAbsent(dest.getClass(), mapper::readerForUpdating);

            ObjectReader reader = READER_CACHE.computeIfAbsent(
                    dest.getClass(),
                    clazz -> mapper.readerForUpdating(dest)
            );

            // 2. treeAsTokens est l'équivalent d'un transfert direct de mémoire à mémoire dans Jackson.
            // On convertit l'objet source en nœuds de tokens, et on l'injecte directement dans le lecteur.
            return  reader.readValue(mapper.treeAsTokens(mapper.valueToTree(source)));
        } catch (IOException e) {
            throw new RuntimeException("Mapping failed between types", e);
        }
    }

    private static final Map<Class<?>, Map<String, MethodHandle>> HANDLE_CACHE = new ConcurrentHashMap<>();
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();


}