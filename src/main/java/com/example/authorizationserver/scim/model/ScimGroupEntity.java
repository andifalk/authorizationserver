package com.example.authorizationserver.scim.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class ScimGroupEntity extends ScimResourceEntity {

    @Column(unique = true)
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 255)
    private String displayName;

    @OneToMany(
            mappedBy = "group",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ScimUserGroupEntity> members = new HashSet<>();

    public ScimGroupEntity() {
    }

    public ScimGroupEntity(UUID identifier, String externalId, String displayName, Set<ScimUserGroupEntity> members) {
        super(identifier, externalId);
        this.displayName = displayName;
        this.members = members;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<ScimUserGroupEntity> getMembers() {
        return members;
    }

    public void setMembers(Set<ScimUserGroupEntity> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("displayName", displayName)
                .toString();
    }
}
