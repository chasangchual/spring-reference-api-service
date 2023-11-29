package com.surefor.service.domain.user.entity;


import com.surefor.service.domain.base.entity.AuditableEntity;
import com.surefor.service.domain.user.UserContactType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity
@Audited
public class UserPhone extends AuditableEntity {
    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    @Enumerated(value = EnumType.STRING)
    private UserContactType type;

    @NotBlank
    @Size(min = 3, max = 3)
    private String countryCode;

    @NotBlank
    @Size(min = 4, max = 16)
    private String phoneNumber;

    @NotNull
    private Boolean isDeleted;

    @NotNull
    private Boolean isPrimary;
}
