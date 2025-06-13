package com.cb.carberus.authorization.service;

import com.cb.carberus.constants.Role;

public interface UserPermission <R extends Role> {
    boolean canAdd(R role);
    boolean canEdit(R role);
    boolean canDelete(R role);
    boolean canView(R role);
}
