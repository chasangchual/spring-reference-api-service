package com.surefor.service.domain.base.entity;

import java.util.UUID;

public interface PublicallyExposable {
    UUID getPublicId();

    void setPublicId(final UUID publicId);
}
