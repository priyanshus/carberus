package com.cb.carberus.project.mapper;

import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.dto.ProjectMemberDTO;
import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.model.ProjectMember;
import com.cb.carberus.user.dto.UserDTO;


public class ProjectMapper {

    public ProjectDTO toProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();

        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        projectDTO.setCreatedAt(project.getCreatedAt());
        projectDTO.setDescription(project.getDescription());

        return projectDTO;
    }

    public ProjectMemberDTO toProjectMemberDTO(ProjectMember projectMember, UserDTO userDTO) {
        ProjectMemberDTO memberDTO = new ProjectMemberDTO();

        memberDTO.setProjectRole(projectMember.getProjectRole());
        memberDTO.setUserDTO(userDTO);

        return memberDTO;
    }


}
