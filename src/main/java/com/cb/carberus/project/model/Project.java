package com.cb.carberus.project.model;

import com.cb.carberus.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(length = 4, nullable = false)
    private String projectCode;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> members = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if(this.status == null) {
            this.status = ProjectStatus.ACTIVE;
        }

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
