package com.cb.carberus.authorization.service;

import com.cb.carberus.constants.UserRole;
import org.springframework.stereotype.Component;

@Component
public class AdminPermission implements UserPermission<UserRole> {

    @Override
    public boolean canAdd(UserRole role) {
        return !role.equals(UserRole.ADMIN);
    }

    @Override
    public boolean canEdit(UserRole role) {
        return !role.equals(UserRole.NONADMIN);
    }

    @Override
    public boolean canDelete(UserRole role) {
        return !role.equals(UserRole.NONADMIN);
    }

    @Override
    public boolean canView(UserRole role) {
        return true;
    }

    public boolean canComment(UserRole role) {
        return !role.equals(UserRole.NONADMIN);
    }
}
