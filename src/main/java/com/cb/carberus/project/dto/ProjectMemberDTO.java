package com.cb.carberus.project.dto;

import com.cb.carberus.constants.ProjectRole;
import com.cb.carberus.user.dto.UserDTO;
import lombok.Data;

@Data
public class ProjectMemberDTO {
    private UserDTO userDTO;
    private ProjectRole projectRole;
}
