package com.cb.carberus.project.controller;

import com.cb.carberus.project.dto.AddProjectDTO;
import com.cb.carberus.project.dto.ProjectDTO;
import com.cb.carberus.project.dto.ProjectMemberDTO;
import com.cb.carberus.project.dto.UpdateProjectDTO;
import com.cb.carberus.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Project", description = "Project Management APIs")
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @Operation(
            summary = "Get all projects",
            description = "Returns projects data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects found",
                            content = @Content(schema = @Schema(implementation = ProjectDTO.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projectDTOList = projectService.getAllProjects();
        return ResponseEntity.ok(projectDTOList);
    }

    @Operation(
            summary = "Get project by id",
            description = "Returns project data",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects found",
                            content = @Content(schema = @Schema(implementation = ProjectDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Projects not found",
                            content = @Content(schema = @Schema(implementation = ProjectDTO.class)))
            }
    )
    @GetMapping("{projectId}")
    public ResponseEntity<ProjectDTO> getProject(@Valid @PathVariable String projectId) {
        ProjectDTO projectDTOList = projectService.getProject(projectId);
        return ResponseEntity.ok(projectDTOList);
    }

    @Operation(
            summary = "Add project",
            description = "Returns project data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Projects created",
                            content = @Content(schema = @Schema(implementation = ProjectDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Input",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<ProjectDTO> addProject(@Valid @RequestBody AddProjectDTO addProjectDTO) {
        ProjectDTO projectDTO = projectService.addProject(addProjectDTO);
        return ResponseEntity.ok(projectDTO);
    }


    @Operation(
            summary = "Update a project",
            description = "Returns project data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Projects updated",
                            content = @Content(schema = @Schema(implementation = ProjectDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Input",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("{projectId}")
    public ResponseEntity<ProjectDTO> patchProject(@PathVariable String projectId, @Valid @RequestBody UpdateProjectDTO dto) {
        ProjectDTO projectDTO = projectService.updateProject(projectId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectDTO);
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

    @Operation(
            summary = "Patch member's role in project",
            description = "Returns project data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Updated Role",
                            content = @Content(schema = @Schema(implementation = ProjectMemberDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid Input",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PatchMapping("{projectId}/members")
    public ResponseEntity<List<ProjectMemberDTO>> patchProjectMembers(@PathVariable String projectId,
                                                                    @Valid @RequestBody ProjectMemberDTO projectMemberDTO) {
        List<ProjectMemberDTO> projectMemberDTOList = projectService.updateProjectMemberRole(projectId, projectMemberDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMemberDTOList);
    }
}
