package com.cb.carberus.testcases.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "test_cases")
public class TestCase {
    @Id
    private String id;
    private String testCaseId;
    private String title;
    private String description;
}
