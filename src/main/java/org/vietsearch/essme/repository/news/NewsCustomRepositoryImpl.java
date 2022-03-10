package org.vietsearch.essme.repository.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.vietsearch.essme.model.News;

import java.util.List;

@Repository
public class NewsCustomRepositoryImpl implements NewsCustomRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public List<String> findDistinctTags() {
        return mongoTemplate.findDistinct(new Query(), "tag", "news", String.class);
    }

    @Override
    public Page<News> findByTextSearchAndTag(String text, String tag, Pageable pageable) {
        Query query = new Query().with(pageable);

        if (text != null && !"".equals(text)) {
            TextCriteria criteria = TextCriteria.forDefaultLanguage().caseSensitive(false).matchingPhrase(text);
            query.addCriteria(criteria);
        }

        if (tag != null && !"".equals(tag)) {
            Criteria tagFilter = Criteria.where("tag").regex(tag);
            query.addCriteria(tagFilter);
        }

        return PageableExecutionUtils.getPage(
                mongoTemplate.find(query, News.class),
                pageable,
                () -> mongoTemplate.count(query.limit(-1).skip(-1), News.class)
        );
    }
}
