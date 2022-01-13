package org.vietsearch.essme.repository.experts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.stereotype.Repository;
import org.vietsearch.essme.model.expert.Expert;

import java.util.List;

@Repository
public class ExpertCustomRepositoryImpl implements ExpertCustomRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Object> getNumberOfExpertsInEachField(){
        GroupOperation groupOperation = new GroupOperation(Fields.fields("research area"));
        GroupOperation.GroupOperationBuilder builder = groupOperation.count();
        groupOperation = builder.as("quantity");
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        groupOperation
                )
                ,
                Expert.class,
                Object.class
        ).getMappedResults();
    }

}
