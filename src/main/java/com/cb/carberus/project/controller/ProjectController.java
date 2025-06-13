package com.cb.carberus.project.controller;

import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projectDTOList = projectService.getAllProjects();
        return ResponseEntity.ok(projectDTOList);
    }

    @GetMapping("{projectId}")
    public ResponseEntity<ProjectDTO> getProject(@Valid @PathVariable String projectId) {
        ProjectDTO projectDTOList = projectService.getProject(projectId);
        return ResponseEntity.ok(projectDTOList);
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> addProject(@Valid @RequestBody AddProjectDTO addProjectDTO) {
        ProjectDTO projectDTO = projectService.addProject(addProjectDTO);
        return ResponseEntity.ok(projectDTO);
    }
}
