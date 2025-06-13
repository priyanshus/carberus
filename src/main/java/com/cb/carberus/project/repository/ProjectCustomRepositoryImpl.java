package com.cb.carberus.project.repository;

import com.cb.carberus.constants.ProjectRole;
import com.cb.carberus.errorHandler.error.DomainException;
import com.cb.carberus.errorHandler.model.DomainErrorCode;
import com.cb.carberus.project.model.ProjectMember;
import com.cb.carberus.project.model.ProjectStatus;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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


    @Override
    public void updateProjectMemberStatus(String projectId, String memberId, ProjectRole role) {
        Query query = new Query(Criteria
                .where("_id").is(new ObjectId(projectId))
                .and("members.userId").is(new ObjectId(memberId)));

        Update update = new Update();
        update.set("members.$.projectRole", role);
        UpdateResult result = mongoTemplate.updateFirst(query, update, ProjectMember.class);
        if (result.getModifiedCount() == 0) {
            throw new DomainException(DomainErrorCode.PROJECT_MEMBER_NOT_FOUND);
        }
    }


}
