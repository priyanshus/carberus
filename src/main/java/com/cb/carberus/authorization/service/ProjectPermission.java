package com.cb.carberus.authorization.service;

import com.cb.carberus.constants.Role;
import org.springframework.stereotype.Component;

@Component
public class ProjectPermission implements UserPermission{
    @Override
    public boolean canAdd(Role role) {
        return role.equals(Role.ADMIN);
    }

    @Override
    public boolean canEdit(Role role) {
        return role.equals(Role.ADMIN);
    }

    @Override
    public boolean canDelete(Role role) {
        return role.equals(Role.ADMIN);
    }

    @Override
    public boolean canView(Role role) {
        return role.equals(Role.ADMIN);
    }
}
