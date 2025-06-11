package com.cb.carberus.project;

import com.cb.carberus.auth.service.AuthUserDetailsService;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.project.service.ProjectService;
import com.cb.carberus.security.jwt.JwtUtil;
import com.cb.carberus.user.controller.UserController;
import com.cb.carberus.user.service.UserService;
import com.cb.carberus.util.TestSecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import(TestSecurityFilter.class)
public class ProjectControllerTest {
    @MockitoBean
    private ProjectService userService;

    @MockitoBean
    private UserContext userContext;

    @MockitoBean
    private AuthUserDetailsService authUserDetailsService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private MockMvc mockMvc;


}
