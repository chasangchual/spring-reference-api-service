package com.surefor.service.domain.user.entity;

import com.surefor.service.domain.base.entity.AuditableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity
@Audited
public class UserDetail extends AuditableEntity {
    @OneToOne
    @JoinColumn(name = "uid")
    private User user;

    @NotBlank
    @Size(min = 1, max = 128)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 128)
    private String lastName;
}
