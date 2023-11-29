package com.surefor.service.domain.user.entity;


import com.surefor.service.domain.base.entity.AuditableEntity;
import com.surefor.service.domain.base.entity.PublicallyExposable;
import com.surefor.service.domain.user.UserState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class User extends AuditableEntity implements PublicallyExposable {
    @Column(nullable = false, updatable = false)
    //@Type(type = "pg-uuid")
    private UUID publicId;

    @NotBlank
    @Size(min=8, max=64)
    private String userName;

    @NotBlank
    @Size(min=32, max=1024)
    private String pwhash;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserState state;

    private LocalDateTime lastLogin;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    private UserDetail userDetail;

    @OneToMany(mappedBy = "user")
    private List<UserRole> roles;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<UserEmail> emails;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<UserPhone> phones;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<UserAddress> addresses;

    @Override
    public UUID getPublicId() {
        return publicId;
    }

    @Override
    public void setPublicId(final UUID publicId) {
        this.publicId = publicId;
    }
}
