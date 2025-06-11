package com.cb.carberus.testcases.controller;

import com.cb.carberus.testcases.dto.TestCaseDTO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/testcases")
public class TestCaseController {

    @GetMapping
    public void getTestCases() {

    }

    @GetMapping("/{testCaseId}")
    public void getTestCase(@Valid @PathVariable String testCaseId) {

    }

    @PostMapping
    public void createTestCase(@Valid @RequestBody TestCaseDTO testCaseDTO) {

    }

    @PutMapping("/{testCaseId}")
    public void updateTestCase(@Valid @RequestBody TestCaseDTO testCaseDTO, @PathVariable String testCaseId) {

    }

    @DeleteMapping("/{testCaseId}")
    public void deleteTestCase(@Valid @PathVariable String testCaseId) {

    }
}
