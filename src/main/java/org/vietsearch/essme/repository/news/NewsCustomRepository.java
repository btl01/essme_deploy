package org.vietsearch.essme.repository.news;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vietsearch.essme.model.News;

import java.util.List;

public interface NewsCustomRepository {
    List<String> findDistinctTags();

    Page<News> findByTextSearchAndTag(String text, String tag, Pageable pageable);
}
