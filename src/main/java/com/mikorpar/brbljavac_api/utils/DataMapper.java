package com.mikorpar.brbljavac_api.utils;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataMapper extends ModelMapper {
    public DataMapper() {this.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);}

    public <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source
                .stream()
                .map(elem -> this.map(elem, targetClass))
                .collect(Collectors.toList());
    }
}