package org.vietsearch.essme.service.expert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vietsearch.essme.model.expert.Expert;
import org.vietsearch.essme.repository.experts.ExpertRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpertServiceImpl implements ExpertService{

    @Autowired
    ExpertRepository expertRepository;

    @Override
    public List<Expert> getTop9ExpertDistinctByRA() {
        List<Expert> result = new ArrayList<>();
        List<Expert> list = expertRepository.findByOrderByScoreDesc();
        List<String> fields = new ArrayList<>();
        for (Expert expert : list) {
            if (!fields.contains(expert.getResearchArea())) {
                fields.add(expert.getResearchArea());
                result.add(expert);
                if (result.size() == 9) break;
            }
        }
        return result;
    }
}
