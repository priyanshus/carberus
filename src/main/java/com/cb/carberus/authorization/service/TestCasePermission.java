package com.cb.carberus.authorization.service;

import com.cb.carberus.constants.UserRole;
import org.springframework.stereotype.Component;

@Component
public class TestCasePermission implements UserPermission {

    @Override
    public boolean canAdd(UserRole role) {
        return !role.equals(UserRole.VIEWER);
    }

    @Override
    public boolean canEdit(UserRole role) {
        return !role.equals(UserRole.VIEWER);
    }

    @Override
    public boolean canDelete(UserRole role) {
        return !role.equals(UserRole.VIEWER);
    }

    @Override
    public boolean canView(UserRole role) {
        return !role.equals(UserRole.VIEWER);
    }

    public boolean canComment(UserRole role) {
        return !role.equals(UserRole.VIEWER);
    }
}
