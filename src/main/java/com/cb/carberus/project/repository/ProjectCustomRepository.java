package com.cb.carberus.project.repository;

import com.cb.carberus.project.model.ProjectMember;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectCustomRepository {
    List<ProjectMember> findProjectMembers(String projectId);

}
