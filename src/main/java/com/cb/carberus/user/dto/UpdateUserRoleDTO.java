package com.cb.carberus.user.dto;

import com.cb.carberus.constants.Role;
import lombok.Data;

@Data
public class UpdateUserRoleDTO {
    private Role role;
}
