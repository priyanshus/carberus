package com.cb.carberus.user.dto;

import com.cb.carberus.constants.UserRole;
import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole userRole;
}
