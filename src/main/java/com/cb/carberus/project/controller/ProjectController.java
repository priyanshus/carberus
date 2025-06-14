package com.cb.carberus.project.controller;

import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.dto.ProjectMemberDTO;
import com.cb.carberus.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

    @GetMapping("{projectId}/members")
    public ResponseEntity<List<ProjectMemberDTO>> getProjectMembers(@PathVariable String projectId) {
        List<ProjectMemberDTO> projectMemberDTOList = projectService
                .getProjectMembers(projectId);

        return ResponseEntity.ok(projectMemberDTOList);
    }

    @PostMapping("{projectId}/members")
    public ResponseEntity<List<ProjectMemberDTO>> postProjectMember(@PathVariable String projectId,
                                                                    @Valid @RequestBody ProjectMemberDTO projectMemberDTO) {
        List<ProjectMemberDTO> projectMemberDTOList = projectService.addProjectMember(projectId, projectMemberDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMemberDTOList);
    }
}
