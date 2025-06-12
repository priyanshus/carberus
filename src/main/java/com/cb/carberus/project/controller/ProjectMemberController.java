package com.cb.carberus.project.controller;

import com.cb.carberus.project.model.ProjectMember;
import com.cb.carberus.project.service.ProjectMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/projects")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @Autowired
    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @GetMapping("{projectId}/members")
    public ResponseEntity<List<ProjectMember>> getProjectMembers(@PathVariable String projectId) {
        var members = projectMemberService.getProjectMembers(projectId);
        return ResponseEntity.ok(members);
    }

    @DeleteMapping("{projectId}/members/{memberId}")
    public ResponseEntity<Object> deactivateMember(@PathVariable String projectId, @PathVariable String memberId) {
        projectMemberService.deactivateMember(projectId, memberId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
