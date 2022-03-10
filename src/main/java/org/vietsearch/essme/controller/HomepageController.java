package org.vietsearch.essme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.vietsearch.essme.model.homepage.Field;
import org.vietsearch.essme.model.homepage.Footer;
import org.vietsearch.essme.model.homepage.Homepage;
import org.vietsearch.essme.repository.event.EventRepository;
import org.vietsearch.essme.repository.HomepageRepository;
import org.vietsearch.essme.repository.news.NewsRepository;
import org.vietsearch.essme.service.expert.ExpertService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/homepage")
public class HomepageController {

    @Resource
    ExpertService expertService;

    @Autowired
    NewsRepository newsRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    HomepageRepository homepageRepository;

    @GetMapping
    public Map<String, ?> get() {
        Homepage fixed = homepageRepository.findFirstBy();
        return Map.of(
                "top_experts", expertService.getTop9ExpertDistinctByRA(),
                "top_news", newsRepository.findAll(PageRequest.of(0, 6)).getContent(),
                "top_events", eventRepository.findAll(PageRequest.of(0, 6)).getContent(),
                "footer", fixed.getFooter(),
                "fields", fixed.getFields()
        );
    }

    @GetMapping("/footer")
    public Footer getFooter() {
        return homepageRepository.findFirstBy().getFooter();
    }

    @GetMapping("/fields")
    public List<Field> getFields() {
        return homepageRepository.findFirstBy().getFields();
    }

    @PutMapping
    public Homepage update(@RequestBody @Valid Homepage data) {
        Homepage homepage = homepageRepository.findFirstBy();
        data.set_id(homepage.get_id());
        return homepageRepository.save(data);
    }
}
