package com.cb.carberus.project.mapper;



import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.dto.ProjectMemberDTO;
import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.model.ProjectMember;
import com.cb.carberus.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper( ProjectMapper.class );

    ProjectDTO toProjectDTO(Project project);
    Project toProject(AddProjectDTO dto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "projectRole", target = "projectRole")
    @Mapping(source = "addedAt", target = "addedAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    ProjectMemberDTO toProjectMemberDTO(ProjectMember projectMember);

    default Long map(User user) {
        return user != null ? user.getId() : null;
    }
}
