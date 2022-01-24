package org.vietsearch.essme.repository.experts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;

import org.springframework.stereotype.Repository;
import org.vietsearch.essme.model.expert.Expert;
import org.vietsearch.essme.utils.OpenStreetMapUtils;

import java.util.List;
import java.util.Map;

@Repository
public class ExpertCustomRepositoryImpl implements ExpertCustomRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<Object> getNumberOfExpertsInEachField() {
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

    @Override
    public PageImpl searchByLocationAndText(String what, String where, double radius, Pageable pageable) {
        Query query = new Query();
        radius = radius/6378.0;

        // text search
        if (what != null) {
            TextCriteria criteria = TextCriteria.forDefaultLanguage().caseSensitive(false).matchingPhrase(what);
            query.addCriteria(criteria);
        }

        // cast where to coordinate
        if (where != null) {
            Map<String, Double> coords = OpenStreetMapUtils.getInstance().getCoordinates(where);
            query.addCriteria(Criteria.where("location.features.geometry").withinSphere(new Circle(coords.get("lon"), coords.get("lat"), radius)));
        }


        List<Expert> list = mongoTemplate.find(query, Expert.class);
        return new PageImpl<>(list, pageable, list.size() );
    }

}
