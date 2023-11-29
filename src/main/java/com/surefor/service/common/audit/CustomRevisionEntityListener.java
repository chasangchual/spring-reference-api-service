package com.surefor.service.common.audit;

import org.hibernate.envers.RevisionListener;

public class CustomRevisionEntityListener implements RevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity customRevisionEntity = (CustomRevisionEntity) revisionEntity;
        if (UpdateCauseHandler.get() != null) {
            customRevisionEntity.setUpdateCause(UpdateCauseHandler.get());
        } else {
            customRevisionEntity.setUpdateCause(UpdateCause.GENERIC);
        }
    }
}
