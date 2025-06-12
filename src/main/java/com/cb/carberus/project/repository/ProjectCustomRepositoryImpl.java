package com.cb.carberus.project.repository;

import com.cb.carberus.project.model.ProjectMember;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProjectCustomRepositoryImpl implements ProjectCustomRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ProjectMember> findProjectMembers(String projectId) {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(new ObjectId(projectId))),
                Aggregation.unwind("members"),

                Aggregation.lookup("users", "members.userId", "_id", "userDetails"),

                Aggregation.unwind("userDetails"),
                Aggregation.project()
                        .and("members.projectRole").as("projectRole")
                        .and("userDetails").as("userDetails")
        );

        AggregationResults<ProjectMember> results =
                mongoTemplate.aggregate(agg, "projects", ProjectMember.class);

        return results.getMappedResults();
    }
}
