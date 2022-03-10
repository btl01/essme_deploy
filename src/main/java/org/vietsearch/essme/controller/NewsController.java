package org.vietsearch.essme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.model.News;
import org.vietsearch.essme.repository.news.NewsCustomRepository;
import org.vietsearch.essme.repository.news.NewsRepository;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    @Autowired
    NewsRepository newsRepository;

    @Autowired
    NewsCustomRepository newsCustomRepository;

    @GetMapping("/search")
    public Page<News> searchEvents(@RequestParam(value = "what", required = false) String what,
                                   @RequestParam(value = "tag", required = false) String tag,
                                   @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                   @RequestParam(value = "size", defaultValue = "20", required = false) int size
    ) {
        return newsCustomRepository.findByTextSearchAndTag(what, tag, PageRequest.of(page, size));
    }

    @GetMapping("/tags")
    public List<String> getAllTags() {
        return newsCustomRepository.findDistinctTags();
    }

    @GetMapping
    public List<News> getEvents(@RequestParam(value = "limit", required = false) Integer limit) {
        if (limit == null) {
            return newsRepository.findAll();
        }
        Page<News> eventPage = newsRepository.findAll(PageRequest.of(0, limit));
        return eventPage.getContent();
    }

    @GetMapping("/{id}")
    public News get(@PathVariable String id) {
        return newsRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public News create(@RequestBody News news) {
        return newsRepository.save(news);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable String id) {
        newsRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public News update(@PathVariable String id, @RequestBody News news) {
        newsRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));
        news.setId(id);
        return newsRepository.save(news);
    }
}
