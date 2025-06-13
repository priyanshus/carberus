package com.cb.carberus.authorization.service;

import com.cb.carberus.constants.UserRole;
import org.springframework.stereotype.Component;

@Component
public class ProjectPermission implements UserPermission{
    @Override
    public boolean canAdd(UserRole role) {
        return role.equals(UserRole.ADMIN);
    }

    @Override
    public boolean canEdit(UserRole role) {
        return role.equals(UserRole.ADMIN);
    }

    @Override
    public boolean canDelete(UserRole role) {
        return role.equals(UserRole.ADMIN);
    }

    @Override
    public boolean canView(UserRole role) {
        return role.equals(UserRole.ADMIN);
    }
}
