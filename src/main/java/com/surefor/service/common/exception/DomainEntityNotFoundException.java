package com.surefor.service.common.exception;

import jakarta.persistence.EntityNotFoundException;

public class DomainEntityNotFoundException extends EntityNotFoundException {
    private static final long serialVersionUID = 1L;

    public DomainEntityNotFoundException(String entity, String field, Object value) {
        super(String.format("Can not find %s with %s: %s", entity, field, value));
    }

    public DomainEntityNotFoundException(String message) {
        super(message);
    }
}
