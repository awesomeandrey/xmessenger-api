package com.xmessenger.model.database.converters;

import com.xmessenger.model.database.entities.Role;

import javax.persistence.AttributeConverter;
import java.util.HashSet;
import java.util.Set;

public class RoleCodeConverter implements AttributeConverter<Set<Role>, Integer> {
    private final int DEFAULT_ROLE_CODE = Role.USER.getCode();

    @Override
    public Integer convertToDatabaseColumn(Set<Role> roles) {
        if (roles.isEmpty()) return this.DEFAULT_ROLE_CODE;
        return roles.contains(Role.ADMIN) ? Role.ADMIN.getCode() : this.DEFAULT_ROLE_CODE;
    }

    @Override
    public Set<Role> convertToEntityAttribute(Integer roleCode) {
        Set<Role> roles = new HashSet<>();
        if (roleCode.equals(Role.ADMIN.getCode())) {
            roles.add(Role.ADMIN);
        }
        roles.add(Role.USER);
        return roles;
    }
}
