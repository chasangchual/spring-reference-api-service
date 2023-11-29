package com.surefor.service.domain.user.entity;


import com.surefor.service.domain.base.entity.AuditableEntity;
import com.surefor.service.domain.user.UserContactType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
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
public class UserAddress extends AuditableEntity {
    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    @Enumerated(EnumType.STRING)
    private UserContactType type;

    @NotBlank
    @Size(min = 3, max = 3)
    private String countryCode;

    @NotBlank
    @Size(min = 4, max = 128)
    private String address1;

    @Max(value = 128)
    private String address2;

    @Max(value = 64)
    private String city;

    @Max(value = 64)
    private String county;

    @Max(value = 32)
    private String state;

    @NotBlank
    @Size(min = 3, max = 16)
    private String postalCode;

    @NotNull
    private Boolean isDeleted;

    @NotNull
    private Boolean isPrimary;
}
