package org.vietsearch.essme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vietsearch.essme.model.News;
import org.vietsearch.essme.repository.NewsRepository;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {
    @Autowired
    private NewsRepository newsRepository;

    @GetMapping("/search")
    public Page<News> search(@RequestParam(value = "what", required = false) String what,
                             @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                             @RequestParam(value = "size", defaultValue = "20", required = false) int size) {
        if( what != null){
            TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingPhrase(what);
            List<News> list = newsRepository.findBy(criteria);
            return new PageImpl<>(list, PageRequest.of(page, size), list.size());
        }
        return newsRepository.findAll(PageRequest.of(page, size));
    }

    @GetMapping
    public List<News> getNews(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
                              @RequestParam(value = "size", defaultValue = "20", required = false) int size) {
        Page<News> newsPagePage = newsRepository.findAll(PageRequest.of(page, size));
        return newsPagePage.getContent();
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
