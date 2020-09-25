package com.example.authorizationserver.scim.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
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
                .append("members", members)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScimGroupEntity that = (ScimGroupEntity) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(displayName, that.displayName)
                .append(members, that.members)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(displayName)
                .append(members)
                .toHashCode();
    }
}
