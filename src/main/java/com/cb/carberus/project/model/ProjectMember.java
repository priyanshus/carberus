package com.cb.carberus.project.model;

import com.cb.carberus.constants.ProjectRole;
import com.cb.carberus.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "project_members")
@IdClass(ProjectMemberId.class)
public class ProjectMember {
    @Id
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_role", nullable = false)
    private ProjectRole projectRole;

    private LocalDateTime addedAt;
    private LocalDateTime updatedAt;
}
