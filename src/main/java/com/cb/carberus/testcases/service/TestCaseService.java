package com.cb.carberus.testcases.service;

import com.cb.carberus.authorization.service.TestCasePermission;
import com.cb.carberus.authorization.service.UserPermission;
import com.cb.carberus.config.UserContext;
import com.cb.carberus.constants.Role;
import com.cb.carberus.errorHandler.error.StandardApiException;
import com.cb.carberus.errorHandler.model.StandardErrorCode;
import com.cb.carberus.testcases.dto.TestCaseDTO;
import com.cb.carberus.testcases.repository.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestCaseService {
    private final TestCaseRepository testCaseRepository;
    private final UserPermission userPermission;
    private final UserContext userContext;

    @Autowired
    public TestCaseService(TestCaseRepository testCaseRepository, TestCasePermission userPermission,
                           UserContext userContext) {
        this.testCaseRepository = testCaseRepository;
        this.userPermission = userPermission;
        this.userContext = userContext;
    }

    private Role currentRole() {
        return userContext.getRole();
    }

    public void addTestCase(TestCaseDTO testCaseDTO) {
        if(userPermission.canAdd(currentRole())) {
            throw new StandardApiException(StandardErrorCode.UNAUTHORIZED);
        }
    }
}
