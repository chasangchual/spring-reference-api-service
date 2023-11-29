package com.surefor.service.domain.user.entity;


import com.surefor.service.domain.base.entity.AuditableEntity;
import com.surefor.service.domain.user.UserRoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class UserRole extends AuditableEntity {
    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    @Enumerated(EnumType.STRING)
    private UserRoleType role;

    @NotNull
    private Boolean isDeleted;
}
