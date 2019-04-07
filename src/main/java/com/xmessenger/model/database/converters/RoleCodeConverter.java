package com.xmessenger.model.database.converters;

import com.xmessenger.model.database.entities.Role;

import javax.persistence.AttributeConverter;

public class RoleCodeConverter implements AttributeConverter<Role, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Role role) {
        return role.getCode();
    }

    @Override
    public Role convertToEntityAttribute(Integer integer) {
        return Role.fromCode(integer);
    }
}
