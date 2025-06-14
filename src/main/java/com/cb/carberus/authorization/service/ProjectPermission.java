package com.cb.carberus.authorization.service;

import com.cb.carberus.constants.ProjectRole;
import com.cb.carberus.project.model.ProjectMember;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

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

    public boolean canAddProjectMember(ProjectRole role) {
        if (role == null) return false;
        return EnumSet.of(ProjectRole.MANAGER, ProjectRole.ADMIN).contains(role);
    }
}
