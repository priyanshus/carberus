package com.cb.carberus.project.model;

import com.cb.carberus.constants.ProjectRole;
import lombok.Data;

@Data
public class ProjectMember {
    private String userId;
    private ProjectRole projectRole;
}
