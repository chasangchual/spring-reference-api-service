package com.surefor.service.domain.user.entity;


import com.surefor.service.domain.base.entity.AuditableEntity;
import com.surefor.service.domain.user.UserContactType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class UserEmail extends AuditableEntity {
    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    @Enumerated(EnumType.STRING)
    private UserContactType type;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private Boolean isDeleted;

    @NotNull
    private Boolean isPrimary;
}
