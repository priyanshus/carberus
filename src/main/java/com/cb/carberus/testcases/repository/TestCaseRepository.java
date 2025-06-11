package com.cb.carberus.testcases.repository;

import com.cb.carberus.testcases.model.TestCase;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestCaseRepository extends MongoRepository<TestCase, String> {
    Optional<TestCase> findByTestCaseId(String testCaseId);
}
