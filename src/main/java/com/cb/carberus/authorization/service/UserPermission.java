package com.cb.carberus.authorization.service;

import com.cb.carberus.constants.Role;

public interface UserPermission {
    boolean canAdd(Role role);
    boolean canEdit(Role role);
    boolean canDelete(Role role);
    boolean canView(Role role);
}
