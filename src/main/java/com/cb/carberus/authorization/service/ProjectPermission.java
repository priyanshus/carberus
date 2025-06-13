package com.cb.carberus.authorization.service;

import com.cb.carberus.constants.ProjectRole;
import org.springframework.stereotype.Component;

@Component
public class ProjectPermission implements UserPermission<ProjectRole> {
    @Override
    public boolean canAdd(ProjectRole role) {
        return role.equals(ProjectRole.ADMIN);
    }

    @Override
    public boolean canEdit(ProjectRole role) {
        return role.equals(ProjectRole.ADMIN);
    }

    @Override
    public boolean canDelete(ProjectRole role) {
        return role.equals(ProjectRole.ADMIN);
    }

    @Override
    public boolean canView(ProjectRole role) {
        return role.equals(ProjectRole.ADMIN);
    }
}
