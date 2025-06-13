package com.cb.carberus.project.controller;

import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ChangeProjectStatusDTO;
import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.dto.UpdateProjectDTO;
import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getProjects() {
        var projects =  projectService.getProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("{projectId}")
    public ResponseEntity<Project> getProject(@PathVariable String projectId) {
        var project =  projectService.getProject(projectId);
        return ResponseEntity.ok(project);
    }

    @PostMapping
    public ResponseEntity<Void> createProject(@Valid @RequestBody AddProjectDTO addProjectDTO) {
        projectService.addProject(addProjectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateProject(@Valid @RequestBody UpdateProjectDTO updateProjectDTO) {
        projectService.updateProject(updateProjectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("{projectId}")
    public ResponseEntity<Void> changeProjectStatus(
            @PathVariable String projectId,
            @Valid @RequestBody ChangeProjectStatusDTO dto) {

        projectService.changeProjectStatus(projectId, dto);
        return ResponseEntity.noContent().build();
    }
}
