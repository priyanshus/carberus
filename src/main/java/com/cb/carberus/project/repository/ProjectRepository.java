package com.cb.carberus.project.repository;

import com.cb.carberus.project.model.Project;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProjectRepository extends CrudRepository<Project, Long> {
    Optional<Project> findByName(String name);
    Optional<Project> findByProjectCode(String projectCode);
}
