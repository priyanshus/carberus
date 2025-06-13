package com.cb.carberus.project.service;

import com.cb.carberus.constants.UserRole;
import com.cb.carberus.errorHandler.error.DomainException;
import com.cb.carberus.errorHandler.model.DomainErrorCode;
import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.model.ProjectMember;
import com.cb.carberus.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMemberService {
    @Autowired
    private final ProjectRepository projectRepository;


    @Autowired
    public ProjectMemberService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<ProjectMember> getProjectMembers(String projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new DomainException(DomainErrorCode.PROJECT_NOT_FOUND)
        );

       return projectRepository.findProjectMembers(projectId);
    }

    public void deactivateMember(String projectId, String memberId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new DomainException(DomainErrorCode.PROJECT_NOT_FOUND));

        boolean updated = false;

//        for (ProjectMember member : project.getMembers()) {
//            if (member.getUserDetails().getId().equals(memberId)) {
//                member.setProjectRole(UserRole.NOACCESS);
//                updated = true;
//                break;
//            }
//        }

        if (!updated) {
            throw new DomainException(DomainErrorCode.PROJECT_MEMBER_NOT_FOUND);
        }

        projectRepository.save(project);
    }
}
