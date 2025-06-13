package com.cb.carberus.project.repository;

import com.cb.carberus.constants.ProjectRole;
import com.cb.carberus.project.model.ProjectMember;
import com.cb.carberus.project.model.ProjectStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectCustomRepository {
    List<ProjectMember> findProjectMembers(String projectId);
    void updateProjectMemberStatus(String projectId, String memberId, ProjectRole role);

}
