package com.cb.carberus.project.mapper;



import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.model.Project;
import com.cb.carberus.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper( ProjectMapper.class );

    @Mapping(source = "createdBy", target = "createdBy")
    ProjectDTO toProjectDTO(Project project);
    Project toProject(AddProjectDTO dto);

    default Long map(User user) {
        return user != null ? user.getId() : null;
    }
}
