package com.cb.carberus.project.model;

import com.cb.carberus.constants.Role;
import com.cb.carberus.user.dto.UserResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProjectMember {
    @JsonProperty("userDetails")
    private UserResponseDTO userDetails;

    @JsonProperty("projectRole")
    private Role projectRole;
}
