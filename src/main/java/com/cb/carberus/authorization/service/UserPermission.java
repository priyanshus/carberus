package com.cb.carberus.authorization.service;

import com.cb.carberus.constants.UserRole;

public interface UserPermission {
    boolean canAdd(UserRole role);
    boolean canEdit(UserRole role);
    boolean canDelete(UserRole role);
    boolean canView(UserRole role);
}
