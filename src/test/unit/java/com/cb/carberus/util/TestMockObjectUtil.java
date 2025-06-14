package com.cb.carberus.util;

import com.cb.carberus.constants.ProjectRole;
import com.cb.carberus.constants.UserRole;
import com.cb.carberus.project.model.Project;
import com.cb.carberus.project.model.ProjectMember;
import com.cb.carberus.project.model.ProjectStatus;
import com.cb.carberus.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TestMockObjectUtil {
    public static User getUser() {
        User user = new User();
        user.setId(123L);
        user.setFirstName("firstName");
        user.setLastName("lastName");
        user.setEmail("mail@mail.com");
        user.setUserRole(UserRole.ADMIN);
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);
        user.setPassword("password");
        return user;
    }

    public static User getUser(Long id, String email, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setFirstName("TestFirst");
        user.setLastName("TestLast");
        user.setEmail(email);
        user.setUserRole(role);
        user.setIsActive(true);
        user.setPassword("testpass");
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    public static ProjectMember projectMember() {
        ProjectMember member = new ProjectMember();
        member.setUser(getUser());
        member.setProjectRole(ProjectRole.TESTER);
        member.setAddedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        return member;
    }

    public static ProjectMember projectMember(ProjectRole role, User user) {
        ProjectMember member = new ProjectMember();
        member.setUser(user);
        member.setProjectRole(role);
        member.setAddedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        return member;
    }

    public static Project getProjectWithMember() {
        User creator = getUser(200L, "creator@mail.com", UserRole.ADMIN);
        ProjectMember member = projectMember(ProjectRole.TESTER, getUser(125L, "projectUser@mail.com", UserRole.NONADMIN));

        Project project = new Project();
        project.setId(100L);
        project.setName("project-name");
        project.setDescription("project-desc");
        project.setProjectCode("PRJ1");
        project.setStatus(ProjectStatus.ACTIVE);
        project.setCreatedBy(creator);
        project.setCreatedAt(LocalDateTime.now());
        project.setMembers(new ArrayList<>());

        // Wire both sides of the relationship
        member.setProject(project);
        project.getMembers().add(member);

        return project;
    }

    public static Project getProjectWithMembers(ProjectMember... members) {
        Project project = new Project();
        project.setId(999L);
        project.setName("Test Project");
        project.setDescription("Project with multiple members");
        project.setStatus(ProjectStatus.ACTIVE);
        project.setCreatedBy(getUser(300L, "owner@mail.com", UserRole.ADMIN));
        project.setCreatedAt(LocalDateTime.now());
        project.setProjectCode("PRJ2");
        project.setMembers(new ArrayList<>());

        for (ProjectMember member : members) {
            member.setProject(project);
            project.getMembers().add(member);
        }

        return project;
    }
}
