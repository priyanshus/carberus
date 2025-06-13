package com.cb.carberus.user.dto;

import com.cb.carberus.constants.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRoleDTO {
    @NotNull
    private UserRole userRole;
}
