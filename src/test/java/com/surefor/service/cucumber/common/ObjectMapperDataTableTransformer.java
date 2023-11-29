package com.surefor.service.cucumber.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.cucumber.cucumberexpressions.ParameterByTypeTransformer;
import io.cucumber.datatable.TableCellByTypeTransformer;
import io.cucumber.datatable.TableEntryByTypeTransformer;
import io.cucumber.java.DefaultDataTableCellTransformer;
import io.cucumber.java.DefaultDataTableEntryTransformer;
import io.cucumber.java.DefaultParameterTransformer;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class ObjectMapperDataTableTransformer implements ParameterByTypeTransformer, TableEntryByTypeTransformer, TableCellByTypeTransformer {
    private final ObjectMapper objectMapper;
    private final String systemTimeZoneId = "UTC";

    public ObjectMapperDataTableTransformer() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setTimeZone(TimeZone.getTimeZone(systemTimeZoneId));
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    @DefaultParameterTransformer
    @DefaultDataTableEntryTransformer
    @DefaultDataTableCellTransformer
    public Object transformer(Object fromValue, Type toValueType) {
        return objectMapper.convertValue(fromValue, objectMapper.constructType(toValueType));
    }

    @Override
    public Object transform(String s, Type type) {
        return objectMapper.convertValue(s, objectMapper.constructType(type));
    }

    //  @Override
    public <T> T transform(
            Map<String, String> map,
            Class<T> classOfT,
            TableCellByTypeTransformer tableCellByTypeTransformer) {
        return objectMapper.convertValue(map, classOfT);
    }

    //  @Override
    public <T> T transform(String s, Class<T> classOfT) {
        return objectMapper.convertValue(s, classOfT);
    }

    @Override
    public Object transform(Map<String, String> map, Type toValueType, TableCellByTypeTransformer cellTransformer) throws Throwable {
        Map<String, String> convertedMap = convertMap(map);
        return objectMapper.convertValue(convertedMap, objectMapper.constructType(toValueType));
    }

    private Map<String, String> convertMap(Map<String, String> map) {
        Set<String> keys = map.keySet();
        Map<String, String> outputMap = new Hashtable<String, String>();
        for (String key : keys) {
            String objectValue = map.get(key);
            String newkey = convertKey(key);
            outputMap.put(newkey, objectValue);
        }
        return outputMap;
    }

    private String convertKey(String key) {
        return camelCase(key);
    }

    private String camelCase(String key) {
        StringBuilder output = new StringBuilder();
        boolean previousSpace = false;
        if (key.length() < 1) {
            return "";
        }
        output.append(Character.toLowerCase(key.charAt(0)));
        for (int i = 1; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c != ' ') {
                if (previousSpace) {
                    output.append(Character.toUpperCase(c));
                } else {
                    output.append(c);
                }
                previousSpace = false;
            } else {
                previousSpace = true;
            }
        }
        return output.toString();
    }
}
