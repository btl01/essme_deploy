package org.vietsearch.essme.repository.experts;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.vietsearch.essme.model.ResearchArea;
import org.vietsearch.essme.model.expert.Expert;
import org.vietsearch.essme.repository.ResearchAreaRepository;
import org.vietsearch.essme.utils.OpenStreetMapUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository
public class ExpertCustomRepositoryImpl implements ExpertCustomRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    ResearchAreaRepository researchAreaRepository;

    public List<Object> getNumberOfExpertsInEachField(String lang) {
        String fieldName = "research area";
        if ("en".equals(lang)) {
            fieldName = "research area en";
        }
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.unwind(fieldName),
                        Aggregation.group(fieldName).count().as("quantity"),
                        Aggregation.sort(Sort.Direction.ASC, "_id")
                ),
                Expert.class,
                Object.class
        ).getMappedResults();
    }

    @Override
    public Page<Expert> searchByLocationAndText(String what, String where, double radius, Pageable pageable) {
        Query query = new Query().with(pageable);
        radius = radius / 6378.0;

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

        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Expert.class),
                pageable,
                () -> mongoTemplate.count(query.limit(-1).skip(-1), Expert.class)
        );
    }

    @Override
    public List<Expert> relatedExpertsByField(String field, int limit, int skip) {
        Optional<ResearchArea> optional = researchAreaRepository.findByNameVnOrNameEn(field, field);
        if (optional.isEmpty()) {
            Query query = new Query().limit(limit).skip(skip);
            query.addCriteria(Criteria.where("research area").regex(Pattern.compile(field, Pattern.CASE_INSENSITIVE)));
            return mongoTemplate.find(query, Expert.class);
        } else {
            ResearchArea researchArea = optional.get();
            Query query = this.relatedExpertsByFields(researchArea.getKeys(), limit, skip);
            return mongoTemplate.find(query, Expert.class);
        }
    }

    @Override
    public List<Expert> relatedExpertsByExpert(Expert expert, int limit, int skip) {
        Query query = this.relatedExpertsByFields(expert.getResearchArea(), limit, skip);
        query.addCriteria(Criteria.where("_id").ne(new ObjectId(expert.get_id())));
        return mongoTemplate.find(query, Expert.class);
    }

    private Query relatedExpertsByFields(List<String> fields, int limit, int skip) {
        Query query = new Query().limit(limit).skip(skip);
        List<Pattern> patternList = fields.stream()
                .map(field -> Pattern.compile(field, Pattern.CASE_INSENSITIVE))
                .collect(Collectors.toList());
        query.addCriteria(Criteria.where("research area").elemMatch(new Criteria().in(patternList)));
        return query;
    }

}
