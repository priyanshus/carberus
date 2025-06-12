package com.cb.carberus.project.repository;

import com.cb.carberus.project.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String>, ProjectCustomRepository {
    Optional<Project> findByName(String name);
}
