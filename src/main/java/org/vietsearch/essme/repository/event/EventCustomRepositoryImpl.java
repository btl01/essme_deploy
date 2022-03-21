package org.vietsearch.essme.repository.event;

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
import org.vietsearch.essme.model.event.Event;
import org.vietsearch.essme.utils.OpenStreetMapUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Repository
public class EventCustomRepositoryImpl implements EventCustomRepository {

    final
    MongoTemplate mongoTemplate;

    public EventCustomRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Event> searchEvents(String text, String location, List<String> types, List<String> tags, String lang, Pageable pageable) {
        Query query = new Query().with(pageable);

        if (text != null && !"".equals(text)) {
            TextCriteria criteria = TextCriteria.forDefaultLanguage().caseSensitive(false).matchingPhrase(text);
            query.addCriteria(criteria);
        }

        if (location != null && !"".equals(location)) {
            Criteria isContain = Criteria.where("location").regex(Pattern.compile(location, Pattern.CASE_INSENSITIVE));
            Map<String, Double> coords = OpenStreetMapUtils.getInstance().getCoordinates(location);
            Criteria isNear = Criteria.where("geojson.geometry").withinSphere(new Circle(coords.get("lon"), coords.get("lat"), 6 / 6378.0));
            query.addCriteria(new Criteria().orOperator(isContain, isNear));
        }

        if (types != null && !types.isEmpty()) {
            String fieldName = "vi".equals(lang) ? "type_vn" : "type_en";
            query.addCriteria(Criteria.where(fieldName).all(types));
        }

        if (tags != null && !tags.isEmpty()) {
            String fieldName = "vi".equals(lang) ? "tags_vn" : "tags_en";
            query.addCriteria(Criteria.where(fieldName).all(tags));
        }

        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Event.class),
                pageable,
                () -> mongoTemplate.count(query.limit(-1).skip(-1), Event.class)
        );
    }

    @Override
    public List<Object> countType(String lang) {
        String fieldName = "vi".equals(lang) ? "type_vn" : "type_en";
        return countArrayField(fieldName);
    }

    @Override
    public List<Object> countTag(String lang) {
        String fieldName = "vi".equals(lang) ? "tags_vn" : "tags_en";
        return countArrayField(fieldName);
    }

    private List<Object> countArrayField(String fieldName) {
        return mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        Aggregation.unwind(fieldName),
                        Aggregation.group(fieldName).count().as("quantity"),
                        Aggregation.sort(Sort.Direction.ASC, "_id")
                ),
                Event.class,
                Object.class
        ).getMappedResults();
    }
}
