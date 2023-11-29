package com.surefor.service.domain.base.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;


@RequiredArgsConstructor
public abstract class ServiceBase {
    private final ModelMapper modelMapper;

    protected  <T> T mapTo(final Object o, final Class<T> type) {
        return modelMapper.map(o, type);
    }
}
