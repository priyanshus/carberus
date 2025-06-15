package com.cb.carberus.project.repository;

import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.model.ProjectMember;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends CrudRepository<Project, Long> {
    Optional<Project> findByName(String name);
    Optional<Project> findByProjectCode(String projectCode);
    List<Project> findByMembers_User_Id(Long userId);
}
