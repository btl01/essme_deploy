package org.vietsearch.essme.repository.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.vietsearch.essme.model.event.Event;
import org.vietsearch.essme.utils.OpenStreetMapUtils;

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
    public Page<Event> searchByTextAndLocation(String text, String location, Pageable pageable) {
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

        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Event.class),
                pageable,
                () -> mongoTemplate.count(query.limit(-1).skip(-1), Event.class)
        );
    }
}
